package com.task.mgmt.tracker.service;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.task.mgmt.tracker.constant.BugStatus;
import com.task.mgmt.tracker.constant.RoleType;
import com.task.mgmt.tracker.constant.Status;
import com.task.mgmt.tracker.constant.TaskDesignation;
import com.task.mgmt.tracker.constant.TaskStatus;
import com.task.mgmt.tracker.constant.WorkStatus;
import com.task.mgmt.tracker.entity.Activity;
import com.task.mgmt.tracker.entity.Attachment;
import com.task.mgmt.tracker.entity.Comments;
import com.task.mgmt.tracker.entity.Member;
import com.task.mgmt.tracker.entity.Project;
import com.task.mgmt.tracker.entity.Tag;
import com.task.mgmt.tracker.entity.Task;
import com.task.mgmt.tracker.entity.TaskAssignment;
import com.task.mgmt.tracker.entity.TaskStatusTrack;
import com.task.mgmt.tracker.entity.Team;
import com.task.mgmt.tracker.exception.AppException;
import com.task.mgmt.tracker.exception.NotFoundException;
import com.task.mgmt.tracker.repository.ActivityRepository;
import com.task.mgmt.tracker.repository.CommentsRepository;
import com.task.mgmt.tracker.repository.MemberRepository;
import com.task.mgmt.tracker.repository.NotificationRepository;
import com.task.mgmt.tracker.repository.ProjectRepository;
import com.task.mgmt.tracker.repository.TaskRepository;
import com.task.mgmt.tracker.repository.TaskStatusTrackRepository;
import com.task.mgmt.tracker.repository.TeamRepository;
import com.task.mgmt.tracker.request.payload.ActivityRequestDto;
import com.task.mgmt.tracker.request.payload.AssigneeDto;
import com.task.mgmt.tracker.request.payload.CommentsRequestDto;
import com.task.mgmt.tracker.request.payload.CreateTaskRequestDto;
import com.task.mgmt.tracker.request.payload.TaskStatusChangeDto;
import com.task.mgmt.tracker.request.payload.UpdateCommentsDo;
import com.task.mgmt.tracker.request.payload.UpdateTaskRequestDto;
import com.task.mgmt.tracker.request.payload.WorkStatusUpdateDto;
import com.task.mgmt.tracker.response.payload.CommonActivityResponseDto;
import com.task.mgmt.tracker.response.payload.CommonAssigneeResponseDto;
import com.task.mgmt.tracker.response.payload.CommonAttachmentResponseDto;
import com.task.mgmt.tracker.response.payload.CommonBugResponseDto;
import com.task.mgmt.tracker.response.payload.CommonCommentResponseDto;
import com.task.mgmt.tracker.response.payload.CommonMemberResponseDto;
import com.task.mgmt.tracker.response.payload.CommonProjectResponseDto;
import com.task.mgmt.tracker.response.payload.CommonTaskStatusTrackResponseDto;
import com.task.mgmt.tracker.response.payload.CommonTeamResponseDto;
import com.task.mgmt.tracker.response.payload.DetailedTaskResponseDto;
import com.task.mgmt.tracker.response.payload.FirstPageTaskResponseDto;
import com.task.mgmt.tracker.response.payload.ProjectResponseDto;
import com.task.mgmt.tracker.response.payload.TaskResponseDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Service
public class TaskService {

	@Autowired
	MemberRepository memberRepo;

	@Autowired
	ProjectRepository projectRepo;

	@Autowired
	TeamRepository teamRepo;

	@Autowired
	TaskRepository taskRepo;

	@Autowired
	CommentsRepository commentsRepo;

	@Autowired
	ActivityRepository activityRepo;

	@Autowired
	TaskStatusTrackRepository taskStatusTrackRepo;

	@Autowired
	EntityManager entityManager;

	@Autowired
	NotificationService notifyService;

	@Autowired
	NotificationRepository notificationRepo;

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

	private static final String SPECIFIC_USER_TOPIC = "/reply";

	public DetailedTaskResponseDto createTask(String managerId, CreateTaskRequestDto taskRequestDto) {
		try {
			Member manager = validMemberCheck(managerId, "creating task", RoleType.MANAGER.name());

			Project project = projectRepo.findByIdAndStatus(taskRequestDto.getProjectId(), Status.ACTIVE.name());
			if (project == null)
				throw new NotFoundException("Project id not found");

			List<Member> assignees = new ArrayList<>();
			if (taskRequestDto.getAssignedTo() != null && !taskRequestDto.getAssignedTo().isEmpty()) {
				boolean hasDesigner = false;
				boolean hasDeveloper = false;
				boolean hasTester = false;

				for (AssigneeDto dto : taskRequestDto.getAssignedTo()) {
					Member m = project.getTeams().stream().flatMap(t -> t.getMembers().stream())
							.filter(mem -> mem.getId().equals(dto.getMemberId())).findFirst().orElseThrow(
									() -> new NotFoundException("Assigned member not found: " + dto.getMemberId()));

					switch (m.getDesignation().toLowerCase()) {
					case "designer" -> hasDesigner = true;
					case "developer" -> hasDeveloper = true;
					case "tester" -> hasTester = true;
					}

					assignees.add(m);
				}

				if (!hasDesigner || !hasDeveloper || !hasTester) {
					throw new AppException("At least one Designer, Developer, and Tester must be assigned.");
				}
			}

			Set<Tag> tags = new HashSet<>();
			if (taskRequestDto.getTags() != null && !taskRequestDto.getTags().isEmpty()) {
				tags = project.getTags().stream().filter(tag -> taskRequestDto.getTags().contains(tag.getId()))
						.collect(Collectors.toSet());
				if (tags.size() != taskRequestDto.getTags().size())
					throw new NotFoundException("One or more tags not found in project");
			}
			if (taskRequestDto.getStartDate() != null && taskRequestDto.getEndDate() != null) {
				if (taskRequestDto.getStartDate().equals(taskRequestDto.getEndDate())) {
					LOGGER.error(
							"Exception occurred while creating task. Start date and end date were the same. Task Created By ID: {}, Start date: {}, End date: {}",
							manager.getId(), taskRequestDto.getStartDate(), taskRequestDto.getEndDate());
					throw new NotFoundException("Start date and end date was same.");
				} else if (taskRequestDto.getStartDate().isAfter(taskRequestDto.getEndDate())) {
					LOGGER.error(
							"Exception occurred while creating task. Start date cannot be after end date. Task Created By ID: {}, Start date: {}, End date: {}",
							manager.getId(), taskRequestDto.getStartDate(), taskRequestDto.getEndDate());
					throw new NotFoundException("Start date cannot be after end date.");
				}
			}
			Task task = new Task();
			task.setTitle(taskRequestDto.getTitle());
			task.setShortDescription(taskRequestDto.getShortDescription());
			task.setDescription(taskRequestDto.getDescription());
			task.setProject(project);
			task.setStatus(TaskStatus.TODO.name());
			task.setCreatedBy(manager);
			task.setModifiedBy(manager);
			task.setCreatedDate(LocalDateTime.now());
			task.setModifiedDate(LocalDateTime.now());
			task.setPriority(taskRequestDto.getPriority());
			task.setComplexity(taskRequestDto.getComplexity());
			task.setActive(true);
			task.setTags(tags);
			task.setAssignedBy(manager);
			if (taskRequestDto.getStartDate() != null) {
				task.setStartDate(taskRequestDto.getStartDate());
			}
			if (taskRequestDto.getEndDate() != null) {
				task.setEndDate(taskRequestDto.getEndDate());
			}
			List<TaskAssignment> assignments = new ArrayList<>();
			if (taskRequestDto.getAssignedTo() != null && !taskRequestDto.getAssignedTo().isEmpty()) {
				for (AssigneeDto dto : taskRequestDto.getAssignedTo()) {

					Member assignee = project.getTeams().stream().flatMap(team -> team.getMembers().stream())
							.filter(m -> m.getId().equals(dto.getMemberId())).findFirst().get();

					TaskAssignment taskAssignment = new TaskAssignment();
					taskAssignment.setAssignedTo(assignee);
					taskAssignment.setAssignedBy(manager);
					taskAssignment.setCreatedAt(LocalDateTime.now());
					taskAssignment.setStageStart(LocalDateTime.now());
					taskAssignment.setStage(TaskStatus.TODO.name());
					taskAssignment.setEstimatedTime(dto.getEstimatedTime());
					taskAssignment.setDesignation(assignee.getDesignation());

					taskAssignment.setTask(task);
					assignments.add(taskAssignment);
				}
			}
			task.setAssignments(assignments);
			TaskStatusTrack track = new TaskStatusTrack();
			track.setTask(task);
			track.setMember(manager);
			track.setChangedStatus(TaskStatus.TODO.name());
			track.setChangedTime(LocalDateTime.now());
			track.setMessage("created this task under TODO");
			task.getTaskStatusTrack().add(track);

			Task savedTask = taskRepo.save(task);

			if (!assignees.isEmpty()) {
				for (Member assignee : assignees) {
					if (!assignee.getId().equals(manager.getId())) {
						String msg = "assigned this task \"" + savedTask.getId() + "\" to you";
						notifyService.sendNotification(assignee.getId(), manager.getId(), SPECIFIC_USER_TOPIC, msg);
					}
				}
				notifyService.sendNotificationAll();
			}

			ActivityRequestDto activity = new ActivityRequestDto();
			createActivity("added this task to TODO", manager, activity, savedTask);
			return convertTaskToDto(savedTask);
		} catch (Exception e) {
			LOGGER.error("Exception occurred while creating task: {}", e.getMessage(), e);
			throw new AppException("Failed to create task: " + e.getMessage());
		}
	}

