package com.task.mgmt.tracker.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.task.mgmt.tracker.entity.Attachment;
import com.task.mgmt.tracker.entity.Comments;
import com.task.mgmt.tracker.entity.TaskAssignment;
import com.task.mgmt.tracker.request.payload.BugRequestDto;
import com.task.mgmt.tracker.request.payload.BugRequestUpdateDto;
import com.task.mgmt.tracker.request.payload.CommentsRequestDto;
import com.task.mgmt.tracker.request.payload.CreateTaskRequestDto;
import com.task.mgmt.tracker.request.payload.TaskStatusChangeDto;
import com.task.mgmt.tracker.request.payload.UpdateCommentsDo;
import com.task.mgmt.tracker.request.payload.UpdateTaskRequestDto;
import com.task.mgmt.tracker.request.payload.WorkStatusUpdateDto;
import com.task.mgmt.tracker.response.payload.DetailedTaskResponseDto;
import com.task.mgmt.tracker.response.payload.FirstPageTaskResponseDto;
import com.task.mgmt.tracker.response.payload.MessageDto;
import com.task.mgmt.tracker.service.AttachmentService;
import com.task.mgmt.tracker.service.TaskService;
import com.task.mgmt.tracker.utils.AppUtils;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/tasks")
@CrossOrigin("*")
@SecurityRequirement(name = "token")
public class TaskController {

	@Autowired
	TaskService taskService;

	@Autowired
	AttachmentService attachmentService;

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

	@PostMapping
	public ResponseEntity<DetailedTaskResponseDto> createTask(@Valid @RequestBody CreateTaskRequestDto taskRequestDto) {
		String loggedUser = AppUtils.getLoggedInUser();
		LOGGER.info("Received request to create a new task, createdBy ID: {}", loggedUser);
		DetailedTaskResponseDto createdTask = taskService.createTask(loggedUser, taskRequestDto);
		LOGGER.info("Task created successfully, createdBy ID: {}", loggedUser);
		return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
	}

	@PutMapping("/{taskId}")
	public ResponseEntity<DetailedTaskResponseDto> updateTask(@PathVariable("taskId") String taskId,
			@Valid @RequestBody UpdateTaskRequestDto taskRequestDto) {
		String loggedUser = AppUtils.getLoggedInUser();
		LOGGER.info("Received request to update task with ID: {} modifyedBy ID :{}", taskId, loggedUser);
		DetailedTaskResponseDto updatedTask = taskService.updateTask(taskId, loggedUser, taskRequestDto);
		LOGGER.info("Task with ID {} updated successfully", taskId);
		return new ResponseEntity<>(updatedTask, HttpStatus.OK);
	}

	@GetMapping("/{taskId}")
	public ResponseEntity<DetailedTaskResponseDto> getTask(@PathVariable("taskId") String taskId) {
		String loggedUser = AppUtils.getLoggedInUser();
		LOGGER.info("Received request to get task with ID: {}, and memberId {}", taskId, loggedUser);
		DetailedTaskResponseDto task = taskService.getTask(taskId, loggedUser);
		LOGGER.info("Task with ID {} get successfully", taskId);
		return new ResponseEntity<>(task, HttpStatus.OK);
	}

