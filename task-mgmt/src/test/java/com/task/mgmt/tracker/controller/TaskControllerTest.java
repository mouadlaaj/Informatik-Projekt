package com.task.mgmt.tracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.task.mgmt.tracker.entity.Attachment;
import com.task.mgmt.tracker.entity.Comments;
import com.task.mgmt.tracker.entity.TaskAssignment;
import com.task.mgmt.tracker.exception.AppException;
import com.task.mgmt.tracker.exception.GlobalExceptionHandling;
import com.task.mgmt.tracker.exception.NotFoundException;
import com.task.mgmt.tracker.request.payload.AssigneeDto;
import com.task.mgmt.tracker.request.payload.CommentsRequestDto;
import com.task.mgmt.tracker.request.payload.CreateTaskRequestDto;
import com.task.mgmt.tracker.request.payload.TaskStatusChangeDto;
import com.task.mgmt.tracker.request.payload.UpdateCommentsDo;
import com.task.mgmt.tracker.request.payload.UpdateTaskRequestDto;
import com.task.mgmt.tracker.request.payload.WorkStatusUpdateDto;
import com.task.mgmt.tracker.response.payload.DetailedTaskResponseDto;
import com.task.mgmt.tracker.response.payload.FirstPageTaskResponseDto;
import com.task.mgmt.tracker.service.AttachmentService;
import com.task.mgmt.tracker.service.TaskService;
import com.task.mgmt.tracker.utils.AppUtils;;

public class TaskControllerTest {

	private MockMvc mockMvc;

	@Mock
	private TaskService taskService;

	@InjectMocks
	private TaskController taskController;

	private ObjectMapper objectMapper;

	@Mock
	private AttachmentService attachmentService;

	@BeforeEach
	void setup() {

		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(taskController).setControllerAdvice(new GlobalExceptionHandling())
				.build();
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	@Test
	void createTask_Valid_ShouldReturn201() throws Exception {
		CreateTaskRequestDto requestDto = new CreateTaskRequestDto();
		requestDto.setTitle("New Task");
		requestDto.setShortDescription("Short desc");
		requestDto.setDescription("Long description...");
		requestDto.setProjectId("proj_123");

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		requestDto.setStartDate(LocalDateTime.parse("2025-07-25 14:30:00", formatter).plusDays(1));
		requestDto.setEndDate(LocalDateTime.parse("2025-07-25 19:30:00", formatter).plusDays(1));
		requestDto.setPriority("HIGH");
		requestDto.setComplexity(5);
		requestDto.setTags(List.of("Bug", "UI"));

		AssigneeDto assignee1 = new AssigneeDto();
		assignee1.setMemberId("mem001");
		assignee1.setEstimatedTime(91);

		AssigneeDto assignee2 = new AssigneeDto();
		assignee2.setMemberId("mem002");
		assignee2.setEstimatedTime(60);

		AssigneeDto assignee3 = new AssigneeDto();
		assignee3.setMemberId("mem003");
		assignee3.setEstimatedTime(75);

		requestDto.setAssignedTo(List.of(assignee1, assignee2, assignee3));

		DetailedTaskResponseDto responseDto = new DetailedTaskResponseDto();
		responseDto.setTitle("New Task");

		when(taskService.createTask(any(), any())).thenReturn(responseDto);

		mockMvc.perform(post("/api/v1/tasks").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.title").value("New Task"));
	}

	@Test
	void createTask_Invalid_ShouldReturn400() throws Exception {
		CreateTaskRequestDto invalidDto = new CreateTaskRequestDto();

		mockMvc.perform(post("/api/v1/tasks").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidDto))).andExpect(status().isBadRequest());
	}

