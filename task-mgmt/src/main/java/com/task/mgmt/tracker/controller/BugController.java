package com.task.mgmt.tracker.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.task.mgmt.tracker.request.payload.BugRequestDto;
import com.task.mgmt.tracker.request.payload.BugRequestUpdateDto;
import com.task.mgmt.tracker.response.payload.DetailedTaskResponseDto;
import com.task.mgmt.tracker.service.BugService;
import com.task.mgmt.tracker.utils.AppUtils;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/bugs")
@CrossOrigin("*")
@SecurityRequirement(name = "token")
public class BugController {

	private static final Logger LOGGER = LoggerFactory.getLogger(BugController.class);

	@Autowired
	BugService bugService;

	@PostMapping
	public ResponseEntity<DetailedTaskResponseDto> reportBugs(@Valid @RequestBody BugRequestDto dto) {
		String loggedUser = AppUtils.getLoggedInUser();
		LOGGER.info("Bug report request received by user: {} for taskId: {}", loggedUser, dto.getTaskId());
		DetailedTaskResponseDto response = bugService.reportBugs(dto, loggedUser);
		LOGGER.info("Bug reported successfully for taskId: {} by user: {}", dto.getTaskId(), loggedUser);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{taskId}")
	public ResponseEntity<Void> updateBugStatus(@PathVariable(name = "taskId") String taskId,
			@Valid @RequestBody List<BugRequestUpdateDto> updates) {

		String loggedUser = AppUtils.getLoggedInUser();
		LOGGER.info("Bug status update request received for taskId: {} by user: {}", taskId, loggedUser);
		bugService.updateBugStatus(taskId, updates, loggedUser);
		LOGGER.info("Bug status updated successfully for taskId: {} by user: {}", taskId, loggedUser);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{taskId}/{bugId}")
	public ResponseEntity<String> deleteBug(@PathVariable(name = "taskId") String taskId,
			@PathVariable(name = "bugId") Long bugId) {
		String loggedUser = AppUtils.getLoggedInUser();
		LOGGER.info("Bug delete request received for taskId: {}, bugId: {} by user: {}", taskId, bugId, loggedUser);
		bugService.deleteBug(taskId, loggedUser, bugId);
		LOGGER.info("Bug deleted successfully. taskId: {}, bugId: {}, deletedBy: {}", taskId, bugId, loggedUser);
		return ResponseEntity.ok("Bug deleted successfully");
	}
}
