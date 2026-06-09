package com.task.mgmt.tracker.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.task.mgmt.tracker.constant.AppConstant;
import com.task.mgmt.tracker.constant.RoleType;
import com.task.mgmt.tracker.constant.Status;
import com.task.mgmt.tracker.constant.TaskDesignation;
import com.task.mgmt.tracker.constant.TaskStatus;
import com.task.mgmt.tracker.entity.Member;
import com.task.mgmt.tracker.entity.Project;
import com.task.mgmt.tracker.entity.Task;
import com.task.mgmt.tracker.entity.TaskAssignment;
import com.task.mgmt.tracker.entity.TaskStatusTrack;
import com.task.mgmt.tracker.entity.Team;
import com.task.mgmt.tracker.exception.AppException;
import com.task.mgmt.tracker.exception.NotFoundException;
import com.task.mgmt.tracker.repository.MemberRepository;
import com.task.mgmt.tracker.repository.TaskRepository;
import com.task.mgmt.tracker.repository.TaskStatusTrackRepository;
import com.task.mgmt.tracker.response.payload.DesignationWiseReportStatus;
import com.task.mgmt.tracker.response.payload.ReportResponseDto;
import com.task.mgmt.tracker.response.payload.TaskReportResponseDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class ReportService {

	@Autowired
	EntityManager entityManager;

	@Autowired
	MemberRepository memberRepo;

	@Autowired
	TaskRepository taskRepo;

	@Autowired
	TaskStatusTrackRepository taskStatusTrackRepo;

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportService.class);

	public List<TaskReportResponseDto> getTaskWorkReportFiltered(String loggedUser, String teamId, String memberId,
			LocalDate startDate, LocalDate endDate) {
		try {
			Member member = memberRepo.findByIdAndStatus(loggedUser, Status.ACTIVE.name());
			if (member == null) {
				throw new NotFoundException("Member not found or inactive");
			}

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<TaskAssignment> query = cb.createQuery(TaskAssignment.class);
			Root<TaskAssignment> root = query.from(TaskAssignment.class);

			Join<TaskAssignment, Member> memberJoin = root.join("assignedTo", JoinType.LEFT);
			Join<TaskAssignment, Task> taskJoin = root.join("task", JoinType.LEFT);
			Join<Task, Project> projectJoin = taskJoin.join("project", JoinType.LEFT);
			Join<Project, Team> teamJoin = projectJoin.join("teams", JoinType.LEFT);

			List<Predicate> predicates = new ArrayList<>();

			if (teamId != null && !teamId.isBlank()) {
				predicates.add(cb.equal(teamJoin.get("id"), teamId));
			}

			if (RoleType.EMPLOYEE.name().equalsIgnoreCase(member.getRole())) {
				predicates.add(cb.equal(memberJoin.get("id"), member.getId()));
			}

			if (memberId != null && !memberId.isBlank()) {
				predicates.add(cb.equal(memberJoin.get("id"), memberId));
			}

			if (startDate != null && endDate != null) {
				predicates.add(cb.greaterThanOrEqualTo(taskJoin.get("startDate"), startDate.atStartOfDay()));
				predicates.add(cb.lessThanOrEqualTo(taskJoin.get("endDate"), endDate.atTime(23, 59, 59)));
			}
			predicates.add(cb.equal(taskJoin.get("status"), TaskStatus.DONE.name()));
			query.where(cb.and(predicates.toArray(new Predicate[0])));
			query.orderBy(cb.desc(taskJoin.get("startDate")));

			List<TaskAssignment> results = entityManager.createQuery(query).getResultList();

			List<TaskReportResponseDto> response = results.stream().map(this::convertToTaskReportDto)
					.filter(dto -> dto.getTeamId() != null)
					.filter(dto -> teamId == null || teamId.isBlank() || dto.getTeamId().equals(teamId))
					.sorted(Comparator.comparing(TaskReportResponseDto::getTeamName)
							.thenComparingLong(TaskReportResponseDto::getTeamOrder))
					.collect(Collectors.toList());

			LOGGER.info("Task report generated. Total entries: {}", response.size());
			return response;

		} catch (Exception e) {
			LOGGER.error("Failed to fetch task report", e);
			throw new AppException(e.getMessage());
		}
	}

	public TaskReportResponseDto convertToTaskReportDto(TaskAssignment assignment) {
		Task task = assignment.getTask();
		Member member = assignment.getAssignedTo();
		Project project = task.getProject();

		TaskReportResponseDto dto = new TaskReportResponseDto();

		if (project.getTeams() != null) {
			for (Team team : project.getTeams()) {
				List<Member> members = team.getMembers();
				if (members != null) {
					for (int i = 0; i < members.size(); i++) {
						if (members.get(i).getId().equals(member.getId())) {
							dto.setTeamId(team.getId());
							dto.setTeamName(team.getTeamName());
							dto.setTeamOrder(i);
							break;
						}
					}
				}
				if (dto.getTeamId() != null)
					break;
			}
		}

		TaskReportResponseDto.TaskMemberWorkInfo workInfo = new TaskReportResponseDto.TaskMemberWorkInfo();
		workInfo.setDate(assignment.getStageStart());
		workInfo.setMemberId(member.getId());
		workInfo.setMemberName(member.getFirstName() + " " + member.getLastName());
		workInfo.setDesignation(assignment.getDesignation());
		workInfo.setTaskId(task.getId());
		workInfo.setTaskTitle(task.getTitle());

		Integer estimated = assignment.getEstimatedTime();
		Integer actual = assignment.getActualSpentTime();

		workInfo.setEstimatedTime(estimated + " mins");
		workInfo.setActualSpentTime(actual + " mins");

		double efficiency = calculateEfficiency(actual, estimated);
		workInfo.setPercentage(String.format("%.2f%%", efficiency));

		dto.setWorkInfo(workInfo);
		return dto;
	}

	private double calculateEfficiency(long actualTime, long estimatedTime) {
		if (actualTime == 0) {
			return 0;
		}
		double efficiency = (estimatedTime / (double) actualTime) * AppConstant.APP_PRODUCTION_TIME;
		return Math.max(efficiency, 0);
	}

	public ReportResponseDto generateTaskReport(LocalDate startDate, LocalDate endDate, String loggedUser) {
		try {
			LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay()
					: LocalDate.now().minusDays(10).atStartOfDay();
			LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : LocalDateTime.now().plusDays(1);

			Member member = memberRepo.findByIdAndStatus(loggedUser, Status.ACTIVE.name());
			if (member == null)
				throw new AppException("Invalid user");

			List<TaskStatusTrack> taskStatusTracks = new ArrayList<>();

			if (member.getRole().equals(RoleType.EMPLOYEE.name())) {
				taskStatusTracks = taskStatusTrackRepo.findByMemberIdAndChangedTimeBetween(loggedUser, startDateTime,
						endDateTime);
			} else if (member.getRole().equals(RoleType.MANAGER.name())) {
				taskStatusTracks = taskStatusTrackRepo.findByMemberIdOrTask_AssignedBy_IdAndChangedTimeBetween(
						loggedUser, loggedUser, startDateTime, endDateTime);
			} else {
				throw new AppException("Unsupported role: " + member.getRole());
			}

			Set<Task> uniqueTasks = taskStatusTracks.stream().map(TaskStatusTrack::getTask).filter(Objects::nonNull)
					.collect(Collectors.toSet());

			return populateReport(uniqueTasks);

		} catch (Exception e) {
			LOGGER.error("Error generating task report: {}", e.getMessage(), e);
			throw new AppException(e.getMessage());
		}
	}

	private ReportResponseDto populateReport(Set<Task> tasks) {
		try {
			ReportResponseDto report = new ReportResponseDto();

			Map<String, Long> tasksByStatus = tasks.stream()
					.collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));

			int totalTasks = tasks.size();

			report.setTasksInTodo(tasksByStatus.getOrDefault(TaskStatus.TODO.name(), 0L).intValue());
			report.setTasksInDesign(tasksByStatus.getOrDefault(TaskStatus.DESIGN.name(), 0L).intValue());
			report.setTasksInDevelopment(tasksByStatus.getOrDefault(TaskStatus.DEVELOPMENT.name(), 0L).intValue());
			report.setTasksInTest(tasksByStatus.getOrDefault(TaskStatus.TESTING.name(), 0L).intValue());
			report.setTasksInDone(tasksByStatus.getOrDefault(TaskStatus.DONE.name(), 0L).intValue());

			report.setTasksInTodoPercentage(calculatePercentage(report.getTasksInTodo(), totalTasks));
			report.setTasksInDesignPercentage(calculatePercentage(report.getTasksInDesign(), totalTasks));
			report.setTasksInDevelopmentPercentage(calculatePercentage(report.getTasksInDevelopment(), totalTasks));
			report.setTasksInTestPercentage(calculatePercentage(report.getTasksInTest(), totalTasks));
			report.setTasksInDonePercentage(calculatePercentage(report.getTasksInDone(), totalTasks));

			return report;
		} catch (Exception e) {
			LOGGER.error("Error populating task report: {}", e.getMessage(), e);
			throw new AppException("Error populating task report: " + e.getMessage());
		}
	}

	private float calculatePercentage(long count, long totalCount) {
		if (totalCount == 0)
			return 0f;
		float percentage = ((float) count / totalCount) * 100;
		return Float.parseFloat(String.format("%.2f", percentage));
	}

	public DesignationWiseReportStatus getDesignationWiseReportStatus(String loggedUser, String designation) {
		try {
			List<Member> allActiveMembers = memberRepo.findAllByStatus(Status.ACTIVE.name());

			boolean isManager = allActiveMembers.stream()
					.anyMatch(m -> loggedUser.equals(m.getId()) && "MANAGER".equalsIgnoreCase(m.getRole()));

			long designerCount = allActiveMembers.stream()
					.filter(m -> TaskDesignation.DESIGNER.name().equalsIgnoreCase(m.getDesignation())).count();

			long developerCount = allActiveMembers.stream()
					.filter(m -> TaskDesignation.DEVELOPER.name().equalsIgnoreCase(m.getDesignation())).count();

			long testerCount = allActiveMembers.stream()
					.filter(m -> TaskDesignation.TESTER.name().equalsIgnoreCase(m.getDesignation())).count();

			List<Member> filteredMembers = isManager
					? allActiveMembers.stream().filter(m -> !loggedUser.equals(m.getId())).collect(Collectors.toList())
					: allActiveMembers.stream().filter(m -> loggedUser.equals(m.getId())).collect(Collectors.toList());

			List<TaskAssignment> assignments = taskRepo.findAllDoneAssignments();

			Map<String, List<TaskAssignment>> tasksByMember = assignments.stream()
					.filter(a -> a.getAssignedTo() != null)
					.collect(Collectors.groupingBy(a -> a.getAssignedTo().getId()));

			List<DesignationWiseReportStatus.MemberEfficiencyDto> efficiencyList = new ArrayList<>();

			for (Member member : filteredMembers) {
				if (designation != null && !designation.isBlank()
						&& !designation.equalsIgnoreCase(member.getDesignation())) {
					continue;
				}

				String memberId = member.getId();
				List<TaskAssignment> tasks = tasksByMember.getOrDefault(memberId, List.of());

				long actualTime = tasks.stream().mapToLong(t -> Optional.ofNullable(t.getActualSpentTime()).orElse(0))
						.sum();

				long estimationTime = tasks.stream().mapToLong(t -> Optional.ofNullable(t.getEstimatedTime()).orElse(0))
						.sum();

				String efficiency = String.format("%.2f%%", calculateEfficiency(actualTime, estimationTime));

				DesignationWiseReportStatus.MemberEfficiencyDto dto = new DesignationWiseReportStatus.MemberEfficiencyDto();
				dto.setMemberId(memberId);
				dto.setActualTime(actualTime);
				dto.setEstimationTime(estimationTime);
				dto.setEfficiency(efficiency);

				efficiencyList.add(dto);
			}

			DesignationWiseReportStatus report = new DesignationWiseReportStatus();

			DesignationWiseReportStatus.Designer designerObj = new DesignationWiseReportStatus.Designer();
			designerObj.setTotalCount(designerCount);
			report.setDesigner(designerObj);

			DesignationWiseReportStatus.Developer developerObj = new DesignationWiseReportStatus.Developer();
			developerObj.setTotalCount(developerCount);
			report.setDeveloper(developerObj);

			DesignationWiseReportStatus.Tester testerObj = new DesignationWiseReportStatus.Tester();
			testerObj.setTotalCount(testerCount);
			report.setTester(testerObj);

			report.setMembers(efficiencyList);

			return report;

		} catch (Exception e) {
			LOGGER.error("Error populating DesignationWiseReportStatus: {}", e.getMessage(), e);
			throw new AppException("Error populating Designation Wise Report Status");
		}
	}


}
