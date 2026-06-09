package com.task.mgmt.tracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.task.mgmt.tracker.exception.AppException;
import com.task.mgmt.tracker.exception.GlobalExceptionHandling;
import com.task.mgmt.tracker.response.payload.DesignationWiseReportStatus;
import com.task.mgmt.tracker.response.payload.ReportResponseDto;
import com.task.mgmt.tracker.response.payload.TaskReportResponseDto;
import com.task.mgmt.tracker.response.payload.TaskReportResponseDto.TaskMemberWorkInfo;
import com.task.mgmt.tracker.service.ReportService;

class ReportControllerTest {

	private MockMvc mockMvc;

	@Mock
	private ReportService reportService;

	@InjectMocks
	private ReportController reportController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(reportController).setControllerAdvice(new GlobalExceptionHandling())
				.build();
	}

	@Test
	void getTaskReport_ValidRequest_ShouldReturn200() throws Exception {
		TaskReportResponseDto dto = new TaskReportResponseDto();
		dto.setTeamId("tm_001");
		dto.setTeamName("Team Alpha");
		dto.setTeamOrder(1);

		TaskMemberWorkInfo info = new TaskMemberWorkInfo();
		info.setMemberId("mem_001");
		info.setMemberName("John Doe");
		info.setTaskId("tsk_001");
		info.setTaskTitle("Design Dashboard");
		info.setEstimatedTime("4h");
		info.setActualSpentTime("3.5h");
		info.setPercentage("87.5%");
		info.setDesignation("Designer");
		info.setDate(LocalDateTime.now());

		dto.setWorkInfo(info);

		when(reportService.getTaskWorkReportFiltered(eq(null), any(), any(), any(), any())).thenReturn(List.of(dto));

		mockMvc.perform(get("/api/v1/report/task-report").param("teamId", "team_001").param("memberId", "mem_001")
				.param("startDate", "2025-07-01").param("endDate", "2025-07-10")).andExpect(status().isOk());
	}

	@Test
	void generateTaskReport_ValidDateRange_ShouldReturn200() throws Exception {
		ReportResponseDto dto = new ReportResponseDto();
		dto.setTasksInTodo(5);
		dto.setTasksInDesign(10);
		dto.setTasksInDevelopment(8);
		dto.setTasksInTest(3);
		dto.setTasksInDone(7);

		when(reportService.generateTaskReport(any(), any(), eq(null))).thenReturn(dto);

		mockMvc.perform(
				get("/api/v1/report/overall-task").param("startDate", "2025-07-01").param("endDate", "2025-07-31"))
				.andExpect(status().isOk());
	}

	@Test
	void getDesignationWiseReportStatus_Valid_ShouldReturn200() throws Exception {
		DesignationWiseReportStatus status = new DesignationWiseReportStatus();
		DesignationWiseReportStatus.Developer developer = new DesignationWiseReportStatus.Developer();
		developer.setTotalCount(4);
		status.setDeveloper(developer);

		when(reportService.getDesignationWiseReportStatus(eq(null), eq("developer"))).thenReturn(status);

		mockMvc.perform(get("/api/v1/report/designation-wise-status").param("designation", "developer"))
				.andExpect(status().isOk());
	}

	@Test
	void getTaskReport_InvalidDateFormat_ShouldReturn400() throws Exception {
		mockMvc.perform(get("/api/v1/report/task-report").param("startDate", "invalid-date"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void generateTaskReport_ServiceThrowsException_ShouldReturn400() throws Exception {
		doThrow(new AppException("Invalid request")).when(reportService).generateTaskReport(any(), any(), any());
		mockMvc.perform(
				get("/api/v1/report/overall-task").param("startDate", "2025-07-01").param("endDate", "2025-07-31"))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("Invalid request"));
	}

	@Test
	void getDesignationWiseReportStatus_InvalidDesignation_ShouldReturn400() throws Exception {
		doThrow(new AppException("Designation not found")).when(reportService).getDesignationWiseReportStatus(any(),
				eq("invalid_role"));

		mockMvc.perform(get("/api/v1/report/designation-wise-status").param("designation", "invalid_role"))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("Designation not found"));
	}
}