	public DetailedTaskResponseDto updateTask(String taskId, String memberId, UpdateTaskRequestDto dto) {
		try {
			Task task = taskRepo.findByIdAndIsActiveAndCreatedById(taskId, true, memberId);
			if (task == null)
				throw new NotFoundException("Task not found or you don't have permission");
			if (!task.getStatus().equalsIgnoreCase(TaskStatus.TODO.name()))
				throw new AppException("You cannot update this task because it is not in TODO stage");

			if (dto.getStartDate() != null && dto.getEndDate() != null) {
				if (dto.getStartDate().equals(dto.getEndDate())) {
					throw new AppException("Start date and end date is same.");
				}

				if (dto.getStartDate().isAfter(dto.getEndDate())) {
					throw new AppException("Start date cannot be after end date.");
				}

				LocalDateTime currentTime = LocalDateTime.now();
				if (dto.getEndDate().isBefore(currentTime) || dto.getEndDate().isEqual(currentTime)) {
					throw new AppException("End date must be in the future.");
				}
			}
			Member actor = task.getCreatedBy();
			ActivityRequestDto activity = new ActivityRequestDto();
			boolean fieldChanged = false;

			if (dto.getTags() != null && !dto.getTags().isEmpty()) {
				Set<String> newTagIds = new HashSet<>(dto.getTags());
				Set<String> projectTagIds = task.getProject().getTags().stream().map(Tag::getName)
						.collect(Collectors.toSet());

				Set<String> invalidTagIds = newTagIds.stream().filter(id -> !projectTagIds.contains(id))
						.collect(Collectors.toSet());

				if (!invalidTagIds.isEmpty()) {
					throw new AppException("Invalid tag ID(s): " + String.join(", ", invalidTagIds));
				}

				Set<String> existingTagIds = task.getTags() != null
						? task.getTags().stream().map(Tag::getName).collect(Collectors.toSet())
						: new HashSet<>();

				if (!newTagIds.equals(existingTagIds)) {
					activity.setTags(dto.getTags());
					fieldChanged = true;

					Set<Tag> updatedTags = task.getProject().getTags().stream()
							.filter(tag -> newTagIds.contains(tag.getName())).collect(Collectors.toSet());

					task.setTags(updatedTags);
				}
			}
			if (dto.getTitle() != null && !dto.getTitle().equals(task.getTitle())) {
				activity.setTitle(dto.getTitle());
				fieldChanged = true;
				task.setTitle(dto.getTitle());
			}

			if (dto.getShortDescription() != null && !dto.getShortDescription().equals(task.getShortDescription())) {
				activity.setShortDescription(dto.getShortDescription());
				fieldChanged = true;
				task.setShortDescription(dto.getShortDescription());
			}

			if (dto.getDescription() != null && !dto.getDescription().equals(task.getDescription())) {
				activity.setDescription(dto.getDescription());
				fieldChanged = true;
				task.setDescription(dto.getDescription());
			}

			if (dto.getPriority() != null && !dto.getPriority().equals(task.getPriority())) {
				activity.setBeforePriority(task.getPriority());
				activity.setPriority(dto.getPriority());
				fieldChanged = true;
				task.setPriority(dto.getPriority());
			}

			if (dto.getComplexity() > 0 && dto.getComplexity() != task.getComplexity()) {
				activity.setBeforeTaskComplexity(task.getComplexity());
				activity.setComplexity(dto.getComplexity());
				fieldChanged = true;
				task.setComplexity(dto.getComplexity());
			}

			if (dto.getAssignedTo() != null) {
				List<TaskAssignment> changedAssignments = updateAssignees(task, dto.getAssignedTo());
				if (!changedAssignments.isEmpty()) {
					activity.setAssignedTo(changedAssignments.stream().map(a -> {
						Member m = a.getAssignedTo();
						return m.getFirstName() + " " + m.getLastName() + " -- " + a.getEstimatedTime() + "mins";
					}).collect(Collectors.toList()));
					fieldChanged = true;
				}
			}

			if (dto.getStartDate() != null && !dto.getStartDate().isEqual(task.getStartDate())) {
				activity.setBeforeStartDate(task.getStartDate());
				activity.setStartDate(dto.getStartDate());
				fieldChanged = true;
				task.setStartDate(dto.getStartDate());
			}
			if (dto.getEndDate() != null && !dto.getEndDate().isEqual(task.getEndDate())) {
				activity.setBeforeEndDate(task.getEndDate());
				activity.setEndDate(dto.getEndDate());
				fieldChanged = true;
				task.setEndDate(dto.getEndDate());
			}

			task.setModifiedBy(actor);
			task.setModifiedDate(LocalDateTime.now());

			if (!fieldChanged)
				return convertTaskToDto(task);

			Task saved = taskRepo.save(task);

			if (fieldChanged) {
				activity.setReason(dto.getReason());
				createActivity("updated the", actor, activity, task);
			}

			return convertTaskToDto(saved);

		} catch (NotFoundException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException(e.getMessage());
		}
	}

	private List<TaskAssignment> updateAssignees(Task task, List<AssigneeDto> assigneeDtos) {
		if (assigneeDtos == null)
			return Collections.emptyList();

		Project project = task.getProject();

		Map<String, Integer> oldAssignments = task.getAssignments().stream()
				.collect(Collectors.toMap(a -> a.getAssignedTo().getId(), TaskAssignment::getEstimatedTime));

		List<TaskAssignment> changedAssignments = new ArrayList<>();

		task.getAssignments().clear();

		for (AssigneeDto dto : assigneeDtos) {
			Member member = project.getTeams().stream().flatMap(team -> team.getMembers().stream())
					.filter(m -> m.getId().equals(dto.getMemberId())).findFirst().orElseThrow(
							() -> new NotFoundException("Assigned member not found in project: " + dto.getMemberId()));

			TaskAssignment assignment = new TaskAssignment();
			assignment.setTask(task);
			assignment.setAssignedTo(member);
			assignment.setAssignedBy(task.getAssignedBy());
			assignment.setCreatedAt(LocalDateTime.now());
			assignment.setEstimatedTime(dto.getEstimatedTime());
			assignment.setDesignation(member.getDesignation());

			task.getAssignments().add(assignment);

			if (!oldAssignments.containsKey(dto.getMemberId())
					|| !oldAssignments.get(dto.getMemberId()).equals(dto.getEstimatedTime())) {
				changedAssignments.add(assignment);
			}
		}

		return changedAssignments;
	}

	@Transactional
	public void deleteMainTask(String taskId, String memberId) {
		try {
			Task task = taskRepo.findByIdAndIsActiveAndCreatedById(taskId, true, memberId);
			if (task == null) {
				LOGGER.warn("Task not found or unauthorized access. Task ID: {}, Member ID: {}", taskId, memberId);
				throw new NotFoundException("Task not found or you don't have permission");
			}

			if (!TaskStatus.TODO.name().equals(task.getStatus())) {
				LOGGER.warn("Cannot delete task not in TODO status. Task ID: {}, Current Status: {}", taskId,
						task.getStatus());
				throw new AppException("You can only delete a task if it's in TODO stage");
			}

			String previousStatus = task.getStatus();
			task.setStatus(TaskStatus.BLOCKER.name());
			task.setModifiedBy(task.getCreatedBy());
			task.setModifiedDate(LocalDateTime.now());

			Task saved = taskRepo.save(task);

			ActivityRequestDto requestDto = new ActivityRequestDto();
			requestDto.setStatus(saved.getStatus());
			requestDto.setModifiedBy(saved.getModifiedBy().getId());
			requestDto.setModifiedDate(saved.getModifiedDate());

			createActivity("Deleted this task: moved from " + previousStatus + " to " + saved.getStatus(),
					saved.getModifiedBy(), requestDto, task);

			LOGGER.info("Task deleted (moved to BLOCKER). Task ID: {}", taskId);
		} catch (Exception e) {
			LOGGER.error("Unexpected error while deleting task. Task ID: {}, Error: {}", taskId, e.getMessage(), e);
			throw new AppException("Failed to delete task: " + e.getMessage());
		}
	}

