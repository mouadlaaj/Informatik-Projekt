package com.task.mgmt.tracker.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.task.mgmt.tracker.constant.RoleType;
import com.task.mgmt.tracker.constant.Status;
import com.task.mgmt.tracker.entity.Member;
import com.task.mgmt.tracker.entity.Project;
import com.task.mgmt.tracker.entity.Tag;
import com.task.mgmt.tracker.entity.Team;
import com.task.mgmt.tracker.exception.AppException;
import com.task.mgmt.tracker.repository.MemberRepository;
import com.task.mgmt.tracker.repository.ProjectRepository;
import com.task.mgmt.tracker.repository.TagRepository;
import com.task.mgmt.tracker.repository.TeamRepository;
import com.task.mgmt.tracker.request.payload.AddTagsToProjectDto;
import com.task.mgmt.tracker.request.payload.AddTeamProjectRequestDto;
import com.task.mgmt.tracker.request.payload.CreateProjectRequestDto;
import com.task.mgmt.tracker.request.payload.RemoveTagsFromProjectDto;
import com.task.mgmt.tracker.request.payload.UpdateProjectRequestDto;
import com.task.mgmt.tracker.response.payload.CommonMemberResponseDto;
import com.task.mgmt.tracker.response.payload.CommonProjectResponseDto;
import com.task.mgmt.tracker.response.payload.CommonTeamResponseDto;
import com.task.mgmt.tracker.service.MemberService;
import com.task.mgmt.tracker.service.ProjectService;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

	@Mock
	private ProjectRepository projectRepo;

	@Mock
	private MemberRepository memberRepo;

	@Mock
	private TeamRepository teamRepo;

	@Mock
	private TagRepository tagRepo;

	@Mock
	private EntityManager entityManager;

	@Mock
	private MemberService memberService;

	@InjectMocks
	private ProjectService projectService;

	private Member manager;

	private Project project;

	@BeforeEach
	void setUp() {
		manager = new Member();
		manager.setId("manager1");
		manager.setRole(RoleType.MANAGER.name());
		manager.setFirstName("John");
		manager.setLastName("Doe");
		manager.setStatus(Status.ACTIVE.name());

		project = new Project();
		project.setId("project1");
		project.setStatus(Status.ACTIVE.name());
		project.setTeams(new ArrayList<>());
		project.setTags(new ArrayList<>());
	}

	@Test
	void testCreateProject_WithNewTags_Success() {

		CreateProjectRequestDto request = new CreateProjectRequestDto();
		request.setProjectName("My Project");
		request.setDescription("This is a new project");
		request.setTeams(List.of("team1"));
		request.setTags(List.of("JAVA", "SPRING"));

		Team team = new Team();
		team.setId("team1");
		team.setStatus(Status.ACTIVE.name());

		when(memberService.validMemberCheck("manager1", "creating project", RoleType.MANAGER.name()))
				.thenReturn(manager);
		when(projectRepo.existsByProjectName("My Project")).thenReturn(false);

		when(teamRepo.findAllByIdInAndStatus(List.of("team1"), Status.ACTIVE.name())).thenReturn(List.of(team));

		when(tagRepo.findByName(anyString())).thenReturn(null);

		when(tagRepo.save(any(Tag.class))).thenAnswer(inv -> {
			Tag tag = inv.getArgument(0);
			tag.setId("tag-" + tag.getName());
			return tag;
		});

		when(projectRepo.save(any(Project.class))).thenAnswer(inv -> {
			Project proj = inv.getArgument(0);
			return proj;
		});

		CommonProjectResponseDto response = projectService.createProject("manager1", request);

		assertNotNull(response);
	}

	@Test
	void testCreateProject_ProjectNameAlreadyExists() {
		CreateProjectRequestDto request = new CreateProjectRequestDto();
		request.setProjectName("Duplicate Project");
		request.setDescription("Duplicate");
		request.setTeams(List.of("team1"));
		request.setTags(List.of("TAG1"));

		when(memberService.validMemberCheck(anyString(), anyString(), anyString())).thenReturn(manager);
		when(projectRepo.existsByProjectName("Duplicate Project")).thenReturn(true);

		assertThrows(AppException.class, () -> projectService.createProject("manager1", request));
	}

	@Test
	void testCreateProject_TeamNotFound() {
		CreateProjectRequestDto request = new CreateProjectRequestDto();
		request.setProjectName("New Project");
		request.setDescription("Invalid team");
		request.setTeams(List.of("invalidTeamId"));
		request.setTags(List.of("TAG1"));

		when(memberService.validMemberCheck(anyString(), anyString(), anyString())).thenReturn(manager);
		when(projectRepo.existsByProjectName(anyString())).thenReturn(false);
		when(teamRepo.findAllByIdInAndStatus(anyList(), eq(Status.ACTIVE.name()))).thenReturn(List.of());

		assertThrows(AppException.class, () -> projectService.createProject("manager1", request));
	}

	@Test
	void testUpdateProject_SuccessWithAllFieldsAndNewTags() {
		String projectId = "project123";
		String modifierId = "manager1";

		Project existingProject = new Project();
		existingProject.setId(projectId);
		existingProject.setProjectName("Old Name");
		existingProject.setDescription("Old Desc");
		existingProject.setStatus(Status.ACTIVE.name());

		Member modifier = new Member();
		modifier.setId(modifierId);
		modifier.setRole(RoleType.MANAGER.name());
		modifier.setStatus(Status.ACTIVE.name());

		Team team = new Team();
		team.setId("team1");
		team.setStatus(Status.ACTIVE.name());

		UpdateProjectRequestDto dto = new UpdateProjectRequestDto();
		dto.setProjectName("Updated Name");
		dto.setDescription("Updated Desc");
		dto.setTeams(List.of("team1"));
		dto.setTags(List.of("JAVA", "SPRING"));

		when(projectRepo.findById(projectId)).thenReturn(Optional.of(existingProject));
		when(memberService.validMemberCheck(modifierId, "updating project", RoleType.MANAGER.name()))
				.thenReturn(modifier);
		when(teamRepo.findAllByIdInAndStatus(List.of("team1"), Status.ACTIVE.name())).thenReturn(List.of(team));
		when(tagRepo.findByName(anyString())).thenReturn(null);
		when(tagRepo.save(any())).thenAnswer(inv -> {
			Tag tag = inv.getArgument(0);
			tag.setId("tag-" + tag.getName());
			return tag;
		});
		when(projectRepo.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

		CommonProjectResponseDto response = projectService.updateProject(projectId, modifierId, dto);

		assertNotNull(response);
		assertEquals("Updated Name", response.getProjectName());
		assertEquals(2, response.getTags().size());
	}

	@Test
	void testUpdateProject_SuccessWithExistingTagsOnly() {
		String projectId = "project123";
		String modifierId = "manager1";

		Project project = new Project();
		project.setId(projectId);
		project.setStatus(Status.ACTIVE.name());

		Member modifier = new Member();
		modifier.setId(modifierId);
		modifier.setRole(RoleType.MANAGER.name());
		modifier.setStatus(Status.ACTIVE.name());

		Tag tag = new Tag();
		tag.setId("tag1");
		tag.setName("REACT");

		UpdateProjectRequestDto dto = new UpdateProjectRequestDto();
		dto.setTeams(List.of("team1"));
		dto.setTags(List.of("REACT"));

		Team team = new Team();
		team.setId("team1");
		team.setStatus(Status.ACTIVE.name());

		when(projectRepo.findById(projectId)).thenReturn(Optional.of(project));
		when(memberService.validMemberCheck(modifierId, "updating project", RoleType.MANAGER.name()))
				.thenReturn(modifier);
		when(teamRepo.findAllByIdInAndStatus(anyList(), eq(Status.ACTIVE.name()))).thenReturn(List.of(team));
		when(tagRepo.findByName("REACT")).thenReturn(tag);
		when(projectRepo.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

		CommonProjectResponseDto response = projectService.updateProject(projectId, modifierId, dto);

		assertNotNull(response);
		assertEquals(1, response.getTags().size());
		assertEquals("REACT", response.getTags().get(0).getName());
	}

	@Test
	void testUpdateProject_ProjectNotFound_ShouldThrow() {
		String projectId = "invalidProject";
		String modifierId = "manager1";

		UpdateProjectRequestDto dto = new UpdateProjectRequestDto();
		dto.setTeams(List.of("team1"));

		when(projectRepo.findById(projectId)).thenReturn(Optional.empty());

		AppException ex = assertThrows(AppException.class, () -> {
			projectService.updateProject(projectId, modifierId, dto);
		});

		assertEquals("Project id not found: " + projectId, ex.getMessage());
	}

	@Test
	void testUpdateProject_InvalidTeamId_ShouldThrow() {
		String projectId = "project123";
		String modifierId = "manager1";

		Project project = new Project();
		project.setId(projectId);
		project.setStatus(Status.ACTIVE.name());

		Member modifier = new Member();
		modifier.setId(modifierId);
		modifier.setRole(RoleType.MANAGER.name());
		modifier.setStatus(Status.ACTIVE.name());

		UpdateProjectRequestDto dto = new UpdateProjectRequestDto();
		dto.setTeams(List.of("invalidTeam"));

		when(projectRepo.findById(projectId)).thenReturn(Optional.of(project));
		when(memberService.validMemberCheck(modifierId, "updating project", RoleType.MANAGER.name()))
				.thenReturn(modifier);
		when(teamRepo.findAllByIdInAndStatus(dto.getTeams(), Status.ACTIVE.name())).thenReturn(List.of());

		AppException ex = assertThrows(AppException.class, () -> {
			projectService.updateProject(projectId, modifierId, dto);
		});

		assertEquals("One or more teams ID not found or team status was inactive", ex.getMessage());
	}

	@Test
	void testGetAllMembersByDesignation_WithMatchingMembers() {
		Member member1 = new Member();
		member1.setId("m1");
		member1.setFirstName("Alice");
		member1.setLastName("Smith");
		member1.setRole("EMPLOYEE");
		member1.setDesignation("DEVELOPER");

		Member member2 = new Member();
		member2.setId("m2");
		member2.setFirstName("Bob");
		member2.setLastName("Jones");
		member2.setRole("EMPLOYEE");
		member2.setDesignation("DEVELOPER");

		Team team = new Team();
		team.setId("t1");
		team.setMembers(List.of(member1, member2));

		Project project = new Project();
		project.setId("p1");
		project.setTeams(List.of(team));

		when(projectRepo.findByIdAndStatus("p1", Status.ACTIVE.name())).thenReturn(project);

		List<CommonMemberResponseDto> result = projectService.getAllMembersByDesigination("anyId", "DEVELOPER", "p1");

		assertEquals(2, result.size());
		assertEquals("Alice", result.get(0).getFirstName());
		assertEquals("Bob", result.get(1).getFirstName());
	}

	@Test
	void testGetAllMembersByDesignation_NoMatchFound() {
		Member member = new Member();
		member.setId("m1");
		member.setFirstName("Charlie");
		member.setLastName("Day");
		member.setRole("EMPLOYEE");
		member.setDesignation("TESTER");

		Team team = new Team();
		team.setId("t1");
		team.setMembers(List.of(member));

		Project project = new Project();
		project.setId("p1");
		project.setTeams(List.of(team));

		when(projectRepo.findByIdAndStatus("p1", Status.ACTIVE.name())).thenReturn(project);

		List<CommonMemberResponseDto> result = projectService.getAllMembersByDesigination("anyId", "DEVELOPER", "p1");

		assertEquals(0, result.size());
	}

	@Test
	void testGetAllMembersByDesignation_ProjectNotFound() {
		when(projectRepo.findByIdAndStatus("invalidProjectId", Status.ACTIVE.name())).thenReturn(null);

		assertThrows(AppException.class, () -> {
			projectService.getAllMembersByDesigination("anyMemberId", "DEVELOPER", "invalidProjectId");
		});
	}

	@Test
	void testGetAllMembersByDesignation_RepositoryThrowsException() {
		when(projectRepo.findByIdAndStatus(anyString(), anyString())).thenThrow(new RuntimeException("DB down"));

		assertThrows(AppException.class, () -> {
			projectService.getAllMembersByDesigination("memberId", "DEVELOPER", "projectId");
		});
	}

	@Test
	void testDeleteProject_Success() {
		Project project = new Project();
		project.setId("p1");
		project.setStatus(Status.ACTIVE.name());

		when(memberService.validMemberCheck("manager1", "deleting project", RoleType.MANAGER.name()))
				.thenReturn(manager);
		when(projectRepo.findById("p1")).thenReturn(Optional.of(project));
		when(projectRepo.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

		assertDoesNotThrow(() -> projectService.deleteProject("manager1", "p1"));

		assertEquals(Status.INACTIVE.name(), project.getStatus());
		assertEquals("manager1", project.getModifiedBy().getId());
	}

	@Test
	void testDeleteProject_AlreadyInactive_Project() {
		Project project = new Project();
		project.setId("p2");
		project.setStatus(Status.INACTIVE.name()); // Already inactive

		when(memberService.validMemberCheck("manager1", "deleting project", RoleType.MANAGER.name()))
				.thenReturn(manager);
		when(projectRepo.findById("p2")).thenReturn(Optional.of(project));

		assertDoesNotThrow(() -> projectService.deleteProject("manager1", "p2"));
		assertEquals(Status.INACTIVE.name(), project.getStatus()); // Should remain INACTIVE
	}

	@Test
	void testDeleteProject_ProjectNotFound() {
		when(memberService.validMemberCheck("manager1", "deleting project", RoleType.MANAGER.name()))
				.thenReturn(manager);
		when(projectRepo.findById("invalidId")).thenReturn(Optional.empty());

		AppException ex = assertThrows(AppException.class, () -> projectService.deleteProject("manager1", "invalidId"));
		assertEquals("Project id not found: invalidId", ex.getMessage());
	}

	@Test
	void testAddTeamFromProject_Success_SingleTeam() {
		Project project = new Project();
		project.setId("project1");
		project.setStatus(Status.ACTIVE.name());
		project.setTeams(new ArrayList<>());

		Team team = new Team();
		team.setId("team1");
		team.setStatus(Status.ACTIVE.name());

		AddTeamProjectRequestDto dto = new AddTeamProjectRequestDto();
		dto.setProjectId("project1");
		dto.setTeams(List.of("team1"));

		when(projectRepo.findByIdAndStatus("project1", Status.ACTIVE.name())).thenReturn(project);
		when(memberService.validMemberCheck("manager1", "adding a new team project", RoleType.MANAGER.name()))
				.thenReturn(manager);
		when(teamRepo.findAllByIdInAndStatus(List.of("team1"), Status.ACTIVE.name())).thenReturn(List.of(team));
		when(projectRepo.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

		CommonProjectResponseDto response = projectService.addTeamFromProject("manager1", dto);

		assertNotNull(response);
		assertEquals("project1", response.getId());
	}

	@Test
	void testAddTeamFromProject_Success_MultipleTeams() {
		Project project = new Project();
		project.setId("project2");
		project.setStatus(Status.ACTIVE.name());
		project.setTeams(new ArrayList<>());

		Team team1 = new Team();
		team1.setId("team1");
		team1.setStatus(Status.ACTIVE.name());

		Team team2 = new Team();
		team2.setId("team2");
		team2.setStatus(Status.ACTIVE.name());

		AddTeamProjectRequestDto dto = new AddTeamProjectRequestDto();
		dto.setProjectId("project2");
		dto.setTeams(List.of("team1", "team2"));

		when(projectRepo.findByIdAndStatus("project2", Status.ACTIVE.name())).thenReturn(project);
		when(memberService.validMemberCheck("manager1", "adding a new team project", RoleType.MANAGER.name()))
				.thenReturn(manager);
		when(teamRepo.findAllByIdInAndStatus(List.of("team1", "team2"), Status.ACTIVE.name()))
				.thenReturn(List.of(team1, team2));
		when(projectRepo.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

		CommonProjectResponseDto response = projectService.addTeamFromProject("manager1", dto);

		assertNotNull(response);
		assertEquals("project2", response.getId());
	}

	@Test
	void testAddTeamFromProject_ProjectNotFound() {
		AddTeamProjectRequestDto dto = new AddTeamProjectRequestDto();
		dto.setProjectId("invalidProject");
		dto.setTeams(List.of("team1"));

		when(projectRepo.findByIdAndStatus("invalidProject", Status.ACTIVE.name())).thenReturn(null);

		AppException ex = assertThrows(AppException.class, () -> projectService.addTeamFromProject("manager1", dto));
		assertEquals("Project id not found", ex.getMessage());
	}

	@Test
	void testAddTeamFromProject_TeamNotFound() {
		Project project = new Project();
		project.setId("project1");
		project.setStatus(Status.ACTIVE.name());
		project.setTeams(new ArrayList<>());

		AddTeamProjectRequestDto dto = new AddTeamProjectRequestDto();
		dto.setProjectId("project1");
		dto.setTeams(List.of("teamX"));

		when(projectRepo.findByIdAndStatus("project1", Status.ACTIVE.name())).thenReturn(project);
		when(memberService.validMemberCheck("manager1", "adding a new team project", RoleType.MANAGER.name()))
				.thenReturn(manager);
		when(teamRepo.findAllByIdInAndStatus(List.of("teamX"), Status.ACTIVE.name())).thenReturn(List.of());

		AppException ex = assertThrows(AppException.class, () -> projectService.addTeamFromProject("manager1", dto));
		assertEquals("Teams id not found", ex.getMessage());
	}

	@Test
	void testRemoveTeamFromProject_Success() {
		Team team = new Team();
		team.setId("team1");
		project.getTeams().add(team);

		when(projectRepo.findByIdAndStatus("project1", Status.ACTIVE.name())).thenReturn(project);
		when(projectRepo.save(any())).thenReturn(project);
		when(memberService.validMemberCheck(any(), any(), any())).thenReturn(manager);

		projectService.removeTeamFromProject("project1", "team1", "manager1");
		assertTrue(project.getTeams().isEmpty());
	}

	@Test
	void testAddTagToProject_Success() {
		AddTagsToProjectDto dto = new AddTagsToProjectDto();
		dto.setProjectId("project1");
		dto.setTagName("JAVA");

		when(memberService.validMemberCheck(any(), any(), any())).thenReturn(manager);
		when(projectRepo.findByIdAndStatus("project1", Status.ACTIVE.name())).thenReturn(project);
		when(tagRepo.findByName("JAVA")).thenReturn(null);
		when(projectRepo.save(any())).thenReturn(project);

		CommonProjectResponseDto response = projectService.addTagToProject("manager1", dto);
		assertEquals(1, response.getTags().size());
	}

	@Test
	void testAddTagToProject_WithExistingTagObject_Success() {
		AddTagsToProjectDto dto = new AddTagsToProjectDto();
		dto.setProjectId("project1");
		dto.setTagName("JAVA");

		Tag tag = new Tag();
		tag.setId("tag1");
		tag.setName("JAVA");

		when(memberService.validMemberCheck(any(), any(), any())).thenReturn(manager);
		when(projectRepo.findByIdAndStatus("project1", Status.ACTIVE.name())).thenReturn(project);
		when(tagRepo.findByName("JAVA")).thenReturn(tag);
		when(projectRepo.save(any())).thenReturn(project);

		CommonProjectResponseDto response = projectService.addTagToProject("manager1", dto);
		assertEquals(1, response.getTags().size());
	}

	@Test
	void testRemoveTagFromProject_Success() {
		Tag tag = new Tag();
		tag.setId("tag1");
		tag.setName("JAVA");
		project.getTags().add(tag);

		RemoveTagsFromProjectDto dto = new RemoveTagsFromProjectDto();
		dto.setProjectId("project1");
		dto.setTagId("tag1");

		when(memberService.validMemberCheck(any(), any(), any())).thenReturn(manager);
		when(projectRepo.findByIdAndStatus("project1", Status.ACTIVE.name())).thenReturn(project);
		when(tagRepo.findById("tag1")).thenReturn(Optional.of(tag));
		when(projectRepo.save(any())).thenReturn(project);

		CommonProjectResponseDto response = projectService.removeTagFromProject("manager1", dto);
		assertTrue(response.getTags().isEmpty());
	}

	@Test
	void testAddTagToProject_CaseInsensitive_DuplicateAvoided() {
		Tag tag = new Tag();
		tag.setName("JAVA");
		project.getTags().add(tag);

		AddTagsToProjectDto dto = new AddTagsToProjectDto();
		dto.setProjectId("project1");
		dto.setTagName("java");

		when(memberService.validMemberCheck(any(), any(), any())).thenReturn(manager);
		when(projectRepo.findByIdAndStatus("project1", Status.ACTIVE.name())).thenReturn(project);

		AppException ex = assertThrows(AppException.class, () -> projectService.addTagToProject("manager1", dto));
		assertEquals("Tag already exists in the project", ex.getMessage());
	}

	@Test
	void testRemoveTeamFromProject_ProjectNotFound() {
		when(projectRepo.findByIdAndStatus("projectX", Status.ACTIVE.name())).thenReturn(null);

		AppException ex = assertThrows(AppException.class,
				() -> projectService.removeTeamFromProject("projectX", "team1", "manager1"));
		assertEquals("Project id not found", ex.getMessage());
	}

	@Test
	void testRemoveTeamFromProject_TeamNotFound() {
		when(projectRepo.findByIdAndStatus("project1", Status.ACTIVE.name())).thenReturn(project);
		when(memberService.validMemberCheck(any(), any(), any())).thenReturn(manager);

		AppException ex = assertThrows(AppException.class,
				() -> projectService.removeTeamFromProject("project1", "unknownTeam", "manager1"));
		assertEquals("Team id not found in the project", ex.getMessage());
	}

	@Test
	void testAddTagToProject_ProjectNotFound() {
		AddTagsToProjectDto dto = new AddTagsToProjectDto();
		dto.setProjectId("projectX");
		dto.setTagName("JAVA");

		when(memberService.validMemberCheck(any(), any(), any())).thenReturn(manager);
		when(projectRepo.findByIdAndStatus("projectX", Status.ACTIVE.name())).thenReturn(null);

		AppException ex = assertThrows(AppException.class, () -> projectService.addTagToProject("manager1", dto));
		assertEquals("Project ID not found or unauthorized user", ex.getMessage());
	}

	@Test
	void testRemoveTagFromProject_ProjectNotFound() {
		RemoveTagsFromProjectDto dto = new RemoveTagsFromProjectDto();
		dto.setProjectId("projectX");
		dto.setTagId("tag1");

		when(memberService.validMemberCheck(any(), any(), any())).thenReturn(manager);
		when(projectRepo.findByIdAndStatus("projectX", Status.ACTIVE.name())).thenReturn(null);

		AppException ex = assertThrows(AppException.class, () -> projectService.removeTagFromProject("manager1", dto));
		assertEquals("Project ID not found or project is in an inactive state", ex.getMessage());
	}

	@Test
	void testRemoveTagFromProject_TagIdNotFound() {
		RemoveTagsFromProjectDto dto = new RemoveTagsFromProjectDto();
		dto.setProjectId("project1");
		dto.setTagId("tagX");

		when(memberService.validMemberCheck(any(), any(), any())).thenReturn(manager);
		when(projectRepo.findByIdAndStatus("project1", Status.ACTIVE.name())).thenReturn(project);
		when(tagRepo.findById("tagX")).thenReturn(Optional.empty());

		AppException ex = assertThrows(AppException.class, () -> projectService.removeTagFromProject("manager1", dto));
		assertEquals("Tag not found with the provided ID", ex.getMessage());
	}

	@Test
	void testRemoveTagFromProject_TagNotInProject() {
		Tag tag = new Tag();
		tag.setId("tag2");
		tag.setName("SPRING");

		RemoveTagsFromProjectDto dto = new RemoveTagsFromProjectDto();
		dto.setProjectId("project1");
		dto.setTagId("tag2");

		when(memberService.validMemberCheck(any(), any(), any())).thenReturn(manager);
		when(projectRepo.findByIdAndStatus("project1", Status.ACTIVE.name())).thenReturn(project);
		when(tagRepo.findById("tag2")).thenReturn(Optional.of(tag));

		AppException ex = assertThrows(AppException.class, () -> projectService.removeTagFromProject("manager1", dto));
		assertEquals("Tag with ID tag2 is not present in the project", ex.getMessage());
	}

	@Test
	void testFindProjectTeams_Success() {
		Project project = new Project();
		Team team = new Team();
		team.setId("team1");
		project.setTeams(List.of(team));

		when(projectRepo.findByIdAndStatus("proj123", Status.ACTIVE.name())).thenReturn(project);

		List<CommonTeamResponseDto> teams = projectService.findProjectTeams("proj123");
		assertNotNull(teams);
		assertEquals(1, teams.size());
	}

	@Test
	void testFindProjectTeams_ProjectNotFound_ShouldThrow() {
		when(projectRepo.findByIdAndStatus("proj999", Status.ACTIVE.name())).thenReturn(null);

		AppException exception = assertThrows(AppException.class, () -> projectService.findProjectTeams("proj999"));
		assertEquals("Project id not found", exception.getMessage());
	}

	@Test
	void testGetProjectMembers_Success() {
		Member member = new Member();
		member.setId("mem1");

		Team team = new Team();
		team.setMembers(List.of(member));

		Project project = new Project();
		project.setTeams(List.of(team));

		when(projectRepo.findByIdAndStatus("proj123", Status.ACTIVE.name())).thenReturn(project);

		List<Member> members = projectService.getProjectMembers("proj123");
		assertNotNull(members);
		assertEquals(1, members.size());
		assertEquals("mem1", members.get(0).getId());
	}

	@Test
	void testGetProjectMembers_ProjectNotFound_ShouldThrow() {
		when(projectRepo.findByIdAndStatus("proj404", Status.ACTIVE.name())).thenReturn(null);

		AppException exception = assertThrows(AppException.class, () -> projectService.getProjectMembers("proj404"));
		assertEquals("Project id not found", exception.getMessage());
	}

	@Test
	void testGetProject_Success() {
		Project project = new Project();
		project.setId("proj123");
		project.setProjectName("Demo");

		when(projectRepo.findByIdAndStatus("proj123", Status.ACTIVE.name())).thenReturn(project);

		CommonProjectResponseDto dto = projectService.getProject("proj123");

		assertNotNull(dto);
		assertEquals("proj123", dto.getId());
		assertEquals("Demo", dto.getProjectName());
	}

	@Test
	void testGetProject_NotFound_ShouldThrow() {
		when(projectRepo.findByIdAndStatus("proj999", Status.ACTIVE.name())).thenReturn(null);

		AppException exception = assertThrows(AppException.class, () -> projectService.getProject("proj999"));
		assertEquals("Project id not found", exception.getMessage());
	}

	@Test
	void testFindProjectTags_Success() {
		Project project = new Project();
		project.setId("p1");
		project.setStatus(Status.ACTIVE.name());
		Tag tag = new Tag();
		tag.setId("tag2");
		tag.setName("SPRING");
		project.setTags(List.of(tag));

		when(projectRepo.findByIdAndStatus("p1", Status.ACTIVE.name())).thenReturn(project);

		List<Tag> tags = projectService.findProjectTags("p1");
		assertEquals(1, tags.size());
	}

	@Test
	void testFindProjectTags_EmptyTags() {
		Project project = new Project();
		project.setId("p2");
		project.setTags(new ArrayList<>());
		project.setStatus(Status.ACTIVE.name());

		when(projectRepo.findByIdAndStatus("p2", Status.ACTIVE.name())).thenReturn(project);
		List<Tag> tags = projectService.findProjectTags("p2");
		assertTrue(tags.isEmpty());
	}

	@Test
	void testFindProjectTags_ProjectNotFound() {
		when(projectRepo.findByIdAndStatus("invalidId", Status.ACTIVE.name())).thenReturn(null);
		assertThrows(AppException.class, () -> projectService.findProjectTags("invalidId"));
	}

	@Test
	void testFindProjectTags_ExceptionOccurs() {
		when(projectRepo.findByIdAndStatus(anyString(), anyString())).thenThrow(new RuntimeException("DB Error"));
		assertThrows(AppException.class, () -> projectService.findProjectTags("any"));
	}

	@Test
	void testGetProjectByAdmin_Success() {
		Member admin = new Member();
		admin.setId("admin1");
		admin.setRole(RoleType.MANAGER.name());

		Project project = new Project();
		project.setId("proj1");
		project.setProjectName("Demo Project");
		project.setStatus(Status.ACTIVE.name());
		project.setCreatedDate(LocalDateTime.now());
		project.setCreatedBy(admin);
		project.setModifiedBy(admin);
		project.setModifiedDate(LocalDateTime.now());
		project.setTeams(new ArrayList<>());
		project.setTags(new ArrayList<>());

		when(memberRepo.findByIdAndStatus("admin1", Status.ACTIVE.name())).thenReturn(admin);
		when(projectRepo.findAll()).thenReturn(List.of(project));

		List<CommonProjectResponseDto> result = projectService.getProjectByAdmin("admin1");

		assertEquals(1, result.size());
		assertEquals("proj1", result.get(0).getId());
		assertEquals("Demo Project", result.get(0).getProjectName());
	}

	@Test
	void testGetProjectByAdmin_EmptyProjectList() {
		Member admin = new Member();
		admin.setId("admin1");
		admin.setRole(RoleType.MANAGER.name());

		when(memberRepo.findByIdAndStatus("admin1", Status.ACTIVE.name())).thenReturn(admin);
		when(projectRepo.findAll()).thenReturn(new ArrayList<>());

		List<CommonProjectResponseDto> result = projectService.getProjectByAdmin("admin1");

		assertTrue(result.isEmpty());
	}

	@Test
	void testGetProjectByAdmin_AdminNotFound() {
		when(memberRepo.findByIdAndStatus("invalidAdmin", Status.ACTIVE.name())).thenReturn(null);
		assertThrows(AppException.class, () -> projectService.getProjectByAdmin("invalidAdmin"));
	}

	@Test
	void testGetProjectByAdmin_UnauthorizedRole() {
		Member employee = new Member();
		employee.setId("emp1");
		employee.setRole(RoleType.EMPLOYEE.name());

		when(memberRepo.findByIdAndStatus("emp1", Status.ACTIVE.name())).thenReturn(employee);

		assertThrows(AppException.class, () -> projectService.getProjectByAdmin("emp1"));
	}

}
