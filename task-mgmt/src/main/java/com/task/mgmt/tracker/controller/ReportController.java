package com.task.mgmt.tracker.controller;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.task.mgmt.tracker.response.payload.DesignationWiseReportStatus;
import com.task.mgmt.tracker.response.payload.ReportResponseDto;
import com.task.mgmt.tracker.response.payload.TaskReportResponseDto;
import com.task.mgmt.tracker.service.ReportService;
import com.task.mgmt.tracker.utils.AppUtils;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/report")
@CrossOrigin("*")
@SecurityRequirement(name = "token")
public class ReportController {

	@Autowired
	private ReportService reportService;

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportController.class);

	@GetMapping("/task-report")
	public ResponseEntity<List<TaskReportResponseDto>> getTaskReport(
			@RequestParam(name = "teamId", required = false) String teamId,
			@RequestParam(name = "memberId", required = false) String memberId,
			@RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

		LOGGER.info("Request received for task report | TeamId: {}, MemberId: {}, StartDate: {}, EndDate: {}", teamId,
				memberId, startDate, endDate);

		String loggedUser = AppUtils.getLoggedInUser();
		LOGGER.debug("Logged-in user: {}", loggedUser);

		List<TaskReportResponseDto> report = reportService.getTaskWorkReportFiltered(loggedUser, teamId, memberId,
				startDate, endDate);

		LOGGER.info("Task report generated successfully.");
		return ResponseEntity.ok(report);
	}

	@GetMapping("/overall-task")
	public ResponseEntity<ReportResponseDto> generateTaskReport(
			@RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

		LOGGER.info("Request received to generate overall task report | StartDate: {}, EndDate: {}", startDate,
				endDate);

		String loggedUser = AppUtils.getLoggedInUser();
		LOGGER.debug("Logged-in user: {}", loggedUser);

		ReportResponseDto report = reportService.generateTaskReport(startDate, endDate, loggedUser);

		LOGGER.info("Overall task report generated successfully");
		return new ResponseEntity<>(report, HttpStatus.OK);
	}

	@GetMapping("/designation-wise-status")
	public ResponseEntity<DesignationWiseReportStatus> getDesignationWiseReportStatus(
			@RequestParam(name = "designation", required = false) String designation) {

		LOGGER.info("Request received for designation-wise report status | Designation: {}", designation);

		String loggedUser = AppUtils.getLoggedInUser();
		LOGGER.debug("Logged-in user: {}", loggedUser);

		DesignationWiseReportStatus report = reportService.getDesignationWiseReportStatus(loggedUser, designation);

		LOGGER.info("Designation-wise report status generated successfully");
		return ResponseEntity.ok(report);

	}

}