	public DetailedTaskResponseDto taskStatusChange(String taskId, String memberId, TaskStatusChangeDto dto) {
		try {
			Task task = taskRepo.findById(taskId).orElseThrow(() -> {
				LOGGER.error("Task not found. Task ID: {}", taskId);
				return new NotFoundException("Task id not found");
			});

			Member actor = task.getAssignments().stream().map(TaskAssignment::getAssignedTo)
					.filter(m -> m.getId().equals(memberId)).findFirst().orElseThrow(() -> {
						LOGGER.error("Member not assigned to task. Member ID: {}, Task ID: {}", memberId, taskId);
						return new NotFoundException("You don't have permission to change this task");
					});

			TaskStatus from = TaskStatus.valueOf(task.getStatus());
			TaskStatus to = TaskStatus.valueOf(dto.getStatus());

			if (from == to)
				return convertTaskToDto(task);

			switch (to) {
			case DESIGN -> {
				if (from != TaskStatus.TODO)
					throw new AppException("Can move to DESIGN only from TODO");

				if (!hasDesignation(task, actor, TaskDesignation.DESIGNER.name()))
					throw new AppException("Only an assigned DESIGNER can start DESIGN");

				startStage(task, actor, "DESIGN");
			}

			case DEVELOPMENT -> {
				boolean isDesigner = hasDesignation(task, actor, TaskDesignation.DESIGNER.name());
				boolean isTester = hasDesignation(task, actor, TaskDesignation.TESTER.name());

				if (from == TaskStatus.DESIGN) {
					if (!(isDesigner))
						throw new AppException("Only an assigned DESIGNER can start DEVELOPMENT");

					stampSpentTime(task, actor);

				} else if (from == TaskStatus.TESTING) {
					if (!isTester)
						throw new AppException("Only an assigned DEVELOPER or TESTER can return task to DEVELOPMENT");

					if (task.getBugs() == null || task.getBugs().isEmpty()
							|| task.getBugs().stream().noneMatch(b -> BugStatus.NOT_FIXED.name().equals(b.getStatus())))
						throw new AppException("No unresolved bugs found to move back to DEVELOPMENT");

					TaskAssignment assignment = getActorAssignment(task, actor)
							.orElseThrow(() -> new AppException("Assignment not found"));

					task.getAssignments().stream()
							.filter(a -> TaskDesignation.DEVELOPER.name().equalsIgnoreCase(a.getDesignation()))
							.filter(a -> a.getWorkStatus() == WorkStatus.COMPLETED).forEach(a -> {
								a.setWorkStatus(WorkStatus.NOT_TAKEN);
								a.setStage(TaskStatus.DEVELOPMENT.name());
								a.setModifiedAt(LocalDateTime.now());
							});

					int minutes = (int) Duration.between(assignment.getStageStart(), assignment.getStageEnd())
							.toMinutes();
					Integer oldTime = assignment.getActualSpentTime();
					assignment.setActualSpentTime((oldTime != null ? oldTime : 0) + minutes);

				} else {
					throw new AppException(
							"Can move to DEVELOPMENT only from DESIGN or TESTING (with unresolved bugs)");
				}

			}
			case TESTING -> {
				if (from != TaskStatus.DEVELOPMENT)
					throw new AppException("Can move to TESTING only from DEVELOPMENT");

				boolean isDeveloper = hasDesignation(task, actor, TaskDesignation.DEVELOPER.name());

				if (!(isDeveloper))
					throw new AppException("Only an assigned DEVELOPER can start TESTING");

				TaskAssignment assignment = getActorAssignment(task, actor)
						.orElseThrow(() -> new AppException("Assignment not found"));

				if (assignment.getWorkStatus() != WorkStatus.WIP)
					throw new AppException("You must start DEVELOPMENT before moving to TESTING");

				if (task.getBugs() != null && !task.getBugs().isEmpty()
						&& task.getBugs().stream().allMatch(b -> b.getStatus().equals(BugStatus.NOT_FIXED.name())))
					throw new AppException("Cannot move to TESTING. All bugs must be resolved first.");

				int minutes = (int) Duration.between(assignment.getStageStart(), assignment.getStageEnd()).toMinutes();
				Integer oldTime = assignment.getActualSpentTime();
				assignment.setActualSpentTime((oldTime != null ? oldTime : 0) + minutes);
				assignment.setWorkStatus(WorkStatus.COMPLETED);
				task.getAssignments().stream()
						.filter(a -> TaskDesignation.TESTER.name().equalsIgnoreCase(a.getDesignation()))
						.filter(a -> a.getWorkStatus() == WorkStatus.WIP).forEach(a -> {
							a.setWorkStatus(WorkStatus.NOT_TAKEN);
							a.setStage(TaskStatus.TESTING.name());
							a.setModifiedAt(LocalDateTime.now());
						});
			}

			case DONE -> {
				if (from != TaskStatus.TESTING)
					throw new AppException("Can move to DONE only from TESTING");
				boolean isTester = hasDesignation(task, actor, TaskDesignation.TESTER.name());
				if (!isTester)
					throw new AppException("Only an assigned TESTER can start DONE");
				TaskAssignment assignment = getActorAssignment(task, actor)
						.orElseThrow(() -> new AppException("Assignment not found"));

				if (assignment.getWorkStatus() != WorkStatus.WIP)
					throw new AppException("You must start TESTING before completing the task");

				if (task.getBugs() != null && !task.getBugs().isEmpty()
						&& task.getBugs().stream().anyMatch(b -> !(BugStatus.VERIFIED.name().equals(b.getStatus())
								|| BugStatus.NOT_AN_ISSUE.name().equals(b.getStatus()))))
					throw new AppException("Cannot move to TESTING. All bugs must be resolved first.");

				int minutes = (int) Duration.between(assignment.getStageStart(), assignment.getStageEnd()).toMinutes();
				Integer oldTime = assignment.getActualSpentTime();
				assignment.setActualSpentTime((oldTime != null ? oldTime : 0) + minutes);
				assignment.setWorkStatus(WorkStatus.COMPLETED);
				task.setActualCompletedDate(LocalDateTime.now());
			}

			case BLOCKER -> {
				if (dto.getMessage() == null || dto.getMessage().isBlank())
					throw new AppException("Please supply a blocker reason");
			}

			default -> throw new AppException("Unsupported status: " + to);
			}

			task.setStatus(to.name());
			task.setModifiedBy(actor);
			task.setModifiedDate(LocalDateTime.now());

			TaskStatusTrack track = new TaskStatusTrack();
			track.setTask(task);
			track.setMember(actor);
			track.setChangedStatus(to.name());
			track.setChangedTime(task.getModifiedDate());
			track.setMessage(buildTrackMessage(from.name(), to.name(), dto));
			task.getTaskStatusTrack().add(track);

			Task saved = taskRepo.save(task);
			pushNotifications(saved, actor, from.name(), to.name());
			createHistoryActivity(saved, actor, from.name(), to.name(), dto);

			return convertTaskToDto(saved);
		} catch (Exception e) {
			LOGGER.error("Status change failed", e);
			throw new AppException(e.getMessage());
		}
	}

	private Optional<TaskAssignment> getActorAssignment(Task task, Member actor) {
		return task.getAssignments().stream().filter(a -> actor.equals(a.getAssignedTo())).findFirst();
	}

	private void startStage(Task task, Member actor, String stageName) {
		getActorAssignment(task, actor).ifPresent(a -> {
			if (a.getWorkStatus() == WorkStatus.COMPLETED)
				return;

			if (a.getWorkStatus() == WorkStatus.WIP && stageName.equals(a.getStage()))
				return;

			a.setStage(stageName);
			a.setStageStart(LocalDateTime.now());
			a.setWorkStatus(WorkStatus.WIP);
			a.setModifiedAt(LocalDateTime.now());
		});
		task.setActualStartDate(LocalDateTime.now());
	}

	private void stampSpentTime(Task task, Member actor) {
		getActorAssignment(task, actor).ifPresentOrElse(a -> {

//			if (a.getStageStart() == null)
//				throw new AppException("You must start work before completing this stage.");

			if (a.getWorkStatus() != WorkStatus.WIP)
				throw new AppException("Your work status must be WIP to complete this stage.");

			int minutes = (int) Duration.between(a.getStageStart(), LocalDateTime.now()).toMinutes();

			a.setActualSpentTime(minutes);
			a.setStageEnd(LocalDateTime.now());
			a.setWorkStatus(WorkStatus.COMPLETED);
			a.setModifiedAt(LocalDateTime.now());
		}, () -> {
			throw new AppException("No assignment found for the current user.");
		});
	}

	private void createHistoryActivity(Task task, Member actor, String from, String to, TaskStatusChangeDto dto) {
		ActivityRequestDto requestDto = new ActivityRequestDto();
		requestDto.setStatus(to);
		requestDto.setModifiedBy(actor.getId());
		requestDto.setModifiedDate(LocalDateTime.now());

		if (dto.getMessage() != null && !dto.getMessage().isBlank()) {
			requestDto.setMessage(dto.getMessage());
		}

		if (dto.getType() != null && !dto.getType().isBlank()) {
			requestDto.setType(dto.getType());
		}

		if (TaskStatus.DONE.name().equals(to)) {
			requestDto.setActualCompletedDate(task.getActualCompletedDate());
		}

		if (TaskStatus.DESIGN.name().equals(to) || TaskStatus.DEVELOPMENT.name().equals(to)) {
			requestDto.setActualStartDate(task.getActualStartDate());
		}

		createActivity("Task moved from " + from + " to " + to, actor, requestDto, task);
	}

	private boolean hasDesignation(Task task, Member actor, String role) {
		for (TaskAssignment assignment : task.getAssignments()) {
			if (assignment.getDesignation() != null && assignment.getDesignation().equalsIgnoreCase(role)
					&& actor.equals(assignment.getAssignedTo())) {
				return true;
			}
		}
		return false;
	}

