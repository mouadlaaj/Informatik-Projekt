package com.task.mgmt.tracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.task.mgmt.tracker.constant.RoleType;
import com.task.mgmt.tracker.constant.Status;
import com.task.mgmt.tracker.constant.TaskStatus;
import com.task.mgmt.tracker.entity.Member;
import com.task.mgmt.tracker.entity.Project;
import com.task.mgmt.tracker.entity.Task;
import com.task.mgmt.tracker.entity.TaskAssignment;
import com.task.mgmt.tracker.entity.TaskStatusTrack;
import com.task.mgmt.tracker.entity.Team;
import com.task.mgmt.tracker.exception.AppException;
import com.task.mgmt.tracker.repository.MemberRepository;
import com.task.mgmt.tracker.repository.TaskRepository;
import com.task.mgmt.tracker.repository.TaskStatusTrackRepository;
import com.task.mgmt.tracker.response.payload.DesignationWiseReportStatus;
import com.task.mgmt.tracker.response.payload.ReportResponseDto;
import com.task.mgmt.tracker.response.payload.TaskCountDetailsResponseDto;
import com.task.mgmt.tracker.response.payload.TaskReportResponseDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

	@Mock
	private MemberRepository memberRepo;

	@Mock
	private EntityManager entityManager;

	@Mock
	private CriteriaBuilder criteriaBuilder;

	@Mock
	private CriteriaQuery<TaskAssignment> criteriaQuery;

	@Mock
	private Root<TaskAssignment> root;

	@Mock
	private Join<Object, Object> memberJoin;

	@Mock
	private Join<Object, Object> taskJoin;

	@Mock
	private Join<Object, Object> projectJoin;

	@Mock
	private Join<Object, Object> teamJoin;

	@Mock
	private TypedQuery<TaskAssignment> typedQuery;

	@InjectMocks
	private ReportService reportService;
	
	@Mock
	private TaskRepository taskRepo;
	
	@Mock
	private TaskStatusTrackRepository taskStatusTrackRepo;

	private Member testMember;
	private TaskAssignment testAssignment;
	private Task testTask;
	private Project testProject;
	private Team testTeam;

	@BeforeEach
	void setUp() {
		testMember = new Member();
		testMember.setId("member123");
		testMember.setFirstName("John");
		testMember.setLastName("Doe");
		testMember.setRole(RoleType.EMPLOYEE.name());

		testTeam = new Team();
		testTeam.setId("team123");
		testTeam.setTeamName("Development Team");
		testTeam.setMembers(Arrays.asList(testMember));

		testProject = new Project();
		testProject.setId("project123");
		testProject.setTeams(Arrays.asList(testTeam));

		testTask = new Task();
		testTask.setId("task123");
		testTask.setTitle("Test Task");
		testTask.setProject(testProject);
		testTask.setStatus(TaskStatus.DONE.name());
		testTask.setStartDate(LocalDate.now().atStartOfDay());
		testTask.setEndDate(LocalDate.now().atTime(23, 59, 59));

		testAssignment = new TaskAssignment();
		testAssignment.setTask(testTask);
		testAssignment.setAssignedTo(testMember);
		testAssignment.setDesignation("Software Engineer");
		testAssignment.setStageStart(LocalDateTime.now());
		testAssignment.setEstimatedTime(120);
		testAssignment.setActualSpentTime(100);
	}

	@Test
	void testGetTaskWorkReportFiltered_SuccessfulExecution() {
		String loggedUser = "user123";
		String teamId = "team123";
		String memberId = "member123";
		LocalDate startDate = LocalDate.now().minusDays(7);
		LocalDate endDate = LocalDate.now();

		when(memberRepo.findByIdAndStatus(loggedUser, Status.ACTIVE.name())).thenReturn(testMember);

		setupCriteriaBuilderMocks();
		when(typedQuery.getResultList()).thenReturn(Arrays.asList(testAssignment));

		List<TaskReportResponseDto> result = reportService.getTaskWorkReportFiltered(loggedUser, teamId, memberId,
				startDate, endDate);

		assertNotNull(result);
		assertEquals(1, result.size());

		TaskReportResponseDto dto = result.get(0);
		assertEquals("team123", dto.getTeamId());
		assertEquals("Development Team", dto.getTeamName());
		assertEquals(0, dto.getTeamOrder());

		TaskReportResponseDto.TaskMemberWorkInfo workInfo = dto.getWorkInfo();
		assertEquals("member123", workInfo.getMemberId());
		assertEquals("John Doe", workInfo.getMemberName());
		assertEquals("Software Engineer", workInfo.getDesignation());
		assertEquals("task123", workInfo.getTaskId());
		assertEquals("Test Task", workInfo.getTaskTitle());
		assertEquals("120 mins", workInfo.getEstimatedTime());
		assertEquals("100 mins", workInfo.getActualSpentTime());
	}

	@Test
	void testGetTaskWorkReportFiltered_MemberNotFound() {
		String loggedUser = "nonexistent";
		when(memberRepo.findByIdAndStatus(loggedUser, Status.ACTIVE.name())).thenReturn(null);

		AppException exception = assertThrows(AppException.class, () -> {
			reportService.getTaskWorkReportFiltered(loggedUser, null, null, null, null);
		});

		assertEquals("Member not found or inactive", exception.getMessage());
	}

	@Test
	void testConvertToTaskReportDto_SuccessfulConversion() {

		TaskReportResponseDto result = reportService.convertToTaskReportDto(testAssignment);

		assertNotNull(result);
		assertEquals("team123", result.getTeamId());
		assertEquals("Development Team", result.getTeamName());
		assertEquals(0, result.getTeamOrder());

		TaskReportResponseDto.TaskMemberWorkInfo workInfo = result.getWorkInfo();
		assertNotNull(workInfo);
		assertEquals("member123", workInfo.getMemberId());
		assertEquals("John Doe", workInfo.getMemberName());
		assertEquals("Software Engineer", workInfo.getDesignation());
		assertEquals("task123", workInfo.getTaskId());
		assertEquals("Test Task", workInfo.getTaskTitle());
		assertEquals("120 mins", workInfo.getEstimatedTime());
		assertEquals("100 mins", workInfo.getActualSpentTime());
		assertNotNull(workInfo.getPercentage());
	}

	@Test
	void testConvertToTaskReportDto_MemberNotInTeam() {

		Member differentMember = new Member();
		differentMember.setId("different123");
		differentMember.setFirstName("Jane");
		differentMember.setLastName("Smith");

		testAssignment.setAssignedTo(differentMember);

		TaskReportResponseDto result = reportService.convertToTaskReportDto(testAssignment);

		assertNotNull(result);
		assertNull(result.getTeamId());
		assertNull(result.getTeamName());
		assertEquals(0, result.getTeamOrder());
	}

	@Test
	void testConvertToTaskReportDto_NoTeamsInProject() {

		testProject.setTeams(null);

		TaskReportResponseDto result = reportService.convertToTaskReportDto(testAssignment);

		assertNotNull(result);
		assertNull(result.getTeamId());
		assertNull(result.getTeamName());
		assertEquals(0, result.getTeamOrder());
	}

	private void setupCriteriaBuilderMocks() {
		when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
		when(criteriaBuilder.createQuery(TaskAssignment.class)).thenReturn(criteriaQuery);
		when(criteriaQuery.from(TaskAssignment.class)).thenReturn(root);

		when(root.join("assignedTo", JoinType.LEFT)).thenReturn(memberJoin);
		when(root.join("task", JoinType.LEFT)).thenReturn(taskJoin);
		when(taskJoin.join("project", JoinType.LEFT)).thenReturn(projectJoin);
		when(projectJoin.join("teams", JoinType.LEFT)).thenReturn(teamJoin);

		when(taskJoin.get("startDate")).thenReturn(mock(jakarta.persistence.criteria.Path.class));
		when(taskJoin.get("endDate")).thenReturn(mock(jakarta.persistence.criteria.Path.class));
		when(taskJoin.get("status")).thenReturn(mock(jakarta.persistence.criteria.Path.class));
		when(memberJoin.get("id")).thenReturn(mock(jakarta.persistence.criteria.Path.class));
		when(teamJoin.get("id")).thenReturn(mock(jakarta.persistence.criteria.Path.class));

		when(criteriaBuilder.<LocalDateTime>greaterThanOrEqualTo(any(), any(LocalDateTime.class)))
				.thenReturn(mock(Predicate.class));
		when(criteriaBuilder.<LocalDateTime>lessThanOrEqualTo(any(), any(LocalDateTime.class)))
				.thenReturn(mock(Predicate.class));
		when(criteriaBuilder.and(any(Predicate[].class))).thenReturn(mock(Predicate.class));
		when(criteriaBuilder.desc(any())).thenReturn(mock(jakarta.persistence.criteria.Order.class));

		when(criteriaQuery.where(any(Predicate.class))).thenReturn(criteriaQuery);
		when(criteriaQuery.orderBy(any(jakarta.persistence.criteria.Order.class))).thenReturn(criteriaQuery);

		when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
	}
	
	@Test
	void testGenerateTaskReport_Success_Manager() {
	    Member manager = new Member();
	    manager.setId("mem_001");
	    manager.setRole("MANAGER");
	    manager.setStatus(Status.ACTIVE.name());

	    Task task1 = new Task(); task1.setId("task_1");
	    task1.setStatus(TaskStatus.TODO.name());
	    Task task2 = new Task(); task2.setId("task_2");
	    task2.setStatus(TaskStatus.DEVELOPMENT.name());

	    TaskStatusTrack track1 = new TaskStatusTrack(); track1.setTask(task1);
	    TaskStatusTrack track2 = new TaskStatusTrack(); track2.setTask(task2);

	    when(memberRepo.findByIdAndStatus("mem_001", "ACTIVE")).thenReturn(manager);
	    when(taskStatusTrackRepo.findByMemberIdOrTask_AssignedBy_IdAndChangedTimeBetween(
	            eq("mem_001"), eq("mem_001"), any(), any()
	    )).thenReturn(List.of(track1, track2));

	    ReportResponseDto report = reportService.generateTaskReport(null, null, "mem_001");

	    assertNotNull(report);
	    assertEquals(1, report.getTasksInTodo());
	    assertEquals(1, report.getTasksInDevelopment());
	}

	
	@Test
	void testGenerateTaskReport_Failure_InvalidUser() {
	    when(memberRepo.findByIdAndStatus("invalid_user", "ACTIVE")).thenReturn(null);

	    AppException exception = assertThrows(AppException.class, () -> {
	    	reportService.generateTaskReport(LocalDate.now().minusDays(5), LocalDate.now(), "invalid_user");
	    });

	    assertEquals("Invalid user", exception.getMessage());
	}

	@Test
	void testGetDesignationWiseReportStatus_Success_Manager() {
	    Member manager = new Member();
	    manager.setId("mgr_01");
	    manager.setRole("MANAGER");
	    manager.setDesignation("MANAGER");

	    Member dev = new Member();
	    dev.setId("dev_01");
	    dev.setRole("EMPLOYEE");
	    dev.setDesignation("DEVELOPER");

	    TaskAssignment ta = new TaskAssignment();
	    ta.setAssignedTo(dev);
	    ta.setActualSpentTime(200);
	    ta.setEstimatedTime(100);

	    when(memberRepo.findAllByStatus("ACTIVE")).thenReturn(List.of(manager, dev));
	    when(taskRepo.findAllDoneAssignments()).thenReturn(List.of(ta));

	    DesignationWiseReportStatus report = reportService.getDesignationWiseReportStatus("mgr_01", "DEVELOPER");

	    assertNotNull(report);
	    assertEquals(1, report.getDeveloper().getTotalCount());
	    assertEquals(0, report.getDesigner().getTotalCount());
	    assertEquals(0, report.getTester().getTotalCount());

	    List<DesignationWiseReportStatus.MemberEfficiencyDto> members = report.getMembers();
	    assertEquals(1, members.size());
	    assertEquals("dev_01", members.get(0).getMemberId());
	    assertEquals("50.00%", members.get(0).getEfficiency());
	}
	@Test
	void testGetDesignationWiseReportStatus_Failure_ExceptionThrown() {
	    when(memberRepo.findAllByStatus("ACTIVE")).thenThrow(new RuntimeException("DB down"));

	    AppException exception = assertThrows(AppException.class, () ->
	        reportService.getDesignationWiseReportStatus("mgr_01", "DEVELOPER")
	    );

	    assertEquals("Error populating Designation Wise Report Status", exception.getMessage());
	}
	

}