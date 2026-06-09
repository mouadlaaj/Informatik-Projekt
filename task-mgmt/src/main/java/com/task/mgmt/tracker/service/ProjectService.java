package com.task.mgmt.tracker.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.task.mgmt.tracker.constant.RoleType;
import com.task.mgmt.tracker.constant.Status;
import com.task.mgmt.tracker.entity.Member;
import com.task.mgmt.tracker.entity.Project;
import com.task.mgmt.tracker.entity.Tag;
import com.task.mgmt.tracker.entity.Team;
import com.task.mgmt.tracker.exception.AlreadyExistsException;
import com.task.mgmt.tracker.exception.AppException;
import com.task.mgmt.tracker.exception.NotFoundException;
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class ProjectService {

	@Autowired
	ProjectRepository projectRepo;

	@Autowired
	MemberRepository memberRepo;

	@Autowired
	TeamRepository teamRepo;

	@Autowired
	TagRepository tagRepo;

	@Autowired
	EntityManager entityManager;

	@Autowired
	MemberService memberService;

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectService.class);

	public CommonProjectResponseDto createProject(String createdMember,
			CreateProjectRequestDto createProjectRequestDto) {
		try {

			Member projectCreateAdmin = memberService.validMemberCheck(createdMember, "creating project",
					RoleType.MANAGER.name());

			boolean isProjectName = projectRepo.existsByProjectName(createProjectRequestDto.getProjectName());

			if (isProjectName) {
				LOGGER.error("Exception occured while creating project,This projectName is already exists : {}",
						createProjectRequestDto.getProjectName());
				throw new AlreadyExistsException("This project name is already exists");
			}

			List<Team> teams = teamRepo.findAllByIdInAndStatus(createProjectRequestDto.getTeams(),
					Status.ACTIVE.name());
			if (teams.size() != createProjectRequestDto.getTeams().size()) {
				LOGGER.info(
						"Exception occured while create project,one or more teams id not found or teams status was inactive",
						createProjectRequestDto.getTeams());
				throw new NotFoundException("one or more teams id not found or teams status was inactive");
			}
			Project project = new Project();
			project.setProjectName(createProjectRequestDto.getProjectName());
			project.setDescription(createProjectRequestDto.getDescription());
			project.setStatus(Status.ACTIVE.name());
			project.setCreatedBy(projectCreateAdmin);
			project.setCreatedDate(LocalDateTime.now());
			project.setModifiedBy(projectCreateAdmin);
			project.setModifiedDate(LocalDateTime.now());

			project.setTeams(teams);

			List<Tag> tags = project.getTags();
			for (String tag : createProjectRequestDto.getTags()) {
				Tag tagName = tagRepo.findByName(tag.toUpperCase());
				if (tagName != null) {
					tags.add(tagName);
				} else {
					Tag newTag = new Tag();
					newTag.setName(tag.toUpperCase());
					Tag saveTag = tagRepo.save(newTag);
					tags.add(saveTag);
				}
			}
			project.setTags(tags);

			Project savedProject = projectRepo.save(project);
			return convertProjectToDto(savedProject);
		} catch (Exception e) {
			LOGGER.error("Exception occured while create project : {}", e.getMessage());
			throw new AppException("Exception occured while create project : " + e.getMessage());
		}
	}

	public CommonProjectResponseDto updateProject(String projectId, String modifyedMember,
			UpdateProjectRequestDto projectRequestDto) {
		try {
			Project project = projectRepo.findById(projectId).orElseThrow(() -> {
				LOGGER.error("Exception occurred while updating project, project id not found: {}", projectId);
				throw new NotFoundException("Project id not found: " + projectId);
			});

			Member projectModifyAdmin = memberService.validMemberCheck(modifyedMember, "updating project",
					RoleType.MANAGER.name());

			project.setModifiedBy(projectModifyAdmin);
			project.setModifiedDate(LocalDateTime.now());

			if (projectRequestDto.getProjectName() != null) {
				project.setProjectName(projectRequestDto.getProjectName());
			}
			if (projectRequestDto.getDescription() != null) {
				project.setDescription(projectRequestDto.getDescription());
			}

			if (projectRequestDto.getTeams() != null && !projectRequestDto.getTeams().isEmpty()) {
				List<Team> teams = teamRepo.findAllByIdInAndStatus(projectRequestDto.getTeams(), Status.ACTIVE.name());
				if (teams.size() != projectRequestDto.getTeams().size()) {
					throw new NotFoundException("One or more teams ID not found or team status was inactive");
				}
				project.setTeams(teams);
			}

			if (projectRequestDto.getTags() != null && !projectRequestDto.getTags().isEmpty()) {
				List<Tag> tags = projectRequestDto.getTags().stream().map(tagName -> {
					Tag tag = tagRepo.findByName(tagName.toUpperCase());
					if (tag != null) {
						return tag;
					} else {
						Tag newTag = new Tag();
						newTag.setName(tagName.toUpperCase());
						return tagRepo.save(newTag);
					}
				}).collect(Collectors.toList());
				project.setTags(tags);
			}

			Project updatedProject = projectRepo.save(project);
			return convertProjectToDto(updatedProject);
		} catch (Exception e) {
			LOGGER.error("Exception occurred while updating project: {}", e.getMessage());
			e.printStackTrace();
			throw new AppException(e.getMessage());
		}
	}

	public List<CommonMemberResponseDto> getAllMembersByDesigination(String memberId, String designation,
			String projectId) {
		try {
			Project project = projectRepo.findByIdAndStatus(projectId, Status.ACTIVE.name());
			if (project == null)
				throw new NotFoundException("Project ID not found");

			return project.getTeams().stream().flatMap(team -> team.getMembers().stream())
					.filter(member -> designation.equalsIgnoreCase(member.getDesignation())).distinct().map(member -> {
						CommonMemberResponseDto dto = new CommonMemberResponseDto();
						dto.setId(member.getId());
						dto.setFirstName(member.getFirstName());
						dto.setLastName(member.getLastName());
						dto.setRole(member.getRole());
						return dto;
					}).collect(Collectors.toList());

		} catch (Exception e) {
			LOGGER.error("Failed to fetch members by designation Member Id: {}, Message: {}", memberId, e);
			throw new AppException("Error while retrieving members with designation: " + designation);
		}
	}

	public void deleteProject(String adminId, String projectId) {
		try {

			Member admin = memberService.validMemberCheck(adminId, "deleting project", RoleType.MANAGER.name());

			Project project = projectRepo.findById(projectId).orElseThrow(() -> {
				LOGGER.error("Exception occurred while deleting project, project id not found: {}", projectId);
				throw new NotFoundException("Project id not found: " + projectId);
			});

			project.setStatus(Status.INACTIVE.name());
			project.setModifiedBy(admin);
			project.setModifiedDate(LocalDateTime.now());
			projectRepo.save(project);

		} catch (Exception e) {
			LOGGER.error("Exception occured delete project :{}", e.getMessage());
			throw new AppException(e.getMessage());
		}
	}

	public CommonProjectResponseDto addTeamFromProject(String adminId, AddTeamProjectRequestDto projectRequestDto) {
		try {

			Project project = projectRepo.findByIdAndStatus(projectRequestDto.getProjectId(), Status.ACTIVE.name());

			if (project == null) {
				LOGGER.error("Exception occured while adding a new team,Project id not found :{}",
						projectRequestDto.getProjectId());
				throw new NotFoundException("Project id not found");
			}

			Member projectModifyAdmin = memberService.validMemberCheck(adminId, "adding a new team project",
					RoleType.MANAGER.name());

			List<Team> teams = teamRepo.findAllByIdInAndStatus(projectRequestDto.getTeams(), Status.ACTIVE.name());

			if (teams.size() != projectRequestDto.getTeams().size()) {
				LOGGER.error("Exception occured while add teams,Teams id not found :{}", projectRequestDto.getTeams());
				throw new NotFoundException("Teams id not found");
			}

			List<Team> AllTeams = project.getTeams();
			AllTeams.addAll(teams);
			project.setModifiedBy(projectModifyAdmin);
			project.setModifiedDate(LocalDateTime.now());

			Project addProject = projectRepo.save(project);
			return convertProjectToDto(addProject);

		} catch (Exception e) {
			LOGGER.error("Exception occured while add teams :{}", e.getMessage());
			throw new AppException(e.getMessage());
		}

	}

	public void removeTeamFromProject(String projectId, String teamId, String adminId) {
		try {

			Project project = projectRepo.findByIdAndStatus(projectId, Status.ACTIVE.name());

			if (project == null) {
				LOGGER.error("Exception occured while removing a team,Project id not found :{}", projectId);
				throw new NotFoundException("Project id not found");
			}

			memberService.validMemberCheck(adminId, "removing a team project", RoleType.MANAGER.name());

			Team projectTeam = project.getTeams().stream().filter(e -> e.getId().equals(teamId)).findFirst()
					.orElseThrow(() -> {
						LOGGER.error("Exception occurred while removing a team: Team id not found in the project");
						return new NotFoundException("Team id not found in the project");
					});

			project.getTeams().remove(projectTeam);

			projectRepo.save(project);
		} catch (Exception e) {
			LOGGER.error("Exception occured while removing a team for project :{}", e.getMessage());
			throw new AppException(e.getMessage());
		}
	}

	public CommonProjectResponseDto addTagToProject(String memberId, AddTagsToProjectDto dto) {
		try {
			memberService.validMemberCheck(memberId, "add tag to project", RoleType.MANAGER.name());

			Project project = projectRepo.findByIdAndStatus(dto.getProjectId(), Status.ACTIVE.name());
			if (project == null) {
				LOGGER.error("Unauthorized user {} or project ID {} not found", memberId, dto.getProjectId());
				throw new NotFoundException("Project ID not found or unauthorized user");
			}

			if (project.getTags().stream()
					.anyMatch(tag -> tag.getName().toUpperCase().equals(dto.getTagName().toUpperCase()))) {
				LOGGER.error("Tag {} already exists in project {}", dto.getTagName(), dto.getProjectId());
				throw new AppException("Tag already exists in the project");
			}

			Tag newTag = tagRepo.findByName(dto.getTagName().toUpperCase());
			if (newTag == null) {
				newTag = new Tag();
				newTag.setName(dto.getTagName().toUpperCase());
			}

			project.getTags().add(newTag);
			project.setModifiedDate(LocalDateTime.now());

			Project updatedProject = projectRepo.save(project);
			return convertProjectToDto(updatedProject);

		} catch (Exception e) {
			LOGGER.error("Exception occurred while adding tag to project: {}", e.getMessage());
			throw new AppException(e.getMessage());
		}
	}
	public CommonProjectResponseDto removeTagFromProject(String adminId, RemoveTagsFromProjectDto dto) {
		try {
			memberService.validMemberCheck(adminId, "remove tag to project", RoleType.MANAGER.name());

			Project project = projectRepo.findByIdAndStatus(dto.getProjectId(), Status.ACTIVE.name());
			if (project == null) {
				LOGGER.error("Project ID {} not found or project is inactive", dto.getProjectId());
				throw new NotFoundException("Project ID not found or project is in an inactive state");
			}

			Tag tagToRemove = tagRepo.findById(dto.getTagId())
					.orElseThrow(() -> new NotFoundException("Tag not found with the provided ID"));

			if (!project.getTags().contains(tagToRemove)) {
				LOGGER.error("Tag ID {} is not present in the project {}", dto.getTagId(), dto.getProjectId());
				throw new NotFoundException("Tag with ID " + dto.getTagId() + " is not present in the project");
			}

			project.getTags().remove(tagToRemove);
			project.setModifiedDate(LocalDateTime.now());

			Project updatedProject = projectRepo.save(project);
			return convertProjectToDto(updatedProject);

		} catch (Exception e) {
			LOGGER.error("Exception occurred while removing tag from project: {}", e.getMessage());
			throw new AppException(e.getMessage());
		}
	}


	public List<CommonTeamResponseDto> findProjectTeams(String projectId) {
		try {
			Project project = projectRepo.findByIdAndStatus(projectId, Status.ACTIVE.name());
			if (project != null) {
				return convertTeamsToDto(project.getTeams());
			} else {
				LOGGER.error("Exception occured while find project inside teams,Project id not found : {}", projectId);
				throw new NotFoundException("Project id not found");
			}
		} catch (Exception e) {
			LOGGER.error("Exception occured while find project inside teams :{}", e.getMessage());
			throw new AppException(e.getMessage());
		}
	}

	public List<Member> getProjectMembers(String projectId) {
		try {

			Project project = projectRepo.findByIdAndStatus(projectId, Status.ACTIVE.name());
			if (project != null) {
				return project.getTeams().stream().flatMap(team -> team.getMembers().stream())
						.filter(member -> member.getId() != null).distinct().collect(Collectors.toList());
			} else {
				LOGGER.error("Exception occured while find project,Project id not found : {}", projectId);
				throw new NotFoundException("Project id not found");
			}

		} catch (Exception e) {
			LOGGER.error("Exception occurred while fetching all members on project: {}", e.getMessage());
			throw new AppException(e.getMessage());
		}

	}

	public CommonProjectResponseDto getProject(String projectId) {
		try {
			Project project = projectRepo.findByIdAndStatus(projectId, Status.ACTIVE.name());
			if (project != null) {
				return convertProjectToDto(project);
			} else {
				LOGGER.error("Exception occured while find project,Project id not found : {}", projectId);
				throw new NotFoundException("Project id not found");
			}

		} catch (Exception e) {
			LOGGER.error("Exception occured while find project :{}", e.getMessage());
			throw new AppException(e.getMessage());
		}
	}

	public List<CommonProjectResponseDto> searchProjects(String searchText) {
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Project> query = cb.createQuery(Project.class);
			Root<Project> root = query.from(Project.class);

			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.equal(root.get("status"), Status.ACTIVE.name()));

			if (!StringUtils.hasText(searchText)) {
				Predicate name = cb.like(root.get("projectName"), "%" + searchText + "%");
				Predicate tag = cb.like(root.join("tags").get("name"), "%" + searchText + "%");
				Predicate team = cb.like(root.join("teams").get("teamName"), "%" + searchText + "%");
				Predicate memId = cb.like(root.join("teams").join("members").get("id"), "%" + searchText + "%");
				predicates.add(cb.or(name, tag, team, memId));
			}

			query.select(root).distinct(true).where(predicates.toArray(new Predicate[0]));
			query.orderBy(cb.desc(root.get("createdDate")));

			List<Project> projects = entityManager.createQuery(query).getResultList();
			return convertProjectsToDto(projects);
		} catch (Exception e) {
			LOGGER.error("Exception occurred while fetching all projects: {}", e.getMessage());
			throw new AppException("Exception occurred while fetching all projects: " + e.getMessage());
		}
	}

	public List<Tag> findProjectTags(String projectId) {
		try {
			Project project = projectRepo.findByIdAndStatus(projectId, Status.ACTIVE.name());
			if (project != null) {
				return project.getTags();
			} else {
				LOGGER.error("Exception occured while find project inside tags,Project id not found : {}", projectId);
				throw new NotFoundException("Project id not found");
			}
		} catch (Exception e) {
			LOGGER.error("Exception occured while find project inside teams :{}", e.getMessage());
			throw new AppException("Exception occured while find project inside teams : " + e.getMessage());
		}
	}

	public List<CommonProjectResponseDto> getProjectByAdmin(String adminId) {
		try {
			Member member = memberRepo.findByIdAndStatus(adminId, Status.ACTIVE.name());
			if (member == null) {
				throw new NotFoundException("Admin id not found");
			} else {
				List<Project> projects;
				if (member.getRole().equals(RoleType.MANAGER.name())) {
					projects = projectRepo.findAll();
				} else {
					throw new AppException("Only admins can retrieve projects");
				}
				List<CommonProjectResponseDto> projectDtos = convertProjectsToDto(projects);
				return projectDtos;
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception occurred while finding projects: {}", e.getMessage());
			throw new AppException("Exception occurred while finding projects: " + e.getMessage());
		}
	}

	private List<CommonTeamResponseDto> convertTeamsToDto(List<Team> teams) {
		return teams.stream().map(this::convertTeamToDto).collect(Collectors.toList());
	}

	List<CommonProjectResponseDto> convertProjectsToDto(List<Project> projects) {
		return projects.stream().map(this::convertProjectToDto).collect(Collectors.toList());
	}

	public CommonProjectResponseDto convertProjectToDto(Project project) {
		CommonProjectResponseDto responseDto = new CommonProjectResponseDto();
		responseDto.setId(project.getId());
		responseDto.setProjectName(project.getProjectName());
		responseDto.setDescription(project.getDescription());
		responseDto.setStatus(project.getStatus());
		responseDto.setCreatedDate(project.getCreatedDate());
		responseDto.setModifiedDate(project.getModifiedDate());
		responseDto.setCreatedBy(convertMemberToDto(project.getCreatedBy()));
		responseDto.setModifiedBy(convertMemberToDto(project.getModifiedBy()));
		responseDto.setTeams(convertTeamsToDto(project.getTeams()));
		responseDto.setTags(project.getTags());
		return responseDto;
	}

	private CommonMemberResponseDto convertMemberToDto(Member member) {
		if (member == null) {
			return null;
		}
		CommonMemberResponseDto commonMemberResponseDto = new CommonMemberResponseDto();
		commonMemberResponseDto.setId(member.getId());
		commonMemberResponseDto.setFirstName(member.getFirstName());
		commonMemberResponseDto.setLastName(member.getLastName());
		commonMemberResponseDto.setRole(member.getRole());
		return commonMemberResponseDto;
	}

	private CommonTeamResponseDto convertTeamToDto(Team team) {
		CommonTeamResponseDto commonTeamResponseDto = new CommonTeamResponseDto();
		commonTeamResponseDto.setId(team.getId());
		commonTeamResponseDto.setTeamName(team.getTeamName());
		commonTeamResponseDto.setStatus(team.getStatus());
		commonTeamResponseDto.setCreatedDate(team.getCreatedDate());
		commonTeamResponseDto.setModifiedDate(team.getModifiedDate());
		commonTeamResponseDto.setTeamLeadId(convertMemberToDto(team.getTeamLeadId()));
		commonTeamResponseDto.setCreatedBy(convertMemberToDto(team.getCreatedBy()));
		commonTeamResponseDto.setModifiedBy(convertMemberToDto(team.getModifiedBy()));
		commonTeamResponseDto
				.setMembers(team.getMembers().stream().map(this::convertMemberToDto).collect(Collectors.toSet()));
		return commonTeamResponseDto;
	}

}
