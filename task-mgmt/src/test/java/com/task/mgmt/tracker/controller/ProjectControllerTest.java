package com.task.mgmt.tracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
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
import com.task.mgmt.tracker.request.payload.AddTagsToProjectDto;
import com.task.mgmt.tracker.request.payload.AddTeamProjectRequestDto;
import com.task.mgmt.tracker.request.payload.CreateProjectRequestDto;
import com.task.mgmt.tracker.request.payload.RemoveTagsFromProjectDto;
import com.task.mgmt.tracker.request.payload.UpdateProjectRequestDto;
import com.task.mgmt.tracker.response.payload.CommonProjectResponseDto;
import com.task.mgmt.tracker.service.ProjectService;

@Import(GlobalExceptionHandling.class)
public class ProjectControllerTest {

	private MockMvc mockMvc;

	@Mock
	private ProjectService projectService;

	@InjectMocks
	private ProjectController projectController;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
	    MockitoAnnotations.openMocks(this);
	    mockMvc = MockMvcBuilders
	                .standaloneSetup(projectController)
	                .setControllerAdvice(new GlobalExceptionHandling())
	                .build();
	}


	@Test
	void testCreateProject_Positive() throws Exception {
		CreateProjectRequestDto dto = new CreateProjectRequestDto();
		dto.setProjectName("Test Project");
		dto.setDescription("Test Description");
		dto.setTeams(List.of("team1"));
		dto.setTags(List.of("tag1"));

		CommonProjectResponseDto response = new CommonProjectResponseDto();
		response.setId("p123");
		response.setProjectName("Test Project");

		when(projectService.createProject(any(), any())).thenReturn(response);

		mockMvc.perform(post("/api/v1/project")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").value("p123"))
			.andExpect(jsonPath("$.projectName").value("Test Project"));
	}


	@Test
	void testCreateProject_Negative() throws Exception {
		CreateProjectRequestDto dto = new CreateProjectRequestDto();
		mockMvc.perform(post("/api/v1/project").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest());
	}

	@Test
	void testUpdateProject_Positive() throws Exception {
		UpdateProjectRequestDto dto = new UpdateProjectRequestDto();
		dto.setProjectName("Updated");
		dto.setTeams(List.of("team1"));

		CommonProjectResponseDto response = new CommonProjectResponseDto();
		response.setProjectName("Updated");
		response.setId("123");

		when(projectService.updateProject(any(), any(), any())).thenReturn(response);

		mockMvc.perform(put("/api/v1/project/123")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.projectName").value("Updated"))
			.andExpect(jsonPath("$.id").value("123"));
	}


	@Test
	void testUpdateProject_Negative() throws Exception {
		UpdateProjectRequestDto dto = new UpdateProjectRequestDto();
		mockMvc.perform(put("/api/v1/project/123").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest());
	}

	@Test
	void testDeleteProject_Positive() throws Exception {
		doNothing().when(projectService).deleteProject(any(), any());
		mockMvc.perform(delete("/api/v1/project/123")).andExpect(status().isOk());
	}

	@Test
	void testDeleteProject_Negative() throws Exception {
		mockMvc.perform(delete("/api/v1/project/")).andExpect(status().is4xxClientError());
	}

	@Test
	void testAddTeamToProject_Positive() throws Exception {
		AddTeamProjectRequestDto dto = new AddTeamProjectRequestDto();
		dto.setProjectId("123");
		dto.setTeams(List.of("team1"));
		when(projectService.addTeamFromProject(any(), any())).thenReturn(new CommonProjectResponseDto());

		mockMvc.perform(put("/api/v1/project/add-team").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk());
	}

	@Test
	void testAddTeamToProject_Negative() throws Exception {
		AddTeamProjectRequestDto dto = new AddTeamProjectRequestDto();
		mockMvc.perform(put("/api/v1/project/add-team").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest());
	}

	@Test
	void testRemoveTeamFromProject_Positive() throws Exception {
		doNothing().when(projectService).removeTeamFromProject(any(), any(), any());
		mockMvc.perform(delete("/api/v1/project/team/p1/t1")).andExpect(status().isOk());
	}

	@Test
	void testRemoveTeamFromProject_Negative() throws Exception {
		mockMvc.perform(delete("/api/v1/project/team/p1/")).andExpect(status().is4xxClientError());
	}

	@Test
	void testGetProjectTeams_Positive() throws Exception {
		when(projectService.findProjectTeams("123")).thenReturn(Collections.emptyList());
		mockMvc.perform(get("/api/v1/project/team/123")).andExpect(status().isOk());
	}

	@Test
	void testGetProjectTeams_Negative() throws Exception {
		when(projectService.findProjectTeams("invalid_project"))
			.thenThrow(new AppException("Project not found"));

		mockMvc.perform(get("/api/v1/project/team/invalid_project"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Project not found"))
			.andExpect(jsonPath("$.status").value("400 BAD_REQUEST"));
	}


	@Test
	void testGetAllDesigners_Positive() throws Exception {
		when(projectService.getAllMembersByDesigination(any(), any(), any())).thenReturn(Collections.emptyList());
		mockMvc.perform(get("/api/v1/project/designers/123").header("USERNAME", "u1")).andExpect(status().isOk());
	}

	@Test
	void testGetAllDesigners_Negative() throws Exception {
		mockMvc.perform(get("/api/v1/project/designers/123")).andExpect(status().is4xxClientError());
	}

	@Test
	void testGetAllDevelopers_Positive() throws Exception {
		when(projectService.getAllMembersByDesigination(any(), any(), any())).thenReturn(Collections.emptyList());
		mockMvc.perform(get("/api/v1/project/developers/123").header("USERNAME", "u1")).andExpect(status().isOk());
	}

	@Test
	void testGetAllDevelopers_Negative() throws Exception {
		mockMvc.perform(get("/api/v1/project/developers/123")).andExpect(status().is4xxClientError());
	}

	@Test
	void testGetAllTesters_Positive() throws Exception {
		when(projectService.getAllMembersByDesigination(any(), any(), any())).thenReturn(Collections.emptyList());
		mockMvc.perform(get("/api/v1/project/testers/123").header("USERNAME", "u1")).andExpect(status().isOk());
	}

	@Test
	void testGetAllTesters_Negative() throws Exception {
		mockMvc.perform(get("/api/v1/project/testers/123")).andExpect(status().is4xxClientError());
	}

	@Test
	void testGetProject_Positive() throws Exception {
		when(projectService.getProject("proj1")).thenReturn(new CommonProjectResponseDto());
		mockMvc.perform(get("/api/v1/project/proj1")).andExpect(status().isOk());
	}

	@Test
	void testGetProject_Negative() throws Exception {
		when(projectService.getProject("proj2")).thenThrow(new AppException("Not Found"));

		mockMvc.perform(get("/api/v1/project/proj2")).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Not Found"))
				.andExpect(jsonPath("$.status").value("400 BAD_REQUEST"));
	}

	@Test
	void testSearchProjects_Positive() throws Exception {
		when(projectService.searchProjects("searchTerm")).thenReturn(Collections.emptyList());
		mockMvc.perform(get("/api/v1/project?search=searchTerm")).andExpect(status().isOk());
	}

	@Test
	void testSearchProjects_Negative() throws Exception {
		when(projectService.searchProjects(null)).thenReturn(Collections.emptyList());
		mockMvc.perform(get("/api/v1/project")).andExpect(status().isOk());
	}

	@Test
	void testAddTagsToProject_Positive() throws Exception {
		AddTagsToProjectDto dto = new AddTagsToProjectDto();
		dto.setProjectId("p1");
		dto.setTagName("tag1");
		when(projectService.addTagToProject(any(), any())).thenReturn(new CommonProjectResponseDto());
		mockMvc.perform(put("/api/v1/project/add-tags").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk());
	}

	@Test
	void testAddTagsToProject_Negative() throws Exception {
		AddTagsToProjectDto dto = new AddTagsToProjectDto();
		mockMvc.perform(put("/api/v1/project/add-tags").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest());
	}

	@Test
	void testRemoveTagsFromProject_Positive() throws Exception {
		RemoveTagsFromProjectDto dto = new RemoveTagsFromProjectDto();
		dto.setProjectId("p1");
		dto.setTagId("t1");

		CommonProjectResponseDto message = new CommonProjectResponseDto();

		when(projectService.removeTagFromProject(any(), any())).thenReturn(message);

		mockMvc.perform(put("/api/v1/project/remove-tags").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Tags in the project :p1, removed successfully"));
	}

	@Test
	void testRemoveTagsFromProject_Negative() throws Exception {
		RemoveTagsFromProjectDto dto = new RemoveTagsFromProjectDto();
		mockMvc.perform(put("/api/v1/project/remove-tags").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest());
	}

	@Test
	void testGetProjectMembers_Positive() throws Exception {
		when(projectService.getProjectMembers("123")).thenReturn(Collections.emptyList());
		mockMvc.perform(get("/api/v1/project/members/123")).andExpect(status().isOk());
	}

	@Test
	void testGetProjectTags_Positive() throws Exception {
		when(projectService.findProjectTags("123")).thenReturn(Collections.emptyList());
		mockMvc.perform(get("/api/v1/project/tag/123")).andExpect(status().isOk());
	}

	@Test
	void testGetProjectsByAdmin_Positive() throws Exception {
		when(projectService.getProjectByAdmin(any())).thenReturn(Collections.emptyList());
		mockMvc.perform(get("/api/v1/project/admin")).andExpect(status().isOk());
	}
}