	public void pushNotifications(Task task, Member actor, String from, String to) {

		String message = "Task \"" + task.getTitle() + "\" moved from " + from + " to " + to;
		Set<String> notified = new HashSet<>();

		Consumer<Member> notify = member -> {
			if (member != null && !actor.equals(member) && notified.add(member.getId())) {
				try {
					notifyService.sendNotification(member.getId(), actor.getId(), SPECIFIC_USER_TOPIC, message);
				} catch (IOException | SQLException e) {
					e.printStackTrace();
				}
				notifyService.sendNotificationAll();
			}
		};

		TaskStatus toStatus = TaskStatus.valueOf(to);
		TaskStatus fromStatus = TaskStatus.valueOf(from);

		if (fromStatus == TaskStatus.TODO && toStatus == TaskStatus.DESIGN) {
			notify.accept(task.getCreatedBy());
		}

		if (toStatus == TaskStatus.DEVELOPMENT && fromStatus != TaskStatus.TESTING) {
			task.getAssignments().stream()
					.filter(a -> TaskDesignation.DEVELOPER.name().equalsIgnoreCase(a.getDesignation()))
					.map(TaskAssignment::getAssignedTo).forEach(notify);

			notify.accept(task.getCreatedBy());
		}

		if (toStatus == TaskStatus.DEVELOPMENT && fromStatus == TaskStatus.TESTING) {
			task.getAssignments().stream()
					.filter(a -> TaskDesignation.DEVELOPER.name().equalsIgnoreCase(a.getDesignation()))
					.map(TaskAssignment::getAssignedTo).forEach(notify);

			notify.accept(task.getCreatedBy());
		}

		if (toStatus == TaskStatus.TESTING) {
			task.getAssignments().stream()
					.filter(a -> TaskDesignation.TESTER.name().equalsIgnoreCase(a.getDesignation()))
					.map(TaskAssignment::getAssignedTo).forEach(notify);

//			task.getAssignments().stream()
//					.filter(a -> TaskDesignation.DESIGNER.name().equalsIgnoreCase(a.getDesignation()))
//					.map(TaskAssignment::getAssignedTo).forEach(notify);

			notify.accept(task.getCreatedBy());
		}

		if (toStatus == TaskStatus.DONE) {
			notify.accept(task.getCreatedBy());
			task.getAssignments().stream().map(TaskAssignment::getAssignedTo).forEach(notify);
		}
	}

	public TaskAssignment updateWorkStatus(String memberId, WorkStatusUpdateDto dto) {
		try {
			Task task = taskRepo.findById(dto.getTaskId()).orElseThrow(() -> new NotFoundException("Task not found"));

			TaskAssignment assignment = task.getAssignments().stream()
					.filter(a -> a.getAssignedTo() != null && a.getAssignedTo().getId().equals(memberId)).findFirst()
					.orElseThrow(() -> new AppException("You are not assigned to this task"));

			String currentStage = task.getStatus();
			String designation = assignment.getDesignation();

			if ("DEVELOPMENT".equalsIgnoreCase(currentStage) && !"DEVELOPER".equalsIgnoreCase(designation)) {
				throw new AppException("Only a Developer can update work status in DEVELOPMENT stage");
			}

			if ("TESTING".equalsIgnoreCase(currentStage) && !"TESTER".equalsIgnoreCase(designation)) {
				throw new AppException("Only a Tester can update work status in TESTING stage");
			}

			if (dto.getStartTime() != null && dto.getEndTime() != null) {
				if (dto.getStartTime().equals(dto.getEndTime())) {
					LOGGER.error(
							" Start time and end time were the same. Task Created By ID: {}, Start time: {}, End time: {}",
							memberId, dto.getStartTime(), dto.getEndTime());
					throw new NotFoundException("Start time and end time was same.");
				} else if (dto.getStartTime().isAfter(dto.getEndTime())) {
					LOGGER.error(
							"Start time cannot be after end time. Task Created By ID: {}, Start time: {}, End time: {}",
							memberId, dto.getStartTime(), dto.getEndTime());
					throw new NotFoundException("Start time cannot be after end time.");
				}
			}
			boolean isFirstTime = (assignment.getStageStart() == null && assignment.getStageEnd() == null);
			assignment.setStage(task.getStatus());
			assignment.setStageEnd(dto.getEndTime());
			assignment.setStageStart(dto.getStartTime());
			assignment.setWorkStatus(WorkStatus.WIP);
			assignment.setModifiedAt(LocalDateTime.now());

			Task save = taskRepo.save(task);

			ActivityRequestDto requestDto = new ActivityRequestDto();
			requestDto.setStatus(currentStage);
			requestDto.setModifiedBy(assignment.getAssignedTo().getId());
			requestDto.setModifiedDate(LocalDateTime.now());

			String name = assignment.getAssignedTo().getFirstName() + " " + assignment.getAssignedTo().getLastName();
			String message;

			if (isFirstTime) {
				message = "assigned themselves as a " + currentStage.toLowerCase()
						+ " for this task. Due details: start date - " + dto.getStartTime() + ", end date - "
						+ dto.getEndTime();
			} else {
				message = "updated work status as a " + currentStage.toLowerCase() + ". Developer name: " + name
						+ ", start date - " + dto.getStartTime() + ", end date - " + dto.getEndTime();
			}

			createActivity(message, assignment.getAssignedTo(), requestDto, save);

			return assignment;

		} catch (NotFoundException | AppException e) {
			throw e;
		} catch (Exception e) {
			throw new AppException("Failed to update work status: " + e.getMessage());
		}
	}

	private String buildTrackMessage(String from, String to, TaskStatusChangeDto dto) {
		if (dto.getMessage() != null && !dto.getMessage().isBlank())
			return dto.getMessage();
		return "moved from " + from + " to " + to;
	}

