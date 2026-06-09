package com.task.mgmt.tracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.mgmt.tracker.exception.AppException;
import com.task.mgmt.tracker.exception.GlobalExceptionHandling;
import com.task.mgmt.tracker.request.payload.CreateTeamRequestDto;
import com.task.mgmt.tracker.request.payload.UpdateTeamMemberRequestDto;
import com.task.mgmt.tracker.request.payload.UpdateTeamRequestDto;
import com.task.mgmt.tracker.response.payload.CommonTeamResponseDto;
import com.task.mgmt.tracker.service.TeamService;

@Import(GlobalExceptionHandling.class)
public class TeamControllerTest {

	private MockMvc mockMvc;

	@Mock
	private TeamService teamService;

	@InjectMocks
	private TeamController teamController;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private CreateTeamRequestDto createDto;
	private UpdateTeamRequestDto updateDto;
	private UpdateTeamMemberRequestDto memberDto;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(teamController).setControllerAdvice(new GlobalExceptionHandling())
				.build();

		createDto = new CreateTeamRequestDto();
		createDto.setTeamLeadId("mem001");
		createDto.setTeamName("Alpha Team");
		createDto.setTeamMembers(List.of("mem002", "mem003"));

		updateDto = new UpdateTeamRequestDto();
		updateDto.setTeamLeadId("mem001");
		updateDto.setTeamName("Updated Team");
		updateDto.setTeamMembers(List.of("mem004"));

		memberDto = new UpdateTeamMemberRequestDto();
		memberDto.setTeamId("team001");
		memberDto.setTeamMembers(List.of("mem005"));
	}

	@Test
	void createTeam_Valid_ShouldReturn201() throws Exception {
		CommonTeamResponseDto response = new CommonTeamResponseDto();
		response.setTeamName("Alpha Team");
		when(teamService.addNewTeam(any(), any())).thenReturn(response);

		mockMvc.perform(post("/api/v1/teams").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.teamName").value("Alpha Team"));
	}

	@Test
	void createTeam_Invalid_ShouldReturn400() throws Exception {
		CreateTeamRequestDto invalidDto = new CreateTeamRequestDto();
		mockMvc.perform(post("/api/v1/teams").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidDto))).andExpect(status().isBadRequest());
	}

	@Test
	void updateTeam_Valid_ShouldReturn200() throws Exception {
		CommonTeamResponseDto response = new CommonTeamResponseDto();
		response.setTeamName("Updated Team");
		when(teamService.updateTeamById(any(), any(), any())).thenReturn(response);

		mockMvc.perform(put("/api/v1/teams/team001").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto))).andExpect(status().isOk())
				.andExpect(jsonPath("$.teamName").value("Updated Team"));
	}

	@Test
	void updateTeam_Invalid_ShouldReturn400() throws Exception {
		UpdateTeamRequestDto invalidDto = new UpdateTeamRequestDto();
		mockMvc.perform(put("/api/v1/teams/team001").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidDto))).andExpect(status().isBadRequest());
	}

	@Test
	void addTeamMember_Valid_ShouldReturn200() throws Exception {
		CommonTeamResponseDto response = new CommonTeamResponseDto();
		when(teamService.addTeamMember(any(), any())).thenReturn(response);

		mockMvc.perform(post("/api/v1/teams/member").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(memberDto))).andExpect(status().isOk());
	}

	@Test
	void addTeamMember_Invalid_ShouldReturn400() throws Exception {
		UpdateTeamMemberRequestDto invalidDto = new UpdateTeamMemberRequestDto();
		mockMvc.perform(post("/api/v1/teams/member").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidDto))).andExpect(status().isBadRequest());
	}

	@Test
	void getTeamDetails_Valid_ShouldReturn200() throws Exception {
		when(teamService.getTeamDetails("team001")).thenReturn(new CommonTeamResponseDto());
		mockMvc.perform(get("/api/v1/teams/team001")).andExpect(status().isOk());
	}

	@Test
	void deleteTeam_Valid_ShouldReturn200() throws Exception {
		doNothing().when(teamService).deleteTeamById(any(), any());
		mockMvc.perform(delete("/api/v1/teams/team001")).andExpect(status().isOk());
	}

	@Test
	void removeTeamMember_Valid_ShouldReturn202() throws Exception {
		doNothing().when(teamService).removeTeamMember(any(), any(), any());
		mockMvc.perform(delete("/api/v1/teams/member/team001/mem005")).andExpect(status().isAccepted());
	}

	@Test
	void getAllTeamsByMember_ShouldReturn200() throws Exception {
		when(teamService.getAllTeamsByMemberId(any())).thenReturn(List.of("team001", "team002"));
		mockMvc.perform(get("/api/v1/teams/in")).andExpect(status().isOk());
	}

	@Test
	void getAllTeamsByAdmin_ShouldReturn200() throws Exception {
		when(teamService.getAllTeamsUnderAdmin(any())).thenReturn(List.of());
		mockMvc.perform(get("/api/v1/teams/admin")).andExpect(status().isOk());
	}

	@Test
	void searchTeams_ValidSearch_ShouldReturn200() throws Exception {
		when(teamService.searchTeams("Alpha")).thenReturn(List.of());
		mockMvc.perform(get("/api/v1/teams").param("search", "Alpha")).andExpect(status().isOk());
	}

	@Test
	void searchTeams_NullSearch_ShouldReturn200() throws Exception {
		when(teamService.searchTeams(null)).thenReturn(List.of());
		mockMvc.perform(get("/api/v1/teams")).andExpect(status().isOk());
	}
 
	@Test
	void removeTeamMember_InvalidPath_ShouldReturn404() throws Exception {
		mockMvc.perform(delete("/api/v1/teams/member/only-one-id")).andExpect(status().isNotFound());
	}

	@Test
	void getTeamDetails_InvalidId_ShouldReturn400() throws Exception {
		when(teamService.getTeamDetails("invalid-id")).thenThrow(new AppException("Team not found"));

		mockMvc.perform(get("/api/v1/teams/invalid-id")).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Team not found"))
				.andExpect(jsonPath("$.status").value("400 BAD_REQUEST"));
	}

	@Test
	void searchTeams_WithSpecialCharacters_ShouldReturn200() throws Exception {
		when(teamService.searchTeams("@#%&!")).thenReturn(List.of());

		mockMvc.perform(get("/api/v1/teams").param("search", "@#%&!")).andExpect(status().isOk());
	}

	@Test
	void searchTeams_EmptyList_ShouldReturn200() throws Exception {
		when(teamService.searchTeams("NonExistent")).thenReturn(List.of());

		mockMvc.perform(get("/api/v1/teams").param("search", "NonExistent")).andExpect(status().isOk())
				.andExpect(content().json("[]"));
	}

	@Test
	void getAllTeamsByAdmin_EmptyResult_ShouldReturn200() throws Exception {
		when(teamService.getAllTeamsUnderAdmin(any())).thenReturn(List.of());

		mockMvc.perform(get("/api/v1/teams/admin")).andExpect(status().isOk()).andExpect(content().json("[]"));
	}

}
