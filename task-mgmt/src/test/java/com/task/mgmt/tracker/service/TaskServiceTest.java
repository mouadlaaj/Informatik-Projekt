package com.task.mgmt.tracker.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;

import com.task.mgmt.tracker.constant.RoleType;
import com.task.mgmt.tracker.constant.Status;
import com.task.mgmt.tracker.constant.WorkStatus;
import com.task.mgmt.tracker.entity.Activity;
import com.task.mgmt.tracker.entity.Bug;
import com.task.mgmt.tracker.entity.Comments;
import com.task.mgmt.tracker.entity.Member;
import com.task.mgmt.tracker.entity.Project;
import com.task.mgmt.tracker.entity.Task;
import com.task.mgmt.tracker.entity.TaskAssignment;
import com.task.mgmt.tracker.entity.Team;
import com.task.mgmt.tracker.exception.AppException;
import com.task.mgmt.tracker.exception.NotFoundException;
import com.task.mgmt.tracker.repository.ActivityRepository;
import com.task.mgmt.tracker.repository.CommentsRepository;
import com.task.mgmt.tracker.repository.MemberRepository;
import com.task.mgmt.tracker.repository.ProjectRepository;
import com.task.mgmt.tracker.repository.TaskRepository;
import com.task.mgmt.tracker.request.payload.AssigneeDto;
import com.task.mgmt.tracker.request.payload.CommentsRequestDto;
import com.task.mgmt.tracker.request.payload.CreateTaskRequestDto;
import com.task.mgmt.tracker.request.payload.TaskStatusChangeDto;
import com.task.mgmt.tracker.request.payload.UpdateCommentsDo;
import com.task.mgmt.tracker.request.payload.UpdateTaskRequestDto;
import com.task.mgmt.tracker.request.payload.WorkStatusUpdateDto;
import com.task.mgmt.tracker.response.payload.DetailedTaskResponseDto;
import com.task.mgmt.tracker.response.payload.FirstPageTaskResponseDto;
import com.task.mgmt.tracker.response.payload.TaskResponseDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

class TaskServiceTest {

	@InjectMocks
	private TaskService taskService;

	@Mock
	private TaskRepository taskRepo;

	@Mock
	private ProjectRepository projectRepo;

	@Mock
	private MemberRepository memberRepo;

	@Mock
	private NotificationService notifyService;

	@Mock
	private ActivityRepository activityRepo;

	@Mock
	private EntityManager entityManager;

	@Mock
	private CriteriaBuilder cb;

	@Mock
	private CriteriaQuery<Task> query;

	@Mock
	private Root<Task> root;

	@Mock
	private TypedQuery<Task> typedQuery;

	@Mock
	private Task mockTask;