	CriteriaQuery<Task> filterTasks(Member member, String titleOrTaskId, String projectName, String teamName,
			String searchMemberId, String taskStatus, LocalDate completedDate, String taskPriority, LocalDate startDate,
			LocalDate endDate, LocalDate assignedDate, String tag) {
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Task> query = cb.createQuery(Task.class);
			Root<Task> root = query.from(Task.class);
			List<Predicate> predicates = new ArrayList<>();

			if (member.getRole().equals(RoleType.EMPLOYEE.name()) || member.getRole().equals(RoleType.MANAGER.name())) {
				predicates.add(
						cb.isTrue(root.join("project").join("teams").join("members").get("id").in(member.getId())));
			}

			if (titleOrTaskId != null && !titleOrTaskId.isBlank()) {
				Predicate titlePredicate = cb.like(cb.lower(root.get("title")),
						"%" + titleOrTaskId.toLowerCase() + "%");
				Predicate idPredicate = cb.like(cb.lower(root.get("id")), "%" + titleOrTaskId.toLowerCase() + "%");
				predicates.add(cb.or(titlePredicate, idPredicate));
			}

			if (projectName != null && !projectName.isBlank()) {
				predicates.add(cb.like(cb.lower(root.get("project").get("projectName")),
						"%" + projectName.toLowerCase() + "%"));
			}

			if (teamName != null && !teamName.isBlank()) {
				predicates.add(cb.like(cb.lower(root.join("project").join("teams").get("teamName")),
						"%" + teamName.toLowerCase() + "%"));
			}

			if (searchMemberId != null && !searchMemberId.isBlank()) {
				Join<Task, TaskAssignment> assignmentJoin = root.join("assignments", JoinType.LEFT);
				predicates.add(cb.or(cb.equal(root.get("assignedBy").get("id"), searchMemberId),
						cb.equal(assignmentJoin.get("assignedTo").get("id"), searchMemberId)));
			}

			if (taskStatus != null && !taskStatus.isBlank()) {
				predicates.add(cb.equal(root.get("status"), taskStatus));
			}

			if (completedDate != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("actualCompletedDate"), completedDate.atTime(23, 59, 59)));
			}

			if (taskPriority != null && !taskPriority.isBlank()) {
				predicates.add(cb.equal(root.get("priority"), taskPriority));
			}

			if (startDate != null && endDate != null) {
				predicates.add(cb.between(root.get("startDate"), startDate.atStartOfDay(), endDate.atTime(23, 59, 59)));
				predicates.add(cb.between(root.get("endDate"), startDate.atStartOfDay(), endDate.atTime(23, 59, 59)));
			} else if (startDate != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), startDate.atStartOfDay()));
			} else if (endDate != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("endDate"), endDate.atTime(23, 59, 59)));
			}

			if (assignedDate != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("assignedDate"), assignedDate.atStartOfDay()));
			}

			if (tag != null && !tag.isBlank()) {
				Join<Task, Tag> tagJoin = root.join("tags", JoinType.LEFT);
				predicates.add(cb.like(cb.lower(tagJoin.get("name")), "%" + tag.toLowerCase() + "%"));
			}

			query.where(cb.and(predicates.toArray(new Predicate[0]))).distinct(true);
			query.orderBy(cb.desc(root.get("modifiedDate")));
			return query;

		} catch (Exception e) {
			LOGGER.error("Exception occurred while filtering tasks", e);
			throw new AppException("Error occurred while filtering tasks: " + e.getMessage());
		}
	}

	public Page<Object> searchTaskPageWise(String status, int pageNumber, int pageSize, String memberId,
			String titleOrTaskId, String projectName, String teamName, String searchMemberId, LocalDate startDate,
			LocalDate endDate, String taskPriority, LocalDate assignedDate, LocalDate completedDate, String tag) {
		try {
			Member member = memberRepo.findByIdAndStatus(memberId, Status.ACTIVE.name());
			if (member == null) {
				throw new NotFoundException("Member id not found");
			}
			CriteriaQuery<Task> query = filterTasks(member, titleOrTaskId, projectName, teamName, searchMemberId,
					status, completedDate, taskPriority, startDate, endDate, assignedDate, tag);

			TypedQuery<Task> typedQuery = entityManager.createQuery(query);
			typedQuery.setFirstResult(pageNumber * pageSize);
			typedQuery.setMaxResults(pageSize);
			List<Task> tasks = typedQuery.getResultList();
			long total = tasks.size();

			Pageable pageable = PageRequest.of(pageNumber, pageSize);
			Page<Task> taskPage = new PageImpl<>(tasks, pageable, total);

			return taskPage.map(task -> convertTaskToDto(task, member));
		} catch (Exception e) {
			LOGGER.error("Exception occurred while searching tasks page wise: {}", e.getMessage());
			throw new AppException(e.getMessage());
		}
	}

	public FirstPageTaskResponseDto searchTasksForFirstPage(String memberId, String titleOrTaskId, String projectName,
			String teamName, String searchMemberId, String taskStatus, LocalDate completedDate, String taskPriority,
			LocalDate startDate, LocalDate endDate, LocalDate assignedDate, String tag) {

		try {
			Member member = memberRepo.findByIdAndStatus(memberId, Status.ACTIVE.name());
			if (member == null) {
				throw new NotFoundException("Member id not found");
			}

			CriteriaQuery<Task> query = filterTasks(member, titleOrTaskId, projectName, teamName, searchMemberId,
					taskStatus, completedDate, taskPriority, startDate, endDate, assignedDate, tag);
			List<Task> tasks = entityManager.createQuery(query).getResultList();

			Map<String, List<Task>> tasksByStatusMap = tasks.stream()
					.collect(Collectors.groupingBy(Task::getStatus, Collectors.collectingAndThen(Collectors.toList(),
							list -> list.stream().limit(10).collect(Collectors.toList()))));

			Map<String, Long> taskCounts = tasks.stream()
					.collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));

			Map<String, List<TaskResponseDto>> tasksByStatus = new LinkedHashMap<>();
			tasksByStatus.put("TODO", new ArrayList<>());
			tasksByStatus.put("DESIGN", new ArrayList<>());
			tasksByStatus.put("DEVELOPMENT", new ArrayList<>());
			tasksByStatus.put("TESTING", new ArrayList<>());
			tasksByStatus.put("DONE", new ArrayList<>());
			tasksByStatus.put("BLOCKER", new ArrayList<>());

			for (Map.Entry<String, List<Task>> entry : tasksByStatusMap.entrySet()) {
				String status = entry.getKey();
				List<Task> limitedTasks = entry.getValue();

				if (tasksByStatus.containsKey(status)) {
					for (Task task : limitedTasks) {
						TaskResponseDto dto = convertTaskToDtoTableView(task);
						tasksByStatus.get(status).add(dto);
					}
				}
			}

			FirstPageTaskResponseDto response = new FirstPageTaskResponseDto();
			response.setTasks(tasksByStatus);
			response.setTodoTaskCount(taskCounts.getOrDefault("TODO", 0L).intValue());
			response.setDesignTaskCount(taskCounts.getOrDefault("DESIGN", 0L).intValue());
			response.setDevelopmentTaskCount(taskCounts.getOrDefault("DEVELOPMENT", 0L).intValue());
			response.setTestTaskCount(taskCounts.getOrDefault("TESTING", 0L).intValue());
			response.setDoneTaskCount(taskCounts.getOrDefault("DONE", 0L).intValue());
			response.setBlockerTaskCount(taskCounts.getOrDefault("BLOCKER", 0L).intValue());

			return response;

		} catch (Exception e) {
			LOGGER.error("Exception occurred while searching first page tasks: {}", e.getMessage(), e);
			throw new AppException(e.getMessage());
		}
	}

	TaskResponseDto convertTaskToDtoTableView(Task task) {
		TaskResponseDto responseDto = new TaskResponseDto();
		responseDto.setId(task.getId());
		responseDto.setTitle(task.getTitle());
		responseDto.setShortDescription(task.getShortDescription());

		ProjectResponseDto project = new ProjectResponseDto();
		project.setId(task.getProject().getId());
		project.setProjectName(task.getProject().getProjectName());
		responseDto.setProjectId(project);

		responseDto.setStatus(task.getStatus());
		responseDto.setComplexity(task.getComplexity());
		responseDto.setCreatedBy(convertMemberToDto(task.getCreatedBy()));
		responseDto.setCreatedDate(task.getCreatedDate());
		responseDto.setModifiedBy(convertMemberToDto(task.getModifiedBy()));
		responseDto.setModifiedDate(task.getModifiedDate());
		responseDto.setStartDate(task.getStartDate());
		responseDto.setEndDate(task.getEndDate());
		responseDto.setPriority(task.getPriority());
		responseDto.setActualStartDate(task.getActualStartDate());
		responseDto.setActualCompletedDate(task.getActualCompletedDate());
		responseDto.setBugsCount(task.getBugs().size());
		responseDto.setCommentsCount(task.getComments().size());
		responseDto.setAttachmentsCount(task.getAttachment().size());

		if (task.getAssignments() != null && !task.getAssignments().isEmpty()) {
			List<CommonAssigneeResponseDto> assigneeDtos = task.getAssignments().stream().map(assignment -> {
				CommonAssigneeResponseDto dto = new CommonAssigneeResponseDto();
				dto.setMember(convertMemberToDto(assignment.getAssignedTo()));
				dto.setDesignation(assignment.getDesignation());
				dto.setEstimationTime(assignment.getEstimatedTime());
				return dto;
			}).toList();
			responseDto.setAssignedTo(assigneeDtos);
		}

		responseDto.setAssignedBy(convertMemberToDto(task.getAssignedBy()));
		responseDto.setTags(task.getTags());

		return responseDto;
	}

	TaskResponseDto convertTaskToDto(Task task, Member memberId) {
		TaskResponseDto responseDto = new TaskResponseDto();
		responseDto.setId(task.getId());
		responseDto.setTitle(task.getTitle());
		responseDto.setShortDescription(task.getShortDescription());
		ProjectResponseDto project = new ProjectResponseDto();
		project.setId(task.getProject().getId());
		project.setProjectName(task.getProject().getProjectName());
		responseDto.setProjectId(project);

		responseDto.setStatus(task.getStatus());
		responseDto.setComplexity(task.getComplexity());
		responseDto.setCreatedBy(convertMemberToDto(task.getCreatedBy()));
		responseDto.setCreatedDate(task.getCreatedDate());
		responseDto.setModifiedBy(convertMemberToDto(task.getModifiedBy()));
		responseDto.setModifiedDate(task.getModifiedDate());
		responseDto.setStartDate(task.getStartDate());
		responseDto.setEndDate(task.getEndDate());
		responseDto.setPriority(task.getPriority());
		if (task.getAssignments() != null && !task.getAssignments().isEmpty()) {
			List<CommonAssigneeResponseDto> assigneeDtos = task.getAssignments().stream().map(assignment -> {
				CommonAssigneeResponseDto dto = new CommonAssigneeResponseDto();
				dto.setMember(convertMemberToDto(assignment.getAssignedTo()));
				dto.setDesignation(assignment.getDesignation());
				dto.setEstimationTime(assignment.getEstimatedTime());
				return dto;
			}).toList();
			responseDto.setAssignedTo(assigneeDtos);
		}
		if (task.getAssignedBy() != null) {
			responseDto.setAssignedBy(convertMemberToDto(task.getAssignedBy()));
		}

		responseDto.setTags(task.getTags());
		responseDto.setActualCompletedDate(task.getActualCompletedDate());
		responseDto.setActualStartDate(task.getActualStartDate());
//		responseDto.setPosition(task.getPosition());
		responseDto.setCommentsCount(task.getComments().size());
		responseDto.setAttachmentsCount(task.getAttachment().size());
		Optional<String> taskStatusTrackOptional = task.getTaskStatusTrack().stream()
				.filter(status -> status.getType() != null).map(this::convertTaskStatusTrackToDto)
				.sorted(Comparator.comparing(CommonTaskStatusTrackResponseDto::getChangedTime).reversed())
				.map(type -> type.getType()).findFirst();
		String taskStatusTrack = taskStatusTrackOptional.orElse("");

		if (taskStatusTrack != null && !taskStatusTrack.equals("") && taskStatusTrack.equals("Additional task")) {
			taskStatusTrack = "EXTRA TASK";
		} else if (taskStatusTrack != null && !taskStatusTrack.equals("")
				&& taskStatusTrack.equals("Internal rework")) {
			taskStatusTrack = "REWORK";
		} else if (taskStatusTrack != null && !taskStatusTrack.equals("")
				&& taskStatusTrack.equals("Client feedback")) {
			taskStatusTrack = "FEEDBACK";
		}
		responseDto.setTaskStatusTrack(taskStatusTrack);

		Optional<String> qcPersonFeedback = task.getTaskStatusTrack().stream()
				.sorted(Comparator.comparing(TaskStatusTrack::getChangedTime).reversed())
				.map(TaskStatusTrack::getMessage).findFirst();

		if (qcPersonFeedback.isPresent()) {
			if (qcPersonFeedback.get().contains("move this task from QA to DONE")) {
				String[] split = qcPersonFeedback.get().split("move this task from QA to DONE, ");
				responseDto.setQcPersonFeedBack(split[1]);
			}
		}
		return responseDto;
	}

	public DetailedTaskResponseDto convertTaskToDto(Task task) {
		DetailedTaskResponseDto responseDto = new DetailedTaskResponseDto();
		responseDto.setId(task.getId());
		responseDto.setTitle(task.getTitle());
		responseDto.setShortDescription(task.getShortDescription());
		responseDto.setDescription(task.getDescription());
		responseDto.setProjectId(convertProjectToDto(task.getProject()));
		responseDto.setStatus(task.getStatus());
		responseDto.setComplexity(task.getComplexity());
		responseDto.setCreatedBy(convertMemberToDto(task.getCreatedBy()));
		responseDto.setCreatedDate(task.getCreatedDate());
		responseDto.setModifiedBy(convertMemberToDto(task.getModifiedBy()));

		responseDto.setModifiedDate(task.getModifiedDate());
		responseDto.setStartDate(task.getStartDate());
		responseDto.setEndDate(task.getEndDate());
		responseDto.setPriority(task.getPriority());

		if (task.getAssignedBy() != null) {
			responseDto.setAssignedBy(convertMemberToDto(task.getAssignedBy()));
		}

		if (task.getAssignments() != null && !task.getAssignments().isEmpty()) {
			List<CommonAssigneeResponseDto> assigneeDtos = task.getAssignments().stream().map(assignment -> {
				CommonAssigneeResponseDto dto = new CommonAssigneeResponseDto();
				dto.setMember(convertMemberToDto(assignment.getAssignedTo()));
				dto.setDesignation(assignment.getDesignation());
				dto.setEstimationTime(assignment.getEstimatedTime());
				return dto;
			}).toList();
			responseDto.setAssignedTo(assigneeDtos);
		}

		responseDto.setTags(task.getTags());
		responseDto.setActualCompletedDate(task.getActualCompletedDate());
		responseDto.setActualStartDate(task.getActualStartDate());

		List<CommonCommentResponseDto> listOfComments = task.getComments().stream().map(comment -> {
			return convertCommentToDto(comment);
		}).collect(Collectors.toList());
		responseDto.setComments(listOfComments);

		responseDto.setCommentsCount(task.getComments().size());

		List<CommonAttachmentResponseDto> listOfAttachments = task.getAttachment().stream().map(attachment -> {
			return converAttachmentToDto(attachment);
		}).collect(Collectors.toList());
		responseDto.setAttachments(listOfAttachments);

		responseDto.setAttachmentsCount(task.getAttachment().size());

		List<CommonActivityResponseDto> listOfActivity = task.getActivity().stream().map(activity -> {
			return convertActivityToDto(activity);
		}).collect(Collectors.toList());
		responseDto.setActivity(listOfActivity);

		return responseDto;
	}

	public DetailedTaskResponseDto getTask(String taskId, String memberId) {
		try {
			Task task = taskRepo.findById(taskId).orElseThrow(() -> new NotFoundException("Task ID not found"));
			Member member = memberRepo.findByIdAndStatus(memberId, Status.ACTIVE.name());
			if (member == null) {
				throw new NotFoundException("Member not found.");
			}

			DetailedTaskResponseDto responseDto = new DetailedTaskResponseDto();
			responseDto.setId(task.getId());
			responseDto.setTitle(task.getTitle());
			responseDto.setShortDescription(task.getShortDescription());
			responseDto.setDescription(task.getDescription());
			responseDto.setProjectId(convertProjectToDto(task.getProject()));
			responseDto.setStatus(task.getStatus());
			responseDto.setComplexity(task.getComplexity());

			responseDto.setCreatedBy(convertMemberToDto(task.getCreatedBy()));

			responseDto.setCreatedDate(task.getCreatedDate());
			responseDto.setModifiedBy(convertMemberToDto(task.getModifiedBy()));
			responseDto.setModifiedDate(task.getModifiedDate());
			responseDto.setStartDate(task.getStartDate());
			responseDto.setEndDate(task.getEndDate());
			responseDto.setPriority(task.getPriority());

			if (task.getAssignedBy() != null) {
				responseDto.setAssignedBy(convertMemberToDto(task.getAssignedBy()));
			}
			if (task.getAssignments() != null && !task.getAssignments().isEmpty()) {
				List<CommonAssigneeResponseDto> assigneeDtos = task.getAssignments().stream().map(assignment -> {
					CommonAssigneeResponseDto dto = new CommonAssigneeResponseDto();
					dto.setMember(convertMemberToDto(assignment.getAssignedTo()));
					dto.setDesignation(assignment.getDesignation());
					dto.setEstimationTime(assignment.getEstimatedTime());
					return dto;
				}).toList();
				responseDto.setAssignedTo(assigneeDtos);
			}
			if (task.getBugs() != null && !task.getBugs().isEmpty()) {
				List<CommonBugResponseDto> bugDtos = task.getBugs().stream().map(bug -> {
					CommonBugResponseDto dto = new CommonBugResponseDto();
					dto.setId(bug.getId());
					dto.setTitle(bug.getTitle());
					dto.setDescription(bug.getDescription());
					dto.setSeverity(bug.getSeverity());
					dto.setStatus(bug.getStatus());
					dto.setReportedAt(bug.getReportedAt());

					if (bug.getCreatedBy() != null) {
						dto.setCreatedBy(convertMemberToDto(bug.getCreatedBy()));
					}

					return dto;
				}).toList();

				responseDto.setBugs(bugDtos);
			}

			responseDto.setTags(task.getTags());
			responseDto.setActualCompletedDate(task.getActualCompletedDate());
			responseDto.setActualStartDate(task.getActualStartDate());

			List<CommonCommentResponseDto> listOfComments = task.getComments().stream().map(comment -> {
				return convertCommentToDto(comment);
			}).collect(Collectors.toList());
			responseDto.setComments(listOfComments);

			responseDto.setCommentsCount(task.getComments().size());

			List<CommonAttachmentResponseDto> listOfAttachments = task.getAttachment().stream().map(e -> {
				return converAttachmentToDto(e);
			}).collect(Collectors.toList());
			responseDto.setAttachments(listOfAttachments);

			responseDto.setAttachmentsCount(task.getAttachment().size());

			List<CommonActivityResponseDto> listOfActivity = task.getActivity().stream().map(activity -> {
				return convertActivityToDto(activity);
			}).collect(Collectors.toList());
			responseDto.setActivity(listOfActivity);

			if (member.getRole().equals(RoleType.MANAGER.name()) && task.getProject() != null
					&& task.getProject().getTeams() != null
					&& task.getProject().getTeams().stream()
							.anyMatch(team -> team.getTeamLeadId() != null && team.getTeamLeadId().getId() != null
									&& team.getTeamLeadId().getId().equals(member.getId()))) {
			}

			List<CommonTaskStatusTrackResponseDto> listOfTaskStatusTrack = task.getTaskStatusTrack().stream()
					.map(taskStatusTrack -> {
						return convertTaskStatusTrackToDto(taskStatusTrack);
					}).collect(Collectors.toList());
			responseDto.setTaskStatusTrack(listOfTaskStatusTrack);

			return responseDto;
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception occured while getting the task with ID {} and memberId {}", taskId, memberId);
			throw new AppException(e.getMessage());
		}
	}

	private CommonProjectResponseDto convertProjectToDto(Project project) {
		CommonProjectResponseDto projectResponse = new CommonProjectResponseDto();
		projectResponse.setId(project.getId());
		projectResponse.setProjectName(project.getProjectName());
		projectResponse.setDescription(project.getDescription());
		projectResponse.setStatus(project.getStatus());
		projectResponse.setCreatedDate(project.getCreatedDate());
		projectResponse.setCreatedBy(convertMemberToDto(project.getCreatedBy()));
		projectResponse.setModifiedBy(convertMemberToDto(project.getModifiedBy()));
		projectResponse.setModifiedDate(project.getModifiedDate());
		projectResponse.setTeams(convertTeamToDto(project.getTeams()));
		projectResponse.setTags(project.getTags());
		List<CommonAttachmentResponseDto> listOfAttachments = project.getAttachments().stream().map(attachment -> {
			return converAttachmentToDto(attachment);
		}).collect(Collectors.toList());
		projectResponse.setAttachments(listOfAttachments);
		return projectResponse;
	}

	public CommonMemberResponseDto convertMemberToDto(Member member) {
		CommonMemberResponseDto memberDto = new CommonMemberResponseDto();
		if (member != null) {
			memberDto.setId(member.getId());
			memberDto.setFirstName(member.getFirstName());
			memberDto.setLastName(member.getLastName());
			memberDto.setRole(member.getRole());
//		memberDto.setDesignation(member.getDesignation());
//		memberDto.setGender(member.getGender());
		}
		return memberDto;
	}

	private List<CommonTeamResponseDto> convertTeamToDto(List<Team> teams) {
		List<CommonTeamResponseDto> listOfTeams = teams.stream().map(e -> {
			CommonTeamResponseDto commonTeamResponse = new CommonTeamResponseDto();
			commonTeamResponse.setId(e.getId());
			commonTeamResponse.setTeamName(e.getTeamName());
			commonTeamResponse.setTeamLeadId(convertMemberToDto(e.getTeamLeadId()));
			commonTeamResponse.setStatus(e.getStatus());
			Set<CommonMemberResponseDto> teamMembers = e.getMembers().stream().map(member -> {
				return convertMemberToDto(member);
			}).collect(Collectors.toSet());
			commonTeamResponse.setMembers(teamMembers);
			commonTeamResponse.setCreatedDate(e.getCreatedDate());
			commonTeamResponse.setModifiedDate(e.getModifiedDate());
			commonTeamResponse.setCreatedBy(convertMemberToDto(e.getCreatedBy()));
			commonTeamResponse.setModifiedBy(convertMemberToDto(e.getModifiedBy()));
			return commonTeamResponse;
		}).collect(Collectors.toList());
		return listOfTeams;
	}

	private CommonAttachmentResponseDto converAttachmentToDto(Attachment attachment) {
		CommonAttachmentResponseDto attachmentDto = new CommonAttachmentResponseDto();
		attachmentDto.setId(attachment.getId());
		attachmentDto.setStatus(attachment.getStatus());
		attachmentDto.setUploadedBy(convertMemberToDto(attachment.getUploadedBy()));
		attachmentDto.setUploadedDate(attachment.getUploadedDate());
		attachmentDto.setUrl(attachment.getUrl());
		return attachmentDto;
	}

	private CommonCommentResponseDto convertCommentToDto(Comments comment) {
		CommonCommentResponseDto commentDto = new CommonCommentResponseDto();
		commentDto.setId(comment.getId());
		commentDto.setCreatedDate(comment.getCreatedDate());
		commentDto.setCreatedBy(convertMemberToDto(comment.getCreatedBy()));
		commentDto.setCreatedDate(comment.getCreatedDate());
		commentDto.setModifiedBy(convertMemberToDto(comment.getModifiedBy()));
		commentDto.setModifiedDate(comment.getModifiedDate());
		commentDto.setMessage(comment.getMessage());
		return commentDto;
	}

	private CommonActivityResponseDto convertActivityToDto(Activity activity) {
		CommonActivityResponseDto activityDto = new CommonActivityResponseDto();
		activityDto.setId(activity.getId());
		activityDto.setCreatedBy(convertMemberToDto(activity.getCreatedBy()));
		activityDto.setCreatedDate(activity.getCreatedDate());
		activityDto.setDetailedMessage(activity.getDetailedMessage());
		activityDto.setMessage(activity.getMessage());
		return activityDto;
	}

	private CommonTaskStatusTrackResponseDto convertTaskStatusTrackToDto(TaskStatusTrack taskStatusTrack) {
		CommonTaskStatusTrackResponseDto taskStatusTrackDto = new CommonTaskStatusTrackResponseDto();
		taskStatusTrackDto.setId(taskStatusTrack.getId());
		taskStatusTrackDto.setChangedStatus(taskStatusTrack.getChangedStatus());
		taskStatusTrackDto.setChangedTime(taskStatusTrack.getChangedTime());
		taskStatusTrackDto.setMember(convertMemberToDto(taskStatusTrack.getMember()));
		if (taskStatusTrack.getType() != null) {
			taskStatusTrackDto.setMessage("(" + taskStatusTrack.getType() + ")" + taskStatusTrack.getMessage());
			taskStatusTrackDto.setType(taskStatusTrack.getType());
		} else {
			taskStatusTrackDto.setMessage(taskStatusTrack.getMessage());
		}
		return taskStatusTrackDto;
	}

	// =====================COMMENTS=================================//

	// Create the comment
	public Comments createCommentForTask(String memberId, CommentsRequestDto dto) {
		try {
			Task task = taskRepo.findById(dto.getTaskId())
					.orElseThrow(() -> new NotFoundException("Task id not found"));

			Optional<Member> assignedMemberOpt = task.getAssignments().stream().map(TaskAssignment::getAssignedTo)
					.filter(m -> m.getId().equals(memberId)).findFirst();

			boolean isAssignedBy = task.getAssignedBy() != null && task.getAssignedBy().getId().equals(memberId);

			Member member = null;
			if (assignedMemberOpt.isPresent()) {
				member = assignedMemberOpt.get();
			} else if (isAssignedBy) {
				member = task.getAssignedBy();
			} else {
				LOGGER.error("Access denied to comment. Member ID: {}, Task ID: {}", memberId, task.getId());
				throw new NotFoundException("You don't have permission to comment on this task");
			}

			Comments comment = new Comments();
			comment.setMessage(dto.getMessage());
			comment.setCreatedBy(member);
			comment.setModifiedBy(member);
			comment.setCreatedDate(LocalDateTime.now());
			comment.setModifiedDate(LocalDateTime.now());
			comment.setTask(task);
			Comments save = commentsRepo.save(comment);

			ActivityRequestDto requestDto = new ActivityRequestDto();
			requestDto.setCreatedDate(save.getCreatedDate());

			if (task.getComments() != null) {
				List<Comments> comments = new ArrayList<>(task.getComments());
				comments.sort(Comparator.comparing(Comments::getCreatedDate).reversed());
				requestDto.setComments(comments.stream().map(Comments::getMessage).collect(Collectors.toList()));
				requestDto.setCommentsCount(comments.size());
			}

			requestDto.setCommentsCount(task.getComments().size());
			createActivity("added a comment ", save.getCreatedBy(), requestDto, task);

			return save;
		} catch (Exception e) {
			LOGGER.error("Exception occurred while creating comment for task: " + e.getMessage());
			throw new AppException(e.getMessage());
		}
	}

	public List<Comments> getCommentsByTaskId(String taskId) {
		try {
			return taskRepo.findAllCommentsByIdOrderByCreatedDateDesc(taskId);
		} catch (Exception e) {
			LOGGER.error("Exception occurred while retrieving comments by task id :{} ", taskId);
			throw new AppException("Exception occured while getting all comments by task Id : {}" + e.getMessage());
		}
	}

	public Comments updateComments(String memberId, UpdateCommentsDo dto) {
		try {
			Comments comment = commentsRepo.findById(dto.getCommentId())
					.orElseThrow(() -> new NotFoundException("Comment Id not found"));

			Task task = taskRepo.findById(dto.getTaskId())
					.orElseThrow(() -> new NotFoundException("Task id not found"));

			Member modifiedBy = memberRepo.findById(memberId)
					.orElseThrow(() -> new NotFoundException("Modified By member not found"));

			if (comment.getCreatedBy().equals(modifiedBy) || modifiedBy.getRole().contains(RoleType.MANAGER.name())) {
				comment.setMessage(dto.getMessage());
				comment.setModifiedBy(modifiedBy);
				comment.setModifiedDate(LocalDateTime.now());
				Comments save = commentsRepo.save(comment);

				ActivityRequestDto requestDto = new ActivityRequestDto();

				requestDto.setModifiedBy(save.getModifiedBy().getId());
				requestDto.setModifiedDate(save.getModifiedDate());

				if (task.getComments() != null) {
					List<Comments> comments = new ArrayList<>(task.getComments());
					comments.sort(Comparator.comparing(Comments::getCreatedDate).reversed());
					requestDto.setComments(comments.stream().map(Comments::getMessage).collect(Collectors.toList()));
					requestDto.setCommentsCount(comments.size());
				}

				requestDto.setCommentsCount(task.getComments().size());
				createActivity("updated the comment ", save.getModifiedBy(), requestDto, task);

				return save;
			} else {
				LOGGER.error(
						"Exception occured while updating the comment : Unauthorized user {}, try to update the comment ..",
						memberId);
				throw new AppException("User is not authorized to update comments");
			}
		} catch (Exception e) {
			LOGGER.error("Exception occurred while updating comment: " + e.getMessage());
			throw new AppException(e.getMessage());
		}
	}

	public void deleteComment(String commentId, String modifiedById, String taskId) {
		try {
			Task task = taskRepo.findById(taskId).orElseThrow(() -> new NotFoundException("Task id not found"));

			Comments comment = commentsRepo.findById(commentId)
					.orElseThrow(() -> new NotFoundException("Comment Id not found"));

			Member modifiedBy = memberRepo.findById(modifiedById)
					.orElseThrow(() -> new NotFoundException("Modified By member not found"));

			if (comment.getCreatedBy().equals(modifiedBy) || modifiedBy.getRole().equals(RoleType.MANAGER.name())) {

				ActivityRequestDto requestDto = new ActivityRequestDto();

				requestDto.setModifiedBy(modifiedById);
				requestDto.setModifiedDate(LocalDateTime.now());

				createActivity("deleted comment", modifiedBy, requestDto, task);
				commentsRepo.delete(comment);
			} else {
				LOGGER.error("Exception occured while deleting the comment: Unauthorized user : {} ", modifiedById);
				throw new AppException("User is not authorized to delete comments");
			}
		} catch (Exception e) {
			LOGGER.error("Error occurred while deleting comment: " + e.getMessage());
			throw new AppException(e.getMessage());
		}
	}

	public void createActivity(String taskUsageMessage, Member createdBy, ActivityRequestDto taskRequestDto,
			Task task) {

		StringBuilder message = new StringBuilder();
		message.append(taskUsageMessage);

		if (taskRequestDto.getTitle() != null) {
			message.append(" title - ").append(taskRequestDto.getTitle());
		}
		if (taskRequestDto.getShortDescription() != null) {
			message.append(" short description");
		}
		if (taskRequestDto.getDescription() != null) {
			message.append(" description");
		}
		if (taskRequestDto.getProjectId() != null && taskRequestDto.getAttachments() == null) {
			message.append(" project ");
		}

		if (taskRequestDto.getAssignedTo() != null && !taskRequestDto.getAssignedTo().isEmpty()) {
			String joinedIds = String.join(", ", taskRequestDto.getAssignedTo());

			message.append(" assigned to - \"").append(" (").append(joinedIds).append(")\"");
		}

		if (taskRequestDto.getStartDate() != null) {
			if (taskRequestDto.getBeforeStartDate() == null) {
				LocalDateTime currentStartDate = taskRequestDto.getStartDate();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss");
				String formattedCurrentStartDate = currentStartDate.format(formatter);
				message.append(" due date of this start date - ").append(formattedCurrentStartDate);
			} else if (taskRequestDto.getBeforeStartDate() != null && taskRequestDto.getEndDate() != null
					&& taskRequestDto.getReason() != null) {
				LocalDateTime currentStartDate = taskRequestDto.getStartDate();
				LocalDateTime beforeStartDate = taskRequestDto.getBeforeStartDate();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss");
				String formattedCurrentStartDate = currentStartDate.format(formatter);
				String formattedBeforeStartDate = beforeStartDate.format(formatter);
				message.append(" due date dates, start date - ").append("from ").append(formattedBeforeStartDate)
						.append(" to ").append(formattedCurrentStartDate);
			} else if (taskRequestDto.getBeforeStartDate() != null && taskRequestDto.getEndDate() == null
					&& taskRequestDto.getReason() != null) {
				LocalDateTime currentStartDate = taskRequestDto.getStartDate();
				LocalDateTime beforeStartDate = taskRequestDto.getBeforeStartDate();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss");
				String formattedCurrentStartDate = currentStartDate.format(formatter);
				String formattedBeforeStartDate = beforeStartDate.format(formatter);
				message.append(" assigned to, start date - ").append("from ").append(formattedBeforeStartDate)
						.append(" to ").append(formattedCurrentStartDate);
			}
		}
		if (taskRequestDto.getEndDate() != null) {

			if (taskRequestDto.getBeforeEndDate() == null) {
				LocalDateTime endDate = taskRequestDto.getEndDate();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss");
				String formattedEndDate = endDate.format(formatter);
				message.append(", end date - ").append(formattedEndDate);
			} else if (taskRequestDto.getBeforeStartDate() != null && taskRequestDto.getBeforeEndDate() != null
					&& taskRequestDto.getReason() != null) {
				LocalDateTime currentEndDate = taskRequestDto.getEndDate();
				LocalDateTime beforeEndDate = taskRequestDto.getBeforeEndDate();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss");
				String formattedCurrentEndDate = currentEndDate.format(formatter);
				String formattedBeforeEndDate = beforeEndDate.format(formatter);
				message.append(" and end date  - ").append("from ").append(formattedBeforeEndDate).append(" to ")
						.append(formattedCurrentEndDate);
			} else if (taskRequestDto.getBeforeStartDate() == null && taskRequestDto.getBeforeEndDate() != null
					&& taskRequestDto.getReason() != null) {
				LocalDateTime currentEndDate = taskRequestDto.getEndDate();
				LocalDateTime beforeEndDate = taskRequestDto.getBeforeEndDate();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss");
				String formattedCurrentEndDate = currentEndDate.format(formatter);
				String formattedBeforeEndDate = beforeEndDate.format(formatter);
				message.append(" assigned to, end date  - ").append("from ").append(formattedBeforeEndDate)
						.append(" to ").append(formattedCurrentEndDate);
			}

		}
		if (taskRequestDto.getPriority() != null) {
			if (taskRequestDto.getBeforePriority() == null) {
				message.append(" priority " + taskRequestDto.getPriority());
			} else if (taskRequestDto.getBeforePriority() != null && taskRequestDto.getReason() != null) {
				message.append(" priority - ").append(taskRequestDto.getBeforePriority()).append(" to ")
						.append(taskRequestDto.getPriority());
			}
		}
		if (taskRequestDto.getPosition() > 0) {
			message.append(" position");
		}
		if (taskRequestDto.getTags() != null) {
			message.append(" tags");
		}
		if (taskRequestDto.getComplexity() >= 1) {
			message.append(" complexity from " + taskRequestDto.getBeforeTaskComplexity() + " to "
					+ taskRequestDto.getComplexity());
		}

		if (taskRequestDto.getReason() != null) {
			message.append(" . Reason : \"" + taskRequestDto.getReason() + "\"");
		}

		if (taskRequestDto.getType() != null && !taskRequestDto.getType().isEmpty()) {

			if (taskRequestDto.getMessage() != null && !taskRequestDto.getMessage().isEmpty()
					&& !taskRequestDto.getType().equalsIgnoreCase("Others")) {
				message.append(" . Type - ").append(taskRequestDto.getType());
				message.append(" , Reason : \"" + taskRequestDto.getMessage() + "\"");
			} else {
				message.append(" . Reason : \"" + taskRequestDto.getMessage() + "\"");
			}
		}

		StringBuilder detailedMessage = new StringBuilder();
		detailedMessage.append(taskUsageMessage);
		detailedMessage.append(" Details : ");

		if (taskRequestDto.getTitle() != null) {
			detailedMessage.append(", Title: ").append(taskRequestDto.getTitle());
		}
		if (taskRequestDto.getShortDescription() != null) {
			detailedMessage.append(", Short Description: ").append(taskRequestDto.getShortDescription());
		}
		if (taskRequestDto.getDescription() != null) {
			detailedMessage.append(", Description: ").append(taskRequestDto.getDescription());
		}
		if (taskRequestDto.getProjectId() != null) {
			detailedMessage.append(", Project: ").append(taskRequestDto.getProjectId());
		}
		if (taskRequestDto.getStatus() != null) {
			detailedMessage.append(", Status: ").append(taskRequestDto.getStatus());
		}
		if (taskRequestDto.getCreatedBy() != null) {
			detailedMessage.append(", CreatedBy : ").append(taskRequestDto.getCreatedBy());
		}
		if (taskRequestDto.getCreatedDate() != null) {
			detailedMessage.append(", CreatedDate : ").append(taskRequestDto.getCreatedDate());
		}
		if (taskRequestDto.getModifiedBy() != null) {
			detailedMessage.append(", ModifiedBy : ").append(taskRequestDto.getModifiedBy());
		}
		if (taskRequestDto.getModifiedDate() != null) {
			detailedMessage.append(", ModifiedDate : ").append(taskRequestDto.getModifiedDate());
		}
		if (taskRequestDto.getActualStartDate() != null) {
			detailedMessage.append(", ActualStartDate : ").append(taskRequestDto.getActualStartDate());
		}
		if (taskRequestDto.getActualCompletedDate() != null) {
			detailedMessage.append(", ActualCompletedDate : ").append(taskRequestDto.getActualCompletedDate());
		}
		if (taskRequestDto.getStartDate() != null) {
			detailedMessage.append(", Start Date - ").append(taskRequestDto.getStartDate());
		}
		if (taskRequestDto.getEndDate() != null) {
			detailedMessage.append(", End Date - ").append(taskRequestDto.getEndDate());
		}
		if (taskRequestDto.getPriority() != null) {
			detailedMessage.append(", Priority: ").append(taskRequestDto.getPriority());
		}
		if (taskRequestDto.getPosition() > 0) {
			detailedMessage.append(", Position: ").append(taskRequestDto.getPosition());
		}
		if (taskRequestDto.getComplexity() > 0) {
			detailedMessage.append(", Complexity: from ").append(taskRequestDto.getBeforeTaskComplexity())
					.append(" to ").append(taskRequestDto.getComplexity());
		}
		if (taskRequestDto.isActive()) {
			detailedMessage.append(", isActive: ").append(taskRequestDto.isActive());
		}
		if (taskRequestDto.getAssignedTo() != null && !taskRequestDto.getAssignedTo().isEmpty()) {
			detailedMessage.append(", Assigned To: ").append(taskRequestDto.getAssignedTo());
		}
		if (taskRequestDto.getAssignedBy() != null && !taskRequestDto.getAssignedBy().isEmpty()) {
			detailedMessage.append(", Assigned By: ").append(taskRequestDto.getAssignedBy());
		}
		if (taskRequestDto.getAssignedDate() != null) {
			detailedMessage.append(", AssignedDate: ").append(taskRequestDto.getAssignedDate());
		}
		if (taskRequestDto.getTags() != null) {
			detailedMessage.append(", Tags: ")
					.append(taskRequestDto.getTags().stream().collect(Collectors.joining(", ")));
		}
		if (taskRequestDto.getComments() != null && !taskRequestDto.getComments().isEmpty()) {
			detailedMessage.append(", Comments: ")
					.append(taskRequestDto.getComments().stream().collect(Collectors.joining(", ")));
		}
		if (taskRequestDto.getAttachments() != null && !taskRequestDto.getAttachments().isEmpty()) {
			detailedMessage.append(", Attachments: ").append(taskRequestDto.getAttachments());
		}
		if (taskRequestDto.getType() != null && !taskRequestDto.getType().isEmpty()) {
			if (taskRequestDto.getMessage() != null && !taskRequestDto.getMessage().isEmpty()
					&& !taskRequestDto.getType().equalsIgnoreCase("Others")) {
				detailedMessage.append(" , Type - ").append(taskRequestDto.getType());
				detailedMessage.append(" , Reason - \"" + taskRequestDto.getMessage() + "\"");
			} else {
				detailedMessage.append(" , Reason - \"" + taskRequestDto.getMessage() + "\"");
			}
		}

		Activity activity = new Activity();
		activity.setTask(task);
		activity.setCreatedBy(createdBy);
		activity.setCreatedDate(LocalDateTime.now());
		activity.setMessage(message.toString());
		activity.setDetailedMessage(detailedMessage.toString());
		activityRepo.save(activity);
	}

	public Member validMemberCheck(String memberId, String errorType, String roleType) {

		Member manager = memberRepo.findByIdAndStatusAndRole(memberId, Status.ACTIVE.name(), roleType);

		if (manager == null) {
			LOGGER.error("Exception occurred while {}. Member ID: {}", errorType, memberId);
			throw new NotFoundException("Manager not found or you don't have permission");
		}

		return manager;

	}

}