	@PutMapping("/work-status")
	public ResponseEntity<TaskAssignment> updateWorkStatus(@Valid @RequestBody WorkStatusUpdateDto dto) {
		String loggedUser = AppUtils.getLoggedInUser();
		TaskAssignment updated = taskService.updateWorkStatus(loggedUser, dto);
		return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/{taskId}")
	public ResponseEntity<MessageDto> deleteMainTask(@PathVariable("taskId") String taskId) {
		String loggedUser = AppUtils.getLoggedInUser();
		LOGGER.info("Received request to delete main task with ID: {} by admin with ID: {}", taskId, loggedUser);
		taskService.deleteMainTask(taskId, loggedUser);
		LOGGER.info("Main task with ID {} deleted successfully by admin with ID {}", taskId, loggedUser);
		MessageDto deleteMember = new MessageDto("Task deleted successfully", LocalDateTime.now());
		return new ResponseEntity<MessageDto>(deleteMember, HttpStatus.ACCEPTED);
	}

	@PutMapping("/status/{taskId}")
	public ResponseEntity<MessageDto> updateTaskStatus(@PathVariable("taskId") String taskId,
			@Valid @RequestBody TaskStatusChangeDto statusChangeDto) {
		String loggedUser = AppUtils.getLoggedInUser();
		LOGGER.info(
				"Received a request to update the status of the task with ID: {}, to status: {}, by member with ID: {}",
				taskId, statusChangeDto.getStatus(), loggedUser);
		taskService.taskStatusChange(taskId, loggedUser, statusChangeDto);
		LOGGER.info("The status of the task with ID {} has been updated successfully to {}, by member with ID: {}",
				taskId, statusChangeDto.getStatus(), loggedUser);
		MessageDto deleteMember = new MessageDto("Task status updated successfully", LocalDateTime.now());
		return new ResponseEntity<MessageDto>(deleteMember, HttpStatus.ACCEPTED);
	}

//	@GetMapping("/search")
//	public ResponseEntity<?> searchTask(@RequestParam(required = false, value = "title") String title,
//			@RequestParam(required = false, value = "projectName") String projectName,
//			@RequestParam(required = false, value = "teamName") String teamName,
//			@RequestParam(required = false, value = "memberName") String memberName,
//			@RequestParam(required = false, value = "taskStatus") String taskStatus,
//			@RequestParam(required = false, value = "completedDate") LocalDate completedDate,
//			@RequestParam(required = false, value = "taskPriority") String taskPriority,
//			@RequestParam(required = false, value = "startDate") LocalDate startDate,
//			@RequestParam(required = false, value = "endDate") LocalDate endDate,
//			@RequestParam(required = false, value = "assignedDate") LocalDate assignedDate,
//			@RequestParam(required = false, value = "tag") String tag) {
//		String loggedUser = AppUtils.getLoggedInUser();
//		LOGGER.info("search by task : {}", loggedUser);
//		Map<String, List<TaskResponseDto>> searchTasks = taskService.searchTasks(loggedUser, title, projectName,
//				teamName, memberName, taskStatus, completedDate, taskPriority, startDate, endDate, assignedDate, tag);
//		LOGGER.info("get all task ={} : " + searchTasks.size());
//		return new ResponseEntity<>(searchTasks, HttpStatus.OK);
//	}

	@PostMapping("/comment")
	public ResponseEntity<Comments> createCommentForTask(@Valid @RequestBody CommentsRequestDto dto) {
		String loggedUser = AppUtils.getLoggedInUser();
		Comments createdComment = taskService.createCommentForTask(loggedUser, dto);
		return new ResponseEntity<Comments>(createdComment, HttpStatus.CREATED);
	}

	@GetMapping("/comments/{taskId}")
	public ResponseEntity<List<Comments>> getCommentsByTaskId(@PathVariable String taskId) {
		List<Comments> response = taskService.getCommentsByTaskId(taskId);
		return new ResponseEntity<List<Comments>>(response, HttpStatus.OK);
	}

	@PutMapping("/comment")
	public ResponseEntity<Comments> updateComments(@Valid @RequestBody UpdateCommentsDo dto) {
		String loggedUser = AppUtils.getLoggedInUser();
		Comments updatedComment = taskService.updateComments(loggedUser, dto);
		return new ResponseEntity<Comments>(updatedComment, HttpStatus.OK);
	}

	@DeleteMapping("/comment/{commentId}/{taskId}")
	public ResponseEntity<MessageDto> deleteComment(@PathVariable("commentId") String commentId,
			@PathVariable("taskId") String taskId) {
		String loggedUser = AppUtils.getLoggedInUser();
		taskService.deleteComment(commentId, loggedUser, taskId);
		MessageDto deleteComment = new MessageDto("Comment deleted successfully", LocalDateTime.now());
		return new ResponseEntity<MessageDto>(deleteComment, HttpStatus.OK);
	}

	@PostMapping("/attachment")
	public ResponseEntity<Attachment> createAttachment(@RequestPart("requestAttachment") String requestAttachment,
			@RequestPart("file") MultipartFile file) throws IOException {
		LOGGER.info("Received request to create attachment");
		String loggedUser = AppUtils.getLoggedInUser();
		Attachment attachment = attachmentService.createAttachment(requestAttachment, loggedUser, file);
		LOGGER.info("Attachment created successfully");
		return new ResponseEntity<>(attachment, HttpStatus.CREATED);
	}

	@DeleteMapping("/attachment/{attachmentId}")
	public ResponseEntity<MessageDto> deleteAttachment(@PathVariable("attachmentId") String attachmentId) {
		String loggedUser = AppUtils.getLoggedInUser();
		LOGGER.info("Received request to delete attachment with ID: {} by member ID: {}", attachmentId, loggedUser);
		attachmentService.deleteAttachment(attachmentId, loggedUser);
		LOGGER.info("Attachment deleted successfully");

		MessageDto deleteAttachment = new MessageDto("Attachment deleted successfully", LocalDateTime.now());
		return new ResponseEntity<MessageDto>(deleteAttachment, HttpStatus.ACCEPTED);
	}

	@GetMapping("/search/first")
	public ResponseEntity<FirstPageTaskResponseDto> searchTasksForFirstPage(
			@RequestParam(name = "title", required = false) String title,
			@RequestParam(name = "projectName", required = false) String projectName,
			@RequestParam(name = "teamName", required = false) String teamName,
			@RequestParam(name = "searchMemberId", required = false) String searchMemberId,
			@RequestParam(name = "taskStatus", required = false) String taskStatus,
			@RequestParam(name = "completedDate", required = false) LocalDate completedDate,
			@RequestParam(name = "taskPriority", required = false) String taskPriority,
			@RequestParam(name = "startDate", required = false) LocalDate startDate,
			@RequestParam(name = "endDate", required = false) LocalDate endDate,
			@RequestParam(name = "assignedDate", required = false) LocalDate assignedDate,
			@RequestParam(name = "tag", required = false) String tag) {
		String loggedUser = AppUtils.getLoggedInUser();

		FirstPageTaskResponseDto tasks = taskService.searchTasksForFirstPage(loggedUser, title, projectName, teamName,
				searchMemberId, taskStatus, completedDate, taskPriority, startDate, endDate, assignedDate, tag);

		return new ResponseEntity<>(tasks, HttpStatus.OK);
	}

	@GetMapping("/search/page")
	public ResponseEntity<Page<Object>> searchTasksByPage(@RequestParam(name = "status", required = true) String status,
			@RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
			@RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
			@RequestParam(name = "title", required = false) String title,
			@RequestParam(name = "projectName", required = false) String projectName,
			@RequestParam(name = "teamName", required = false) String teamName,
			@RequestParam(name = "searchMemberId", required = false) String searchMemberId,
			@RequestParam(name = "completedDate", required = false) LocalDate completedDate,
			@RequestParam(name = "taskPriority", required = false) String taskPriority,
			@RequestParam(name = "startDate", required = false) LocalDate startDate,
			@RequestParam(name = "endDate", required = false) LocalDate endDate,
			@RequestParam(name = "assignedDate", required = false) LocalDate assignedDate,
			@RequestParam(name = "tag", required = false) String tag) {
		String loggedUser = AppUtils.getLoggedInUser();

		Page<Object> tasks = taskService.searchTaskPageWise(status, pageNumber, pageSize, loggedUser, title,
				projectName, teamName, searchMemberId, startDate, endDate, taskPriority, assignedDate, completedDate,
				tag);

		return new ResponseEntity<>(tasks, HttpStatus.OK);
	}

}