	@Mock
	private CommentsRepository commentsRepo;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		taskService = Mockito.spy(taskService);
	}

	@Test
	void testCreateTask_success() {
		String managerId = "mem_001";

		Member manager = new Member();
		manager.setId(managerId);
		manager.setDesignation("Manager");

		Project project = new Project();
		project.setId("proj_001");

		Member designer = new Member();
		designer.setId("mem_d1");
		designer.setDesignation("Designer");

		Member developer = new Member();
		developer.setId("mem_dev1");
		developer.setDesignation("Developer");

		Member tester = new Member();
		tester.setId("mem_test1");
		tester.setDesignation("Tester");

		Team team = new Team();
		team.setMembers(List.of(designer, developer, tester));
		project.setTeams(List.of(team));

		CreateTaskRequestDto dto = new CreateTaskRequestDto();
		dto.setTitle("New Task");
		dto.setProjectId("proj_001");

		AssigneeDto designerDto = new AssigneeDto();
		designerDto.setMemberId("mem_d1");
		designerDto.setEstimatedTime(10);

		AssigneeDto developerDto = new AssigneeDto();
		developerDto.setMemberId("mem_dev1");
		developerDto.setEstimatedTime(15);

		AssigneeDto testerDto = new AssigneeDto();
		testerDto.setMemberId("mem_test1");
		testerDto.setEstimatedTime(20);

		dto.setAssignedTo(List.of(designerDto, developerDto, testerDto));

		Task savedTask = new Task();
		savedTask.setId("task_001");

		when(activityRepo.save(any())).thenReturn(new Activity());

		when(projectRepo.findByIdAndStatus("proj_001", Status.ACTIVE.name())).thenReturn(project);
		doReturn(manager).when(taskService).validMemberCheck(managerId, "creating task", RoleType.MANAGER.name());
		when(taskRepo.save(any(Task.class))).thenReturn(savedTask);
		doReturn(new DetailedTaskResponseDto()).when(taskService).convertTaskToDto(any());

		DetailedTaskResponseDto response = taskService.createTask(managerId, dto);

		assertNotNull(response);
	}

	@Test
	void testCreateTask_noAssignees() {
		String managerId = "mem_001";

		Member manager = new Member();
		manager.setId(managerId);
		manager.setDesignation("Manager");

		Project project = new Project();
		project.setId("proj_001");

		CreateTaskRequestDto dto = new CreateTaskRequestDto();
		dto.setTitle("Task without assignees");
		dto.setProjectId("proj_001");

		Task savedTask = new Task();
		savedTask.setId("task_002");

		when(activityRepo.save(any())).thenReturn(new Activity());

		when(projectRepo.findByIdAndStatus("proj_001", Status.ACTIVE.name())).thenReturn(project);
		doReturn(manager).when(taskService).validMemberCheck(managerId, "creating task", RoleType.MANAGER.name());
		when(taskRepo.save(any(Task.class))).thenReturn(savedTask);
		doReturn(new DetailedTaskResponseDto()).when(taskService).convertTaskToDto(any());

		DetailedTaskResponseDto response = taskService.createTask(managerId, dto);

		assertNotNull(response);
	}

	@Test
	void testCreateTask_projectNotFound() {
		String managerId = "mem_001";
		CreateTaskRequestDto dto = new CreateTaskRequestDto();
		dto.setProjectId("proj_404");

		doReturn(new Member()).when(taskService).validMemberCheck(managerId, "creating task", RoleType.MANAGER.name());
		when(projectRepo.findByIdAndStatus("proj_404", Status.ACTIVE.name())).thenReturn(null);

		AppException ex = assertThrows(AppException.class, () -> {
			taskService.createTask(managerId, dto);
		});

		assertTrue(ex.getMessage().contains("Project id not found"));
	}

	@Test
	void testCreateTask_missingTester() {
		String managerId = "mem_001";

		Member manager = new Member();
		manager.setId(managerId);

		Project project = new Project();
		Member designer = new Member();
		designer.setId("mem_d1");
		designer.setDesignation("Designer");

		Member developer = new Member();
		developer.setId("mem_dev1");
		developer.setDesignation("Developer");

		Team team = new Team();
		team.setMembers(List.of(designer, developer));
		project.setTeams(List.of(team));

		CreateTaskRequestDto dto = new CreateTaskRequestDto();
		dto.setTitle("Bad Assignee Task");
		dto.setProjectId("proj_001");
		AssigneeDto designerDto = new AssigneeDto();
		designerDto.setMemberId("mem_d1");
		designerDto.setEstimatedTime(5);

		AssigneeDto developerDto = new AssigneeDto();
		developerDto.setMemberId("mem_dev1");
		developerDto.setEstimatedTime(8);

		dto.setAssignedTo(List.of(designerDto, developerDto));

		when(projectRepo.findByIdAndStatus("proj_001", Status.ACTIVE.name())).thenReturn(project);
		doReturn(manager).when(taskService).validMemberCheck(managerId, "creating task", RoleType.MANAGER.name());

		AppException ex = assertThrows(AppException.class, () -> {
			taskService.createTask(managerId, dto);
		});

		assertTrue(ex.getMessage().contains("Designer, Developer, and Tester"));
	}

	@Test
	void testUpdateTask_updateTitleAndDescription_success() {
		String taskId = "task_001";
		String memberId = "mem_001";

		Task task = new Task();
		task.setId(taskId);
		task.setTitle("Old Title");
		task.setShortDescription("Old Desc");
		task.setStatus("TODO");

		Project project = new Project();
		project.setTags(List.of());
		task.setProject(project);

		Member creator = new Member();
		creator.setId(memberId);
		task.setCreatedBy(creator);

		UpdateTaskRequestDto dto = new UpdateTaskRequestDto();
		dto.setTitle("New Title");
		dto.setShortDescription("New Desc");
		dto.setComplexity(0);
		dto.setAssignedTo(null);

		when(taskRepo.findByIdAndIsActiveAndCreatedById(taskId, true, memberId)).thenReturn(task);
		when(taskRepo.save(any(Task.class))).thenReturn(task);

		DetailedTaskResponseDto mockResponse = new DetailedTaskResponseDto();
		doReturn(mockResponse).when(taskService).convertTaskToDto(any());

		DetailedTaskResponseDto response = taskService.updateTask(taskId, memberId, dto);

		assertNotNull(response);
	}

	@Test
	void testUpdateTask_updateAssignees_success() {
		String taskId = "task_001";
		String memberId = "mem_001";

		Task task = new Task();
		task.setId(taskId);
		task.setStatus("TODO");

		Member creator = new Member();
		creator.setId(memberId);
		task.setCreatedBy(creator);
		task.setAssignments(new ArrayList<>());

		Member assignee = new Member();
		assignee.setId("mem_d1");
		assignee.setDesignation("Designer");

		Team team = new Team();
		team.setMembers(List.of(assignee));
		Project project = new Project();
		project.setTeams(List.of(team));
		project.setTags(List.of());
		task.setProject(project);

		UpdateTaskRequestDto dto = new UpdateTaskRequestDto();
		AssigneeDto dtoAssignee = new AssigneeDto();
		dtoAssignee.setMemberId("mem_d1");
		dtoAssignee.setEstimatedTime(5);
		dto.setAssignedTo(List.of(dtoAssignee));
		dto.setComplexity(0);

		when(taskRepo.findByIdAndIsActiveAndCreatedById(taskId, true, memberId)).thenReturn(task);
		when(taskRepo.save(any(Task.class))).thenReturn(task);
		doReturn(new DetailedTaskResponseDto()).when(taskService).convertTaskToDto(any());

		DetailedTaskResponseDto response = taskService.updateTask(taskId, memberId, dto);

		assertNotNull(response);
	}

	@Test
	void testUpdateTask_taskNotFound() {
		String taskId = "invalid_task";
		String memberId = "mem_001";

		when(taskRepo.findByIdAndIsActiveAndCreatedById(taskId, true, memberId)).thenReturn(null);

		UpdateTaskRequestDto dto = new UpdateTaskRequestDto();
		dto.setTitle("Update attempt");

		NotFoundException ex = assertThrows(NotFoundException.class,
				() -> taskService.updateTask(taskId, memberId, dto));

		assertTrue(ex.getMessage().contains("Task not found"));
	}

	@Test
	void testUpdateTask_invalidDates() {
		String taskId = "task_001";
		String memberId = "mem_001";

		Task task = new Task();
		task.setId(taskId);
		task.setStatus("TODO");
		task.setCreatedBy(new Member());

		Project project = new Project();
		project.setTags(List.of());
		task.setProject(project);

		when(taskRepo.findByIdAndIsActiveAndCreatedById(taskId, true, memberId)).thenReturn(task);

		UpdateTaskRequestDto dto = new UpdateTaskRequestDto();
		dto.setStartDate(LocalDateTime.of(2025, 7, 20, 10, 0));
		dto.setEndDate(LocalDateTime.of(2025, 7, 19, 10, 0));

		AppException ex = assertThrows(AppException.class, () -> taskService.updateTask(taskId, memberId, dto));

		assertTrue(ex.getMessage().contains("Start date cannot be after end date"));
	}

	@Test
	void testDeleteMainTask_success() {
		String taskId = "task_001";
		String memberId = "mem_001";

		Member creator = new Member();
		creator.setId(memberId);

		Task task = new Task();
		task.setId(taskId);
		task.setStatus("TODO");
		task.setCreatedBy(creator);

		when(taskRepo.findByIdAndIsActiveAndCreatedById(taskId, true, memberId)).thenReturn(task);
		when(taskRepo.save(any(Task.class))).thenReturn(task);

		doNothing().when(taskService).createActivity(anyString(), any(Member.class), any(), any(Task.class));

		assertDoesNotThrow(() -> taskService.deleteMainTask(taskId, memberId));
	}

	@Test
	void testDeleteMainTask_notInTodoStatus_shouldThrow() {
		String taskId = "task_002";
		String memberId = "mem_001";

		Member creator = new Member();
		creator.setId(memberId);

		Task task = new Task();
		task.setId(taskId);
		task.setStatus("IN_PROGRESS");
		task.setCreatedBy(creator);

		when(taskRepo.findByIdAndIsActiveAndCreatedById(taskId, true, memberId)).thenReturn(task);

		AppException ex = assertThrows(AppException.class, () -> {
			taskService.deleteMainTask(taskId, memberId);
		});

		assertTrue(ex.getMessage().contains("only delete a task if it's in TODO stage"));
	}

	@Test
	void testStatusChange_TodoToDesign_Success() {
		String taskId = "task_001";
		String memberId = "mem_design";

		Member designer = new Member();
		designer.setId(memberId);

		TaskAssignment assignment = new TaskAssignment();
		assignment.setAssignedTo(designer);
		assignment.setDesignation("DESIGNER");
		assignment.setWorkStatus(WorkStatus.NOT_TAKEN);

		Project project = new Project();
		project.setId("proj_001");
		project.setProjectName("Test Project");

		Task task = new Task();
		task.setId(taskId);
		task.setStatus("TODO");
		task.setAssignments(List.of(assignment));
		task.setTaskStatusTrack(new ArrayList<>());
		task.setProject(project);

		TaskStatusChangeDto dto = new TaskStatusChangeDto();
		dto.setStatus("DESIGN");

		when(taskRepo.findById(taskId)).thenReturn(Optional.of(task));
		when(taskRepo.save(any(Task.class))).thenReturn(task);
		doNothing().when(taskService).createActivity(any(), any(), any(), any());
		doNothing().when(taskService).pushNotifications(any(), any(), any(), any());

		DetailedTaskResponseDto response = taskService.taskStatusChange(taskId, memberId, dto);

		assertNotNull(response);
	}

	@Test
	void testStatusChange_DesignToDevelopment_Success() {
		String taskId = "task_002";
		String memberId = "mem_design";

		Member designer = new Member();
		designer.setId(memberId);

		TaskAssignment assignment = new TaskAssignment();
		assignment.setAssignedTo(designer);
		assignment.setDesignation("DESIGNER");
		assignment.setWorkStatus(WorkStatus.WIP);
		assignment.setStage("DESIGN");
		assignment.setStageStart(LocalDateTime.now().minusMinutes(30));
		assignment.setStageEnd(LocalDateTime.now());

		Project project = new Project();
		project.setId("proj_001");
		project.setProjectName("Test Project");

		Task task = new Task();
		task.setId(taskId);
		task.setStatus("DESIGN");
		task.setAssignments(List.of(assignment));
		task.setTaskStatusTrack(new ArrayList<>());
		task.setProject(project);

		TaskStatusChangeDto dto = new TaskStatusChangeDto();
		dto.setStatus("DEVELOPMENT");

		when(taskRepo.findById(taskId)).thenReturn(Optional.of(task));
		when(taskRepo.save(any(Task.class))).thenReturn(task);
		doNothing().when(taskService).createActivity(any(), any(), any(), any());
		doNothing().when(taskService).pushNotifications(any(), any(), any(), any());

		DetailedTaskResponseDto response = taskService.taskStatusChange(taskId, memberId, dto);

		assertNotNull(response);
	}

	@Test
	void testStatusChange_TaskNotFound_ShouldThrow() {
		String taskId = "invalid_task";
		String memberId = "mem_001";

		TaskStatusChangeDto dto = new TaskStatusChangeDto();
		dto.setStatus("DESIGN");

		when(taskRepo.findById(taskId)).thenReturn(Optional.empty());

		AppException ex = assertThrows(AppException.class, () -> {
			taskService.taskStatusChange(taskId, memberId, dto);
		});

		assertTrue(ex.getMessage().contains("Task id not found"));
	}

	@Test
	void testStatusChange_MemberNotAssigned_ShouldThrow() {
		String taskId = "task_003";
		String memberId = "mem_unauthorized";

		Member assignedMember = new Member();
		assignedMember.setId("mem_assigned");

		TaskAssignment assignment = new TaskAssignment();
		assignment.setAssignedTo(assignedMember);
		assignment.setDesignation("DESIGNER");

		Task task = new Task();
		task.setId(taskId);
		task.setStatus("TODO");
		task.setAssignments(List.of(assignment));
		task.setTaskStatusTrack(new ArrayList<>());

		TaskStatusChangeDto dto = new TaskStatusChangeDto();
		dto.setStatus("DESIGN");

		when(taskRepo.findById(taskId)).thenReturn(Optional.of(task));

		AppException ex = assertThrows(AppException.class, () -> {
			taskService.taskStatusChange(taskId, memberId, dto);
		});

		assertTrue(ex.getMessage().contains("You don't have permission"));
	}

	@Test
	void testUpdateWorkStatus_DeveloperInDevelopment_Success() {
		String memberId = "dev_001";
		Task task = new Task();
		task.setId("task_001");
		task.setStatus("DEVELOPMENT");

		Member developer = new Member();
		developer.setId(memberId);
		developer.setFirstName("Dev");
		developer.setLastName("One");

		TaskAssignment assignment = new TaskAssignment();
		assignment.setAssignedTo(developer);
		assignment.setDesignation("DEVELOPER");

		task.setAssignments(List.of(assignment));

		WorkStatusUpdateDto dto = new WorkStatusUpdateDto();
		dto.setTaskId("task_001");
		dto.setStartTime(LocalDateTime.now().minusMinutes(30));
		dto.setEndTime(LocalDateTime.now());

		when(taskRepo.findById("task_001")).thenReturn(Optional.of(task));
		when(taskRepo.save(any(Task.class))).thenReturn(task);
		doNothing().when(taskService).createActivity(any(), any(), any(), any());

		TaskAssignment result = taskService.updateWorkStatus(memberId, dto);

		assertNotNull(result);
		assertEquals(WorkStatus.WIP, result.getWorkStatus());
	}

	@Test
	void testUpdateWorkStatus_TesterInTesting_Success() {
		String memberId = "test_001";
		Task task = new Task();
		task.setId("task_002");
		task.setStatus("TESTING");

		Member tester = new Member();
		tester.setId(memberId);
		tester.setFirstName("Test");
		tester.setLastName("User");

		TaskAssignment assignment = new TaskAssignment();
		assignment.setAssignedTo(tester);
		assignment.setDesignation("TESTER");

		task.setAssignments(List.of(assignment));

		WorkStatusUpdateDto dto = new WorkStatusUpdateDto();
		dto.setTaskId("task_002");
		dto.setStartTime(LocalDateTime.now().minusMinutes(20));
		dto.setEndTime(LocalDateTime.now());

		when(taskRepo.findById("task_002")).thenReturn(Optional.of(task));
		when(taskRepo.save(any(Task.class))).thenReturn(task);
		doNothing().when(taskService).createActivity(any(), any(), any(), any());

		TaskAssignment result = taskService.updateWorkStatus(memberId, dto);

		assertNotNull(result);
		assertEquals(WorkStatus.WIP, result.getWorkStatus());
	}

	@Test
	void testUpdateWorkStatus_DeveloperInTesting_ThrowsAppException() {
		String memberId = "dev_002";
		Task task = new Task();
		task.setId("task_003");
		task.setStatus("TESTING");

		Member developer = new Member();
		developer.setId(memberId);

		TaskAssignment assignment = new TaskAssignment();
		assignment.setAssignedTo(developer);
		assignment.setDesignation("DEVELOPER");

		task.setAssignments(List.of(assignment));

		WorkStatusUpdateDto dto = new WorkStatusUpdateDto();
		dto.setTaskId("task_003");
		dto.setStartTime(LocalDateTime.now().minusMinutes(15));
		dto.setEndTime(LocalDateTime.now());

		when(taskRepo.findById("task_003")).thenReturn(Optional.of(task));

		AppException exception = assertThrows(AppException.class, () -> taskService.updateWorkStatus(memberId, dto));

		assertEquals("Only a Tester can update work status in TESTING stage", exception.getMessage());
	}

	@Test
	void testUpdateWorkStatus_StartAfterEnd_ThrowsNotFoundException() {
		String memberId = "test_002";
		Task task = new Task();
		task.setId("task_004");
		task.setStatus("TESTING");

		Member tester = new Member();
		tester.setId(memberId);

		TaskAssignment assignment = new TaskAssignment();
		assignment.setAssignedTo(tester);
		assignment.setDesignation("TESTER");

		task.setAssignments(List.of(assignment));

		WorkStatusUpdateDto dto = new WorkStatusUpdateDto();
		dto.setTaskId("task_004");
		dto.setStartTime(LocalDateTime.now().plusMinutes(10));
		dto.setEndTime(LocalDateTime.now());

		when(taskRepo.findById("task_004")).thenReturn(Optional.of(task));

		NotFoundException exception = assertThrows(NotFoundException.class,
				() -> taskService.updateWorkStatus(memberId, dto));

		assertEquals("Start time cannot be after end time.", exception.getMessage());
	}

	@Test
	void testGetTask_Success_WithBugsAndAssignments() {
		String taskId = "task001";
		String memberId = "mem001";

		when(taskRepo.findById(taskId)).thenReturn(Optional.of(mockTask));

		Member member = new Member();
		member.setId(memberId);
		member.setRole("EMPLOYEE");
		when(memberRepo.findByIdAndStatus(memberId, "ACTIVE")).thenReturn(member);

		when(mockTask.getId()).thenReturn(taskId);
		when(mockTask.getTitle()).thenReturn("Sample Task");
		when(mockTask.getShortDescription()).thenReturn("Short desc");
		when(mockTask.getDescription()).thenReturn("Detailed desc");
		when(mockTask.getStatus()).thenReturn("TODO");

		when(mockTask.getAssignments()).thenReturn(List.of(getAssignment("DEVELOPER", 50, member)));

		when(mockTask.getBugs()).thenReturn(List.of(getBug()));
		when(mockTask.getTags()).thenReturn(Set.of());
		when(mockTask.getComments()).thenReturn(List.of());
		when(mockTask.getAttachment()).thenReturn(List.of());
		when(mockTask.getActivity()).thenReturn(List.of());
		when(mockTask.getTaskStatusTrack()).thenReturn(List.of());

		when(mockTask.getProject()).thenReturn(new Project());
		when(mockTask.getCreatedBy()).thenReturn(member);
		when(mockTask.getModifiedBy()).thenReturn(member);
		when(mockTask.getAssignedBy()).thenReturn(member);

		DetailedTaskResponseDto response = taskService.getTask(taskId, memberId);

		assertNotNull(response);
		assertEquals(taskId, response.getId());
		assertEquals("Sample Task", response.getTitle());
		assertEquals(1, response.getAssignedTo().size());
		assertEquals(1, response.getBugs().size());
	}

	@Test
	void testGetTask_Success_WithoutAssignmentsOrBugs() {
		String taskId = "task002";
		String memberId = "mem002";

		when(taskRepo.findById(taskId)).thenReturn(Optional.of(mockTask));

		Member member = new Member();
		member.setId(memberId);
		member.setRole("MANAGER");
		when(memberRepo.findByIdAndStatus(memberId, "ACTIVE")).thenReturn(member);

		when(mockTask.getId()).thenReturn(taskId);
		when(mockTask.getTitle()).thenReturn("Task without assignees or bugs");
		when(mockTask.getAssignments()).thenReturn(List.of());
		when(mockTask.getBugs()).thenReturn(null);
		when(mockTask.getTags()).thenReturn(Set.of());
		when(mockTask.getComments()).thenReturn(List.of());
		when(mockTask.getAttachment()).thenReturn(List.of());
		when(mockTask.getActivity()).thenReturn(List.of());
		when(mockTask.getTaskStatusTrack()).thenReturn(List.of());

		when(mockTask.getProject()).thenReturn(new Project());
		when(mockTask.getCreatedBy()).thenReturn(member);
		when(mockTask.getModifiedBy()).thenReturn(member);
		when(mockTask.getAssignedBy()).thenReturn(null);

		DetailedTaskResponseDto response = taskService.getTask(taskId, memberId);

		assertNotNull(response);
		assertEquals(taskId, response.getId());
		assertEquals("Task without assignees or bugs", response.getTitle());
	}

	@Test
	void testGetTask_Failure_TaskNotFound() {
		String taskId = "invalid_task";
		String memberId = "mem001";

		when(taskRepo.findById(taskId)).thenReturn(Optional.empty());

		AppException ex = assertThrows(AppException.class, () -> {
			taskService.getTask(taskId, memberId);
		});

		assertEquals("Task ID not found", ex.getMessage());
	}

	@Test
	void testGetTask_Failure_InvalidMember() {
		String taskId = "task003";
		String memberId = "invalid_mem";

		when(taskRepo.findById(taskId)).thenReturn(Optional.of(mockTask));
		when(memberRepo.findByIdAndStatus(memberId, "ACTIVE")).thenReturn(null);

		AppException ex = assertThrows(AppException.class, () -> {
			taskService.getTask(taskId, memberId);
		});

		assertEquals("Member not found.", ex.getMessage());
	}

	private TaskAssignment getAssignment(String designation, int estimatedTime, Member member) {
		TaskAssignment a = new TaskAssignment();
		a.setDesignation(designation);
		a.setEstimatedTime(estimatedTime);
		a.setAssignedTo(member);
		return a;
	}

	private Bug getBug() {
		Bug bug = new Bug();
		bug.setId(50l);
		bug.setTitle("NullPointer");
		bug.setDescription("Null value crash");
		bug.setSeverity("HIGH");
		bug.setStatus("OPEN");
		bug.setReportedAt(LocalDateTime.now());
		bug.setCreatedBy(new Member());
		return bug;
	}

	@Test
	void testCreateCommentForTask_Success_AssignedMember() {
		String memberId = "mem001";
		String taskId = "task001";

		Member member = new Member();
		member.setId(memberId);

		Task task = new Task();
		task.setId(taskId);
		task.setAssignments(List.of(getAssignment(taskId, 0, member)));

		CommentsRequestDto dto = new CommentsRequestDto();
		dto.setTaskId(taskId);
		dto.setMessage("Looks good");

		when(taskRepo.findById(taskId)).thenReturn(Optional.of(task));
		when(commentsRepo.save(any())).thenAnswer(i -> i.getArgument(0));

		Comments savedComment = taskService.createCommentForTask(memberId, dto);

		assertNotNull(savedComment);
		assertEquals("Looks good", savedComment.getMessage());
		assertEquals(member, savedComment.getCreatedBy());
	}

	@Test
	void testCreateCommentForTask_Success_AssignedBy() {
		String memberId = "mem002";
		String taskId = "task002";

		Member assigner = new Member();
		assigner.setId(memberId);

		Task task = new Task();
		task.setId(taskId);
		task.setAssignments(List.of());
		task.setAssignedBy(assigner);

		CommentsRequestDto dto = new CommentsRequestDto();
		dto.setTaskId(taskId);
		dto.setMessage("Manager’s comment");

		when(taskRepo.findById(taskId)).thenReturn(Optional.of(task));
		when(commentsRepo.save(any())).thenAnswer(i -> i.getArgument(0));

		Comments savedComment = taskService.createCommentForTask(memberId, dto);

		assertNotNull(savedComment);
		assertEquals("Manager’s comment", savedComment.getMessage());
		assertEquals(assigner, savedComment.getCreatedBy());
	}

	@Test
	void testCreateCommentForTask_Failure_TaskNotFound() {
		String memberId = "memX";
		String taskId = "invalidTask";

		CommentsRequestDto dto = new CommentsRequestDto();
		dto.setTaskId(taskId);
		dto.setMessage("Invalid task");

		when(taskRepo.findById(taskId)).thenReturn(Optional.empty());

		AppException ex = assertThrows(AppException.class, () -> {
			taskService.createCommentForTask(memberId, dto);
		});

		assertEquals("Task id not found", ex.getMessage());
	}

	@Test
	void testCreateCommentForTask_Failure_UnauthorizedMember() {
		String memberId = "unauthorizedMember";
		String taskId = "task003";

		Member anotherMember = new Member();
		anotherMember.setId("assignedMember");

		Task task = new Task();
		task.setId(taskId);
		task.setAssignments(List.of(getAssignment(anotherMember)));
		task.setAssignedBy(null);

		CommentsRequestDto dto = new CommentsRequestDto();
		dto.setTaskId(taskId);
		dto.setMessage("Hacking comment");

		when(taskRepo.findById(taskId)).thenReturn(Optional.of(task));

		AppException ex = assertThrows(AppException.class, () -> {
			taskService.createCommentForTask(memberId, dto);
		});

		assertEquals("You don't have permission to comment on this task", ex.getMessage());
	}

	private TaskAssignment getAssignment(Member member) {
		TaskAssignment assignment = new TaskAssignment();
		assignment.setAssignedTo(member);
		return assignment;
	}

	@Test
	void testGetCommentsByTaskId_Success() {
		String taskId = "task123";

		Comments comment1 = new Comments();
		comment1.setMessage("First comment");
		comment1.setCreatedDate(LocalDateTime.now().minusMinutes(10));

		Comments comment2 = new Comments();
		comment2.setMessage("Second comment");
		comment2.setCreatedDate(LocalDateTime.now());

		List<Comments> commentList = List.of(comment2, comment1);

		when(taskRepo.findAllCommentsByIdOrderByCreatedDateDesc(taskId)).thenReturn(commentList);

		List<Comments> result = taskService.getCommentsByTaskId(taskId);

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("Second comment", result.get(0).getMessage());
	}

	@Test
	void testGetCommentsByTaskId_Failure_Exception() {
		String taskId = "invalid_task";

		when(taskRepo.findAllCommentsByIdOrderByCreatedDateDesc(taskId)).thenThrow(new RuntimeException("DB error"));

		AppException ex = assertThrows(AppException.class, () -> {
			taskService.getCommentsByTaskId(taskId);
		});

		assertTrue(ex.getMessage().contains("Exception occured while getting all comments by task Id"));
	}

	@Test
	void testUpdateComment_Success_ByCreator() {
		Member creator = new Member();
		creator.setId("mem_001");
		creator.setRole("EMPLOYEE");
		Comments comment = new Comments();
		comment.setId("cmt_001");
		comment.setCreatedBy(creator);
		Task task = new Task();
		task.setId("task_001");
		task.setComments(List.of(comment));

		UpdateCommentsDo dto = new UpdateCommentsDo();
		dto.setCommentId("cmt_001");
		dto.setTaskId("task_001");
		dto.setMessage("Updated message");

		when(commentsRepo.findById("cmt_001")).thenReturn(Optional.of(comment));
		when(taskRepo.findById("task_001")).thenReturn(Optional.of(task));
		when(memberRepo.findById("mem_001")).thenReturn(Optional.of(creator));
		when(commentsRepo.save(any())).thenReturn(comment);

		Comments result = taskService.updateComments("mem_001", dto);
		assertEquals("Updated message", result.getMessage());
	}

	@Test
	void testUpdateComment_Success_ByManager() {
		Member manager = new Member();
		manager.setId("mem_mgr");
		manager.setRole("MANAGER");
		Member creator = new Member();
		creator.setId("mem_002");
		Comments comment = new Comments();
		comment.setId("cmt_002");
		comment.setCreatedBy(creator);
		Task task = new Task();
		task.setId("task_002");
		task.setComments(List.of(comment));

		UpdateCommentsDo dto = new UpdateCommentsDo();
		dto.setCommentId("cmt_002");
		dto.setTaskId("task_002");
		dto.setMessage("Manager updated");

		when(commentsRepo.findById("cmt_002")).thenReturn(Optional.of(comment));
		when(taskRepo.findById("task_002")).thenReturn(Optional.of(task));
		when(memberRepo.findById("mem_mgr")).thenReturn(Optional.of(manager));
		when(commentsRepo.save(any())).thenReturn(comment);

		Comments result = taskService.updateComments("mem_mgr", dto);
		assertEquals("Manager updated", result.getMessage());
	}

	@Test
	void testUpdateComment_Failure_UnauthorizedUser() {
		Member other = new Member();
		other.setId("mem_other");
		other.setRole("EMPLOYEE");
		Member creator = new Member();
		creator.setId("mem_creator");
		Comments comment = new Comments();
		comment.setId("cmt_003");
		comment.setCreatedBy(creator);
		Task task = new Task();
		task.setId("task_003");

		UpdateCommentsDo dto = new UpdateCommentsDo();
		dto.setCommentId("cmt_003");
		dto.setTaskId("task_003");
		dto.setMessage("Should fail");

		when(commentsRepo.findById("cmt_003")).thenReturn(Optional.of(comment));
		when(taskRepo.findById("task_003")).thenReturn(Optional.of(task));
		when(memberRepo.findById("mem_other")).thenReturn(Optional.of(other));

		AppException exception = assertThrows(AppException.class, () -> {
			taskService.updateComments("mem_other", dto);
		});

		assertEquals("User is not authorized to update comments", exception.getMessage());
	}

	@Test
	void testUpdateComment_Failure_CommentNotFound() {
		UpdateCommentsDo dto = new UpdateCommentsDo();
		dto.setCommentId("invalid_comment");
		dto.setTaskId("task_004");

		when(commentsRepo.findById("invalid_comment")).thenReturn(Optional.empty());

		AppException exception = assertThrows(AppException.class, () -> {
			taskService.updateComments("mem_004", dto);
		});

		assertEquals("Comment Id not found", exception.getMessage());
	}

	@Test
	void testDeleteComment_Success_ByCreator() {
		Member creator = new Member();
		creator.setId("mem_del_001");
		creator.setRole("EMPLOYEE");
		Comments comment = new Comments();
		comment.setId("cmt_del_001");
		comment.setCreatedBy(creator);
		Task task = new Task();
		task.setId("task_del_001");

		when(taskRepo.findById("task_del_001")).thenReturn(Optional.of(task));
		when(commentsRepo.findById("cmt_del_001")).thenReturn(Optional.of(comment));
		when(memberRepo.findById("mem_del_001")).thenReturn(Optional.of(creator));
		doNothing().when(commentsRepo).delete(comment);

		assertDoesNotThrow(() -> taskService.deleteComment("cmt_del_001", "mem_del_001", "task_del_001"));
	}

	@Test
	void testDeleteComment_Success_ByManager() {
		Member manager = new Member();
		manager.setId("mem_mgr_del");
		manager.setRole("MANAGER");
		Member creator = new Member();
		creator.setId("mem_other_del");
		Comments comment = new Comments();
		comment.setId("cmt_del_mgr");
		comment.setCreatedBy(creator);
		Task task = new Task();
		task.setId("task_del_mgr");

		when(taskRepo.findById("task_del_mgr")).thenReturn(Optional.of(task));
		when(commentsRepo.findById("cmt_del_mgr")).thenReturn(Optional.of(comment));
		when(memberRepo.findById("mem_mgr_del")).thenReturn(Optional.of(manager));
		doNothing().when(commentsRepo).delete(comment);

		assertDoesNotThrow(() -> taskService.deleteComment("cmt_del_mgr", "mem_mgr_del", "task_del_mgr"));
	}

	@Test
	void testDeleteComment_Failure_UnauthorizedUser() {
		Member other = new Member();
		other.setId("mem_wrong");
		other.setRole("EMPLOYEE");
		Member creator = new Member();
		creator.setId("mem_creator_del");
		Comments comment = new Comments();
		comment.setId("cmt_del_fail");
		comment.setCreatedBy(creator);
		Task task = new Task();
		task.setId("task_del_fail");

		when(taskRepo.findById("task_del_fail")).thenReturn(Optional.of(task));
		when(commentsRepo.findById("cmt_del_fail")).thenReturn(Optional.of(comment));
		when(memberRepo.findById("mem_wrong")).thenReturn(Optional.of(other));

		AppException exception = assertThrows(AppException.class, () -> {
			taskService.deleteComment("cmt_del_fail", "mem_wrong", "task_del_fail");
		});

		assertEquals("User is not authorized to delete comments", exception.getMessage());
	}

	@Test
	void testDeleteComment_Failure_TaskNotFound() {
		when(taskRepo.findById("invalid_task")).thenReturn(Optional.empty());

		AppException exception = assertThrows(AppException.class, () -> {
			taskService.deleteComment("cmt_id", "mem_id", "invalid_task");
		});

		assertEquals("Task id not found", exception.getMessage());
	}

	@Test
	void testSearchTasksForFirstPage_validMember_returnsGroupedTasks() {
		String memberId = "mem123";
		Member mockMember = new Member();
		mockMember.setId(memberId);
		mockMember.setRole(RoleType.EMPLOYEE.name());

		Project mockProject = new Project();
		mockProject.setId("proj123");

		Task task1 = new Task();
		task1.setStatus("TODO");
		task1.setProject(mockProject);

		Task task2 = new Task();
		task2.setStatus("TODO");
		task2.setProject(mockProject);

		Task task3 = new Task();
		task3.setStatus("DONE");
		task3.setProject(mockProject);

		List<Task> taskList = Arrays.asList(task1, task2, task3);

		CriteriaBuilder cb = mock(CriteriaBuilder.class);
		CriteriaQuery<Task> cq = mock(CriteriaQuery.class);
		Root<Task> root = mock(Root.class);

		Predicate mainPredicate = mock(Predicate.class);
		Predicate inPredicate = mock(Predicate.class);

		Join<Object, Object> projectJoin = mock(Join.class);
		Join<Object, Object> teamsJoin = mock(Join.class);
		Join<Object, Object> membersJoin = mock(Join.class);
		Path<Object> memberIdPath = mock(Path.class);

		when(memberRepo.findByIdAndStatus(memberId, Status.ACTIVE.name())).thenReturn(mockMember);

		when(entityManager.getCriteriaBuilder()).thenReturn(cb);
		when(cb.createQuery(Task.class)).thenReturn(cq);
		when(cq.from(Task.class)).thenReturn(root);
		when(cq.select(root)).thenReturn(cq);

		when(root.join("project")).thenReturn(projectJoin);
		when(projectJoin.join("teams")).thenReturn(teamsJoin);
		when(teamsJoin.join("members")).thenReturn(membersJoin);
		when(membersJoin.get("id")).thenReturn(memberIdPath);

		when(memberIdPath.in(memberId)).thenReturn(inPredicate);
		when(cb.isTrue(inPredicate)).thenReturn(mainPredicate);
		when(cb.and(any(Predicate[].class))).thenReturn(mainPredicate);
		when(cq.where(any(Predicate.class))).thenReturn(cq);
		when(cq.where(any(Predicate[].class))).thenReturn(cq);
		when(cq.distinct(true)).thenReturn(cq);

		Order mockOrder = mock(Order.class);
		Path<Object> modifiedDatePath = mock(Path.class);
		when(root.get("modifiedDate")).thenReturn(modifiedDatePath);
		when(cb.desc(modifiedDatePath)).thenReturn(mockOrder);
		when(cq.orderBy(mockOrder)).thenReturn(cq);

		when(entityManager.createQuery(cq)).thenReturn(typedQuery);
		when(typedQuery.getResultList()).thenReturn(taskList);

		FirstPageTaskResponseDto response = taskService.searchTasksForFirstPage(memberId, null, null, null, null, null,
				null, null, null, null, null, null);

		assertNotNull(response);
		assertEquals(2, response.getTodoTaskCount());
		assertEquals(1, response.getDoneTaskCount());
		assertEquals(2, response.getTasks().get("TODO").size());
		assertEquals(1, response.getTasks().get("DONE").size());

		assertEquals(0, response.getDesignTaskCount());
		assertEquals(0, response.getDevelopmentTaskCount());
		assertEquals(0, response.getTestTaskCount());
		assertEquals(0, response.getBlockerTaskCount());

		verify(memberRepo).findByIdAndStatus(memberId, Status.ACTIVE.name());
		verify(entityManager).getCriteriaBuilder();
		verify(entityManager).createQuery(cq);
		verify(typedQuery).getResultList();
	}

	@Test
	void testSearchTasksForFirstPage_memberNotFound_throwsException() {
		when(memberRepo.findByIdAndStatus("invalid-id", Status.ACTIVE.name())).thenReturn(null);

		AppException exception = assertThrows(AppException.class, () -> {
			taskService.searchTasksForFirstPage("invalid-id", null, null, null, null, null, null, null, null, null,
					null, null);
		});

		assertTrue(exception.getMessage().contains("Member id not found"));
	}

	@Test
	void testSearchTaskPageWise_validMember_returnsPagedTasks() {
		String memberId = "mem123";
		int pageNumber = 0;
		int pageSize = 10;

		Member mockMember = new Member();
		mockMember.setId(memberId);
		mockMember.setRole(RoleType.EMPLOYEE.name());

		Project mockProject = new Project();
		mockProject.setId("proj123");

		Task task1 = new Task();
		task1.setId("task1");
		task1.setStatus("TODO");
		task1.setProject(mockProject);

		Task task2 = new Task();
		task2.setId("task2");
		task2.setStatus("DONE");
		task2.setProject(mockProject);

		List<Task> mockTasks = Arrays.asList(task1, task2);

		TaskResponseDto dto1 = new TaskResponseDto();
		TaskResponseDto dto2 = new TaskResponseDto();

		when(memberRepo.findByIdAndStatus(memberId, Status.ACTIVE.name())).thenReturn(mockMember);

		CriteriaQuery<Task> mockCriteriaQuery = mock(CriteriaQuery.class);

		doReturn(mockCriteriaQuery).when(taskService).filterTasks(any(), any(), any(), any(), any(), any(), any(),
				any(), any(), any(), any(), any());

		when(entityManager.createQuery(mockCriteriaQuery)).thenReturn(typedQuery);
		when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
		when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
		when(typedQuery.getResultList()).thenReturn(mockTasks);

		doReturn(dto1).when(taskService).convertTaskToDto(task1, mockMember);
		doReturn(dto2).when(taskService).convertTaskToDto(task2, mockMember);

		Page<Object> result = taskService.searchTaskPageWise("TODO", pageNumber, pageSize, memberId, null, null, null,
				null, null, null, null, null, null, null);

		assertNotNull(result);
		assertEquals(2, result.getContent().size());
		assertTrue(result.getContent().contains(dto1));
		assertTrue(result.getContent().contains(dto2));
		assertEquals(0, result.getNumber());
		assertEquals(10, result.getSize());

		verify(memberRepo).findByIdAndStatus(memberId, Status.ACTIVE.name());
		verify(entityManager).createQuery(mockCriteriaQuery);
		verify(typedQuery).setFirstResult(0);
		verify(typedQuery).setMaxResults(10);
		verify(typedQuery).getResultList();
		verify(taskService).convertTaskToDto(task1, mockMember);
		verify(taskService).convertTaskToDto(task2, mockMember);
	}

	@Test
	void testSearchTaskPageWise_invalidMember_throwsNotFoundException() {
	    String invalidMemberId = "invalid123";

	    when(memberRepo.findByIdAndStatus(invalidMemberId, Status.ACTIVE.name())).thenReturn(null);

	    AppException exception = assertThrows(
	    		AppException.class,
	        () -> taskService.searchTaskPageWise(
	            "TODO", 0, 10, invalidMemberId,
	            null, null, null, null, null, null, null, null, null, null)
	    );

	    assertEquals("Member id not found", exception.getMessage());

	    verify(memberRepo).findByIdAndStatus(invalidMemberId, Status.ACTIVE.name());
	    verifyNoInteractions(entityManager);
	}

}
