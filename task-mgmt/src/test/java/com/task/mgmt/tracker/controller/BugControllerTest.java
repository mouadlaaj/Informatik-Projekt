package com.task.mgmt.tracker.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.mgmt.tracker.exception.AppException;
import com.task.mgmt.tracker.exception.GlobalExceptionHandling;
import com.task.mgmt.tracker.request.payload.BugRequestDto;
import com.task.mgmt.tracker.request.payload.BugRequestDto.BugItemDto;
import com.task.mgmt.tracker.request.payload.BugRequestUpdateDto;
import com.task.mgmt.tracker.response.payload.DetailedTaskResponseDto;
import com.task.mgmt.tracker.service.BugService;

public class BugControllerTest {

	private MockMvc mockMvc;

	@Mock
	private BugService bugService;

	@InjectMocks
	private BugController bugController;

	private ObjectMapper objectMapper;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(bugController).setControllerAdvice(new GlobalExceptionHandling())
				.build();
		objectMapper = new ObjectMapper();
	}

	@Test
	void reportBugs_ValidRequest_ShouldReturn200() throws Exception {
		BugRequestDto dto = new BugRequestDto();
		dto.setTaskId("task_001");

		BugItemDto bug = new BugItemDto();
		bug.setTitle("UI Issue");
		bug.setDescription("Button misaligned");
		bug.setSeverity("HIGH");

		dto.setBugs(List.of(bug));

		when(bugService.reportBugs(eq(dto), anyString())).thenReturn(new DetailedTaskResponseDto());

		mockMvc.perform(post("/api/v1/bugs").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk());
	}

	@Test
	void updateBugStatus_ValidRequest_ShouldReturn200() throws Exception {
		BugRequestUpdateDto dto = new BugRequestUpdateDto();
		dto.setId(1L);
		dto.setStatus("FIXED");

		doNothing().when(bugService).updateBugStatus(eq("task_001"), eq(List.of(dto)), anyString());

		mockMvc.perform(put("/api/v1/bugs/task_001").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(List.of(dto)))).andExpect(status().isOk());
	}

	@Test
	void deleteBug_ValidRequest_ShouldReturn200() throws Exception {
		doNothing().when(bugService).deleteBug(eq("task_001"), anyString(), eq(1L));

		mockMvc.perform(delete("/api/v1/bugs/task_001/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$").value("Bug deleted successfully"));
	}

	@Test
	void reportBugs_MissingTaskId_ShouldReturn400() throws Exception {
		BugRequestDto dto = new BugRequestDto();
		dto.setTaskId("");
		dto.setBugs(List.of());

		mockMvc.perform(post("/api/v1/bugs").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest());
	}

	@Test
	void updateBugStatus_InvalidStatus_ShouldReturn400() throws Exception {
		BugRequestUpdateDto dto = new BugRequestUpdateDto();
		dto.setId(1L);
		dto.setStatus("INVALID");

		mockMvc.perform(put("/api/v1/bugs/task_001").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(List.of(dto)))).andExpect(status().isBadRequest());
	}

	@Test
	void deleteBug_InvalidBugId_ShouldReturn400() throws Exception {

		doThrow(new AppException("Bug not found")).when(bugService).deleteBug(eq("invalid_task"), anyString(),
				eq(999L));

		mockMvc.perform(delete("/api/v1/bugs/invalid_task/mock_user_id")).andExpect(status().isBadRequest());
	}

}