	@Test
	void updateTask_ValidInput_ShouldReturn200() throws Exception {
		UpdateTaskRequestDto updateDto = new UpdateTaskRequestDto();
		updateDto.setTitle("Updated Task");
		updateDto.setShortDescription("Updated Short Description");
		updateDto.setDescription("Updated long description...");
		updateDto.setPriority("LOW");
		updateDto.setComplexity(2);
		updateDto.setReason("Need to add more features");

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		updateDto.setStartDate(LocalDateTime.parse("2025-07-20 10:00:00", formatter));
		updateDto.setEndDate(LocalDateTime.parse("2025-07-22 18:00:00", formatter));

		AssigneeDto assignee = new AssigneeDto();
		assignee.setMemberId("mem005");
		updateDto.setAssignedTo(List.of(assignee));
		updateDto.setTags(List.of("Refactor", "Backend"));

		DetailedTaskResponseDto responseDto = new DetailedTaskResponseDto();
		responseDto.setTitle("Updated Task");

		when(taskService.updateTask(any(), any(), any())).thenReturn(responseDto);

		mockMvc.perform(put("/api/v1/tasks/task_001").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto))).andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("Updated Task"));
	}

	@Test
	void updateTask_InvalidInput_ShouldReturn400() throws Exception {
		UpdateTaskRequestDto updateDto = new UpdateTaskRequestDto();
		updateDto.setTitle("T".repeat(100));
		updateDto.setDescription("Some description");
		updateDto.setPriority("HIGH");
		updateDto.setComplexity(3);
		updateDto.setTags(List.of("Invalid"));

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		updateDto.setStartDate(LocalDateTime.parse("2025-07-21 10:00:00", formatter));
		updateDto.setEndDate(LocalDateTime.parse("2025-07-22 15:00:00", formatter));

		AssigneeDto assignee = new AssigneeDto();
		assignee.setMemberId("mem002");
		updateDto.setAssignedTo(List.of(assignee));

		mockMvc.perform(put("/api/v1/tasks/task_002").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto))).andExpect(status().isBadRequest());
	}

	@Test
	void getTask_ValidId_ShouldReturn200() throws Exception {
		String taskId = "task_001";

		DetailedTaskResponseDto responseDto = new DetailedTaskResponseDto();
		responseDto.setId(taskId);
		responseDto.setTitle("Test Task");

		when(taskService.getTask(eq(taskId), any())).thenReturn(responseDto);

		mockMvc.perform(get("/api/v1/tasks/{taskId}", taskId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("task_001")).andExpect(jsonPath("$.title").value("Test Task"));
	}

	@Test
	void getTask_EmptyId_ShouldReturn400() throws Exception {
		String emptyTaskId = "";

		mockMvc.perform(get("/api/v1/tasks/{taskId}", emptyTaskId)).andExpect(status().is4xxClientError());
	}

	@Test
	void updateWorkStatus_ValidInput_ShouldReturn200() throws Exception {
		WorkStatusUpdateDto dto = new WorkStatusUpdateDto();
		dto.setTaskId("task_001");
		dto.setStartTime(LocalDateTime.now().plusMinutes(1));
		dto.setEndTime(LocalDateTime.now().plusMinutes(10));

		TaskAssignment mockAssignment = new TaskAssignment();
		mockAssignment.setId(1L);
		mockAssignment.setStageStart(dto.getStartTime());
		mockAssignment.setStageEnd(dto.getEndTime());

		when(taskService.updateWorkStatus(any(), eq(dto))).thenReturn(mockAssignment);

		mockMvc.perform(put("/api/v1/tasks/work-status").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk());
	}

	@Test
	void updateWorkStatus_MissingStartTime_ShouldReturn400() throws Exception {
		WorkStatusUpdateDto dto = new WorkStatusUpdateDto();
		dto.setTaskId("task_001");
		dto.setEndTime(LocalDateTime.now().plusMinutes(10));
		dto.setStartTime(null);

		mockMvc.perform(put("/api/v1/tasks/work-status").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors").isArray())
				.andExpect(jsonPath("$.errors[0]").value("Start time is required"));
	}

	@Test
	void deleteMainTask_ValidId_ShouldReturn202() throws Exception {
		String validTaskId = "task_001";

		doNothing().when(taskService).deleteMainTask(eq(validTaskId), anyString());

		mockMvc.perform(delete("/api/v1/tasks/{taskId}", validTaskId)).andExpect(status().isAccepted())
				.andExpect(jsonPath("$.message").value("Task deleted successfully"))
				.andExpect(jsonPath("$.time").exists())
				.andDo(result -> System.out.println("RESPONSE: " + result.getResponse().getContentAsString()));
	}

	@Test
	void deleteMainTask_InvalidId_ShouldReturn400() throws Exception {
		doThrow(new NotFoundException("Task not found")).when(taskService).deleteMainTask(any(), any());

		mockMvc.perform(delete("/api/v1/tasks/{taskId}", "invalid_task")).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Task not found"))
				.andExpect(jsonPath("$.status").value("400 BAD_REQUEST"));
	}

	@Test
	void updateTaskStatus_ValidInput_ShouldReturn202() throws Exception {
		TaskStatusChangeDto dto = new TaskStatusChangeDto();
		dto.setStatus("DONE");
		dto.setMessage("Task completed successfully");
		dto.setType("auto");
		dto.setQcFeedback("All checks passed");

		Mockito.doAnswer(invocation -> null).when(taskService).taskStatusChange(eq("task_001"), anyString(), eq(dto));

		mockMvc.perform(put("/api/v1/tasks/status/{taskId}", "task_001").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isAccepted())
				.andExpect(jsonPath("$.message").value("Task status updated successfully"));
	}

	@Test
	void updateTaskStatus_InvalidStatus_ShouldReturn400() throws Exception {
		TaskStatusChangeDto dto = new TaskStatusChangeDto();
		dto.setStatus("INVALID_STATUS");
		dto.setMessage("Trying invalid");
		dto.setType("manual");

		mockMvc.perform(put("/api/v1/tasks/status/{taskId}", "task_001").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0]").value(
						"Status must be one of the following: TODO, DESIGN, DEVELOPMENT, TESTING, DONE, BLOCKER"));
	}

	@Test
	void createCommentForTask_ValidInput_ShouldReturn201() throws Exception {
		CommentsRequestDto dto = new CommentsRequestDto();
		dto.setTaskId("task_001");
		dto.setMessage("This is a test comment");

		Comments mockComment = new Comments();
		mockComment.setId("cmt_001");
		mockComment.setMessage("This is a test comment");

		when(taskService.createCommentForTask(anyString(), eq(dto))).thenReturn(mockComment);

		mockMvc.perform(post("/api/v1/tasks/comment").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isCreated());

	}

	@Test
	void createComment_MissingTaskId_ShouldReturn400() throws Exception {
		CommentsRequestDto dto = new CommentsRequestDto();
		dto.setMessage("Missing taskId");

		mockMvc.perform(post("/api/v1/tasks/comment").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest());
	}

	@Test
	void getCommentsByTaskId_ValidId_ShouldReturn200() throws Exception {
		Comments comment = new Comments();
		comment.setId("cmt_001");
		comment.setMessage("Test comment");

		when(taskService.getCommentsByTaskId("task_001")).thenReturn(List.of(comment));

		mockMvc.perform(get("/api/v1/tasks/comments/task_001")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].message").value("Test comment"));
	}

	@Test
	void getCommentsByTaskId_InvalidId_ShouldReturnEmptyList() throws Exception {
		when(taskService.getCommentsByTaskId("invalid_task")).thenReturn(List.of());

		mockMvc.perform(get("/api/v1/tasks/comments/invalid_task")).andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	void updateComment_ValidRequest_ShouldReturn200() throws Exception {
		UpdateCommentsDo dto = new UpdateCommentsDo();
		dto.setTaskId("task_001");
		dto.setCommentId("cmt_001");
		dto.setMessage("Updated message");

		Comments updated = new Comments();
		updated.setId("cmt_001");
		updated.setMessage("Updated message");

		when(taskService.updateComments(anyString(), eq(dto))).thenReturn(updated);

		mockMvc.perform(put("/api/v1/tasks/comment").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
				.andDo(result -> System.out.println("Response JSON: " + result.getResponse().getContentAsString()))
				.andExpect(status().isOk());
	}

	@Test
	void updateComment_MissingCommentId_ShouldReturn400() throws Exception {
		UpdateCommentsDo dto = new UpdateCommentsDo();
		dto.setTaskId("task_001");
		dto.setMessage("Missing comment ID");

		mockMvc.perform(put("/api/v1/tasks/comment").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest());
	}

	@Test
	void deleteComment_ValidRequest_ShouldReturn200() throws Exception {
		doAnswer(invocation -> null).when(taskService).deleteComment(eq("cmt_001"), anyString(), eq("task_001"));

		mockMvc.perform(delete("/api/v1/tasks/comment/cmt_001/task_001")).andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Comment deleted successfully"));
	}

	@Test
	void deleteComment_TaskIdNotFound_ShouldReturn400() throws Exception {
		MockedStatic<AppUtils> mockedAppUtils = mockStatic(AppUtils.class);
		mockedAppUtils.when(AppUtils::getLoggedInUser).thenReturn("mock_user_id");

		doThrow(new AppException("Task id not found")).when(taskService).deleteComment(eq("cmt_001"),
				eq("mock_user_id"), eq("invalid_task"));

		mockMvc.perform(delete("/api/v1/tasks/comment/cmt_001/invalid_task")).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Task id not found"));
	}

	@Test
	void createAttachment_ValidRequest_ShouldReturn201() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain",
				"Sample file content".getBytes());

		String jsonPart = "{\"taskId\":\"task_001\",\"fileName\":\"test.txt\"}";

		MockMultipartFile requestAttachment = new MockMultipartFile("requestAttachment", "", "application/json",
				jsonPart.getBytes());

		Attachment attachment = new Attachment();
		attachment.setId("att_001");

		when(attachmentService.createAttachment(eq(jsonPart), anyString(), eq(file))).thenReturn(attachment);

		mockMvc.perform(multipart("/api/v1/tasks/attachment").file(requestAttachment).file(file)
				.contentType(MediaType.MULTIPART_FORM_DATA)).andExpect(status().isCreated());
	}

	@Test
	void createAttachment_MissingFile_ShouldReturn400() throws Exception {
		String jsonPart = "{\"taskId\":\"task_001\",\"fileName\":\"test.txt\"}";

		MockMultipartFile requestAttachment = new MockMultipartFile("requestAttachment", "", "application/json",
				jsonPart.getBytes());

		mockMvc.perform(multipart("/api/v1/tasks/attachment").file(requestAttachment)
				.contentType(MediaType.MULTIPART_FORM_DATA)).andExpect(status().isBadRequest());
	}

	@Test
	void deleteAttachment_ValidId_ShouldReturn202() throws Exception {
		doNothing().when(attachmentService).deleteAttachment(eq("att_001"), anyString());

		mockMvc.perform(delete("/api/v1/tasks/attachment/att_001")).andExpect(status().isAccepted())
				.andExpect(jsonPath("$.message").value("Attachment deleted successfully"));
	}

	@Test
	void deleteAttachment_InvalidId_ShouldReturn400() throws Exception {
		doThrow(new AppException("Attachment not found")).when(attachmentService).deleteAttachment(eq("invalid_att"),
				anyString());

		mockMvc.perform(delete("/api/v1/tasks/attachment/invalid_att")).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Attachment not found"));
	}

	@Test
	void searchTasksForFirstPage_ValidFilters_ShouldReturn200() throws Exception {
		FirstPageTaskResponseDto mockResponse = new FirstPageTaskResponseDto();

		when(taskService.searchTasksForFirstPage(anyString(), any(), any(), any(), any(), any(), any(), any(), any(),
				any(), any(), any())).thenReturn(mockResponse);

		mockMvc.perform(get("/api/v1/tasks/search/first").param("title", "UI Fix").param("projectName", "Tracker")
				.param("teamName", "Frontend").param("searchMemberId", "mem_001").param("taskStatus", "DONE")
				.param("taskPriority", "HIGH").param("completedDate", "2025-07-01").param("startDate", "2025-06-01")
				.param("endDate", "2025-07-15").param("assignedDate", "2025-06-28").param("tag", "bugfix"))
				.andExpect(status().isOk());
	}

	@Test
	void searchTasksForFirstPage_InvalidDateFormat_ShouldReturn400() throws Exception {
		mockMvc.perform(get("/api/v1/tasks/search/first").param("completedDate", "invalid-date"))
				.andExpect(status().isBadRequest());
	}

}
