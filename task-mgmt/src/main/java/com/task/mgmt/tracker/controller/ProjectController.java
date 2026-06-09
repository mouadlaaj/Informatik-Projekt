package com.task.mgmt.tracker.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.task.mgmt.tracker.constant.TaskDesignation;
import com.task.mgmt.tracker.entity.Member;
import com.task.mgmt.tracker.entity.Tag;
import com.task.mgmt.tracker.request.payload.AddTagsToProjectDto;
import com.task.mgmt.tracker.request.payload.AddTeamProjectRequestDto;
import com.task.mgmt.tracker.request.payload.CreateProjectRequestDto;
import com.task.mgmt.tracker.request.payload.RemoveTagsFromProjectDto;
import com.task.mgmt.tracker.request.payload.UpdateProjectRequestDto;
import com.task.mgmt.tracker.response.payload.CommonMemberResponseDto;
import com.task.mgmt.tracker.response.payload.CommonProjectResponseDto;
import com.task.mgmt.tracker.response.payload.CommonTeamResponseDto;
import com.task.mgmt.tracker.response.payload.MessageDto;
import com.task.mgmt.tracker.service.ProjectService;
import com.task.mgmt.tracker.utils.AppUtils;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/project")
@CrossOrigin("*")
@SecurityRequirement(name = "token")
public class ProjectController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectController.class);

	@Autowired
	private ProjectService projectService;

	@PostMapping
	public ResponseEntity<CommonProjectResponseDto> createProject(
			@Valid @RequestBody CreateProjectRequestDto createProjectRequestDto) {
		String loggedUser = AppUtils.getLoggedInUser();
		LOGGER.info("Creating project by: {}", loggedUser);
		CommonProjectResponseDto createdProject = projectService.createProject(loggedUser, createProjectRequestDto);
		LOGGER.info("Project created by: {}", loggedUser);
		return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
	}

	@PutMapping("/{projectId}")
	public ResponseEntity<CommonProjectResponseDto> updateProject(@PathVariable("projectId") String projectId,
			@Valid @RequestBody UpdateProjectRequestDto updateProjectRequestDto) {
		String loggedUser = AppUtils.getLoggedInUser();
		LOGGER.info("Updating project by: {}", loggedUser);
		CommonProjectResponseDto updatedProject = projectService.updateProject(projectId, loggedUser,
				updateProjectRequestDto);
		LOGGER.info("Project updated by: {}", loggedUser);
		return new ResponseEntity<>(updatedProject, HttpStatus.OK);
	}

	@DeleteMapping("/{projectId}")
	public ResponseEntity<MessageDto> deleteProject(@PathVariable("projectId") String projectId) {
		String loggedUser = AppUtils.getLoggedInUser();
		LOGGER.info("Deleting project by: {}", loggedUser);
		projectService.deleteProject(loggedUser, projectId);
		LOGGER.info("Project deleted by: {}", loggedUser);
		MessageDto deleteProject = new MessageDto("Project deleted successfully", LocalDateTime.now());
		return new ResponseEntity<>(deleteProject, HttpStatus.OK);
	}

	@PutMapping("/add-team")
	public ResponseEntity<CommonProjectResponseDto> addTeamToProject(
			@Valid @RequestBody AddTeamProjectRequestDto projectRequestDto) {
		String loggedUser = AppUtils.getLoggedInUser();
		LOGGER.info("Adding team to project by: {}", loggedUser);
		CommonProjectResponseDto updatedProject = projectService.addTeamFromProject(loggedUser, projectRequestDto);
		LOGGER.info("Team added to project by: {}", loggedUser);
		return new ResponseEntity<>(updatedProject, HttpStatus.OK);
	}

	@DeleteMapping("/team/{projectId}/{teamId}")
	public ResponseEntity<MessageDto> removeTeamFromProject(@PathVariable("projectId") String projectId,
			@PathVariable("teamId") String teamId) {
		String loggedUser = AppUtils.getLoggedInUser();
		LOGGER.info("Removing team from project by: {}", loggedUser);
		projectService.removeTeamFromProject(projectId, teamId, loggedUser);
		LOGGER.info("Team removed from project by: {}", loggedUser);
		MessageDto removedTeam = new MessageDto("Team removed from project successfully", LocalDateTime.now());
		return new ResponseEntity<>(removedTeam, HttpStatus.OK);
	}

	@GetMapping("/team/{projectId}")
	public ResponseEntity<List<CommonTeamResponseDto>> getProjectTeams(@PathVariable("projectId") String projectId) {
		LOGGER.info("Fetching project members for Project ID: {} ", projectId);
		List<CommonTeamResponseDto> team = projectService.findProjectTeams(projectId);
		LOGGER.info("Project members retrieved successfully team size :{}", team.size());
		return new ResponseEntity<List<CommonTeamResponseDto>>(team, HttpStatus.OK);
	}

	@GetMapping("/designers/{projectId}")
	public ResponseEntity<List<CommonMemberResponseDto>> getAllDesigners(@RequestHeader("USERNAME") String memberId,
			@PathVariable(name = "projectId") String projectId) {

		String designation = TaskDesignation.DESIGNER.name();
		LOGGER.info("Fetching designers for project '{}'", projectId);

		List<CommonMemberResponseDto> members = projectService.getAllMembersByDesigination(memberId, designation,
				projectId);
		return ResponseEntity.ok(members);
	}

	@GetMapping("/developers/{projectId}")
	public ResponseEntity<List<CommonMemberResponseDto>> getAllDevelopers(@RequestHeader("USERNAME") String memberId,
			@PathVariable(name = "projectId") String projectId) {

		String designation = TaskDesignation.DEVELOPER.name();
		LOGGER.info("Fetching developers for project '{}'", projectId);

		List<CommonMemberResponseDto> members = projectService.getAllMembersByDesigination(memberId, designation,
				projectId);
		return ResponseEntity.ok(members);
	}

	@GetMapping("/testers/{projectId}")
	public ResponseEntity<List<CommonMemberResponseDto>> getAllTesters(@RequestHeader("USERNAME") String memberId,
			@PathVariable(name = "projectId") String projectId) {

		String designation = TaskDesignation.TESTER.name();
		LOGGER.info("Fetching testers for project '{}'", projectId);

		List<CommonMemberResponseDto> members = projectService.getAllMembersByDesigination(memberId, designation,
				projectId);
		return ResponseEntity.ok(members);
	}

	@GetMapping("/{projectId}")
	public ResponseEntity<CommonProjectResponseDto> getProject(@PathVariable("projectId") String projectId) {
		LOGGER.info("Fetching project with ID: {} ", projectId);
		CommonProjectResponseDto project = projectService.getProject(projectId);
		LOGGER.info("Successfully fetched project with ID: {}", projectId);
		return new ResponseEntity<CommonProjectResponseDto>(project, HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<List<CommonProjectResponseDto>> searchProjects(
			@RequestParam(required = false, name = "search") String searchText) {
		LOGGER.info("Searching projects with filters ");
		List<CommonProjectResponseDto> projects = projectService.searchProjects(searchText);
		LOGGER.info("Successfully fetched projects with filters :{}", projects.size());
		return new ResponseEntity<List<CommonProjectResponseDto>>(projects, HttpStatus.OK);
	}

	@PutMapping("/add-tags")
	public ResponseEntity<CommonProjectResponseDto> addTagsToProject(@Valid @RequestBody AddTagsToProjectDto dto) {
		String loggedUser = AppUtils.getLoggedInUser();
		LOGGER.info("User try to add tags for this project ID: {} addedBy ID: {} ", dto.getProjectId(), loggedUser);
		CommonProjectResponseDto addTags = projectService.addTagToProject(loggedUser, dto);
		LOGGER.info("Tags added succesfully to the project ID: {} added by ID: {}", dto.getProjectId(), loggedUser);
		return new ResponseEntity<>(addTags, HttpStatus.OK);
	}

	@PutMapping("/remove-tags")
	public ResponseEntity<MessageDto> removeTagsFromProject(@Valid @RequestBody RemoveTagsFromProjectDto dto) {
		String loggedUser = AppUtils.getLoggedInUser();
		LOGGER.info("Request received to remove tags from project ID : {} removedBy ID :{}", dto.getProjectId(),
				loggedUser);
		projectService.removeTagFromProject(loggedUser, dto);
		MessageDto deleteTagsFromprojects = new MessageDto(
				"Tags in the project :" + dto.getProjectId() + ", removed successfully", LocalDateTime.now());
		LOGGER.info("Tags removed successfully from project: {}", dto.getProjectId());
		return new ResponseEntity<>(deleteTagsFromprojects, HttpStatus.OK);
	}

	@GetMapping("/members/{projectId}")
	public ResponseEntity<List<Member>> getProjectMembers(@PathVariable("projectId") String projectId) {
		LOGGER.info("Fetching members for project with ID: {}", projectId);
		List<Member> members = projectService.getProjectMembers(projectId);
		LOGGER.info("Found {} members for project with ID: {}", members.size(), projectId);
		return new ResponseEntity<>(members, HttpStatus.OK);
	}

	@GetMapping("/tag/{projectId}")
	public ResponseEntity<List<Tag>> getProjectTags(@PathVariable("projectId") String projectId) {
		LOGGER.info("Fetching project members for Project ID: {} ", projectId);
		List<Tag> tags = projectService.findProjectTags(projectId);
		LOGGER.info("Project members retrieved successfully team size :{}", tags.size());
		return new ResponseEntity<List<Tag>>(tags, HttpStatus.OK);
	}

	@GetMapping("/admin")
	public ResponseEntity<List<CommonProjectResponseDto>> getProjectsByAdmin() {
		String loggedUser = AppUtils.getLoggedInUser();
		LOGGER.info("Fetching project by admin ID : {}", loggedUser);
		List<CommonProjectResponseDto> projects = projectService.getProjectByAdmin(loggedUser);
		LOGGER.info("Projects retrieved successfully for admin ID: {}", loggedUser);
		return new ResponseEntity<List<CommonProjectResponseDto>>(projects, HttpStatus.OK);
	}

}
