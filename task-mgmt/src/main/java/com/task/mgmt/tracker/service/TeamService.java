package com.task.mgmt.tracker.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.task.mgmt.tracker.constant.RoleType;
import com.task.mgmt.tracker.constant.Status;
import com.task.mgmt.tracker.entity.Member;
import com.task.mgmt.tracker.entity.Team;
import com.task.mgmt.tracker.exception.AppException;
import com.task.mgmt.tracker.exception.NotFoundException;
import com.task.mgmt.tracker.repository.MemberRepository;
import com.task.mgmt.tracker.repository.TeamRepository;
import com.task.mgmt.tracker.request.payload.CreateTeamRequestDto;
import com.task.mgmt.tracker.request.payload.UpdateTeamMemberRequestDto;
import com.task.mgmt.tracker.request.payload.UpdateTeamRequestDto;
import com.task.mgmt.tracker.response.payload.CommonMemberResponseDto;
import com.task.mgmt.tracker.response.payload.CommonTeamResponseDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class TeamService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TeamService.class);

	private static final List<String> ALL_ROLES = Arrays.asList("MANAGER", "EMPLOYEE");

	@Autowired
	private MemberRepository memberRepo;

	@Autowired
	private TeamRepository teamRepo;

	@Autowired
	EntityManager entityManager;

	public CommonTeamResponseDto addNewTeam(String adminId, CreateTeamRequestDto dto) {
		try {

			Member teamLeader = memberRepo.findByIdAndStatusAndRole(dto.getTeamLeadId(), Status.ACTIVE.name(),
					RoleType.MANAGER.name());
			if (teamLeader == null) {
				LOGGER.error("TeamLeader id: {}, not found", dto.getTeamLeadId());
				throw new NotFoundException("Team leader not found");
			}

			List<Member> teamMembers = memberRepo.findAllByIdInAndStatusAndRoleIn(dto.getTeamMembers(),
					Status.ACTIVE.name(), ALL_ROLES);

			if (teamMembers.size() != dto.getTeamMembers().size()) {
				LOGGER.error("One or more team members are not active or team members id not found");
				throw new NotFoundException("One or more team members are not active or team members id not found");
			}

			if (!teamMembers.contains(teamLeader)) {
				teamMembers.add(teamLeader);
			}

			Team newTeam = new Team();
			newTeam.setTeamLeadId(teamLeader);
			newTeam.setTeamName(dto.getTeamName());
			newTeam.setCreatedDate(LocalDateTime.now());
			newTeam.setCreatedBy(teamLeader);
			newTeam.setModifiedBy(teamLeader);
			newTeam.setModifiedDate(LocalDateTime.now());
			newTeam.setStatus(Status.ACTIVE.name());
			newTeam.setMembers(teamMembers);
			Team savedTeam = teamRepo.save(newTeam);
			CommonTeamResponseDto commonTeamResponseDto = convertTeamToDto(savedTeam);
			return commonTeamResponseDto;
		} catch (Exception e) {
			LOGGER.error("Exception occurred while adding a new team: {}", e.getMessage());
			throw new AppException("Exception occurred while adding a new team: " + e.getMessage());
		}
	}

	public CommonTeamResponseDto getTeamDetails(String teamId) {
		try {
			Team teamDetails = teamRepo.findByIdAndStatus(teamId, Status.ACTIVE.name());
			if (teamDetails == null) {
				LOGGER.error("Exception occured while getting a team by id {}, Team not found with id {} ", teamId,
						teamId);
				throw new NotFoundException("Team Id not found");
			}
			return convertTeamToDto(teamDetails);
		} catch (Exception e) {
			LOGGER.error("Exception occurred while getting team details by id {}: {}", teamId, e.getMessage());
			throw new NotFoundException(
					"Exception occurred while getting team details by id: " + teamId + ", " + e.getMessage());
		}
	}

	public void deleteTeamById(String teamId, String memberId) {
		try {

			Member member = memberRepo.findByIdAndStatusAndRole(memberId, Status.ACTIVE.name(),
					RoleType.MANAGER.name());

			if (member == null) {
				throw new NotFoundException("Admin id not found: " + memberId);
			}
			Team team = teamRepo.findByIdAndStatus(teamId, Status.ACTIVE.name());
			if (team == null) {
				throw new NotFoundException("Team id not found");
			}
			team.setStatus(Status.DROP.name());
			team.setModifiedBy(member);
			team.setModifiedDate(LocalDateTime.now());
			teamRepo.save(team);
		} catch (Exception e) {
			LOGGER.error("Exception occurred while deleting team by id {}: {}", teamId, e.getMessage());
			throw new AppException(e.getMessage());
		}
	}

	public CommonTeamResponseDto updateTeamById(String teamId, String adminId, UpdateTeamRequestDto updateTeamDto) {
		try {

			Team teamToUpdate = teamRepo.findByIdAndStatus(teamId, Status.ACTIVE.name());
			if (teamToUpdate == null) {
				throw new NotFoundException("Team id not found.");
			}

			Member modifiedAdmin = memberRepo.findByIdAndStatusAndRole(adminId, Status.ACTIVE.name(),
					RoleType.MANAGER.name());
			if (modifiedAdmin == null) {
				LOGGER.error("Modified by Admin id not found: {}", adminId);
				throw new NotFoundException("Modified by Admin  id not found: " + adminId);
			}

			if (updateTeamDto.getTeamMembers() != null) {
				List<Member> teamMembers = memberRepo.findAllByIdInAndStatusAndRoleIn(updateTeamDto.getTeamMembers(),
						Status.ACTIVE.name(), ALL_ROLES);

				if (teamMembers == null) {
					LOGGER.error("Given team members id not found : {}", updateTeamDto.getTeamMembers());
					throw new NotFoundException("Given team members id not found ");
				}
				teamToUpdate.setMembers(teamMembers);
			}

			if (updateTeamDto.getTeamLeadId() != null) {
				Member teamLeader = memberRepo.findByIdAndStatusAndRole(updateTeamDto.getTeamLeadId(),
						Status.ACTIVE.name(), RoleType.MANAGER.name());
				if (teamLeader == null) {
					LOGGER.error("Team leader id {} not found", updateTeamDto.getTeamLeadId());
					throw new NotFoundException("Team lead Id not found");
				}
				List<Member> listOfMembers = teamToUpdate.getMembers();
				listOfMembers.removeIf(members -> members.getId().equals(teamToUpdate.getTeamLeadId().getId()));
				listOfMembers.add(teamLeader);
				teamToUpdate.setMembers(listOfMembers);
				teamToUpdate.setTeamLeadId(teamLeader);
			}

			if (updateTeamDto.getTeamName() != null) {
				teamToUpdate.setTeamName(updateTeamDto.getTeamName());
			}

			if (modifiedAdmin != null) {
				teamToUpdate.setModifiedBy(modifiedAdmin);
			}

			Team updatedTeam = teamRepo.save(teamToUpdate);
			return convertTeamToDto(updatedTeam);
		} catch (Exception e) {
			LOGGER.error("Exception occurred while updating team {}: {}", teamId, e.getMessage());
			throw new AppException(e.getMessage());
		}
	}

	public CommonTeamResponseDto addTeamMember(String adminId, UpdateTeamMemberRequestDto dto) {
		try {
			Team teamId = teamRepo.findByIdAndStatus(dto.getTeamId(), Status.ACTIVE.name());
			if (teamId == null) {
				throw new NotFoundException("Team not found");
			}

			Member member = memberRepo.findByIdAndStatusAndRole(adminId, Status.ACTIVE.name(), RoleType.MANAGER.name());
			if (member == null) {
				throw new NotFoundException("You are not authorized");
			}

			Team existingMember = teamRepo.findByIdAndMembersIdInAndMembersStatus(dto.getTeamId(), dto.getTeamMembers(),
					Status.ACTIVE.name());
			if (existingMember != null) {
				LOGGER.error("Members {} already exist in the team", dto.getTeamMembers());
				throw new AppException("One or more members already exists in the team");
			}

			List<Member> teamMembers = memberRepo.findAllByIdInAndStatusAndRoleIn(dto.getTeamMembers(),
					Status.ACTIVE.name(), ALL_ROLES);
			if (teamMembers.size() != dto.getTeamMembers().size()) {
				LOGGER.error("One or more team members are not active");
				throw new NotFoundException("One or more team members are not active");
			}

			List<Member> allTeamMembers = teamId.getMembers();
			allTeamMembers.addAll(teamMembers);
			teamId.setModifiedBy(member);
			teamId.setModifiedDate(LocalDateTime.now());
			teamId.setMembers(allTeamMembers);

			Team updatedTeam = teamRepo.save(teamId);
			return convertTeamToDto(updatedTeam);

		} catch (Exception e) {
			LOGGER.error("Exception occurred while adding team members to team {}: {}", dto.getTeamId(),
					e.getMessage());
			throw new AppException(e.getMessage());
		}
	}

	public void removeTeamMember(String teamId, String adminId, String teamMemberId) {
		try {
			Team team = teamRepo.findByIdAndStatus(teamId, Status.ACTIVE.name());
			if (team == null) {
				throw new NotFoundException("Team not found");
			}

			Member admin = memberRepo.findByIdAndStatusAndRole(adminId, Status.ACTIVE.name(), RoleType.MANAGER.name());
			if (admin == null) {
				throw new NotFoundException("Not authorized");
			}

			Member teamMember = team.getMembers().stream().filter(member -> member.getId().equals(teamMemberId))
					.findFirst().orElseThrow(() -> new NotFoundException("Team member not found in the team"));

			team.getMembers().remove(teamMember);
			team.setModifiedBy(admin);
			team.setModifiedDate(LocalDateTime.now());
			teamRepo.save(team);

		} catch (Exception e) {
			LOGGER.error("Exception occurred while removing member {} from team {}: {}", teamMemberId, teamId,
					e.getMessage());
			throw new AppException("Exception occurred while removing team member: " + e.getMessage());
		}
	}

	public List<String> getAllTeamsByMemberId(String memberId) {
		try {
			Member member = memberRepo.findByIdAndStatus(memberId, Status.ACTIVE.name());
			if (member == null) {
				throw new NotFoundException("Member Id not found");
			}

			return teamRepo.findAllByStatusAndMembersId(Status.ACTIVE.name(), memberId).stream().map(Team::getTeamName)
					.collect(Collectors.toList());

		} catch (Exception e) {
			LOGGER.error("Exception occurred while getting teams for member {}: {}", memberId, e.getMessage());
			throw new AppException("Exception occurred while fetching team list: " + e.getMessage());
		}
	}

	public List<Team> getAllTeamsUnderAdmin(String memberId) {
		try {
			Member member = memberRepo.findByIdAndStatus(memberId, Status.ACTIVE.name());
			if (member == null) {
				throw new NotFoundException("Member Id not found");
			}

			return teamRepo.findAllByStatusAndMembersId(Status.ACTIVE.name(), memberId);

		} catch (Exception e) {
			LOGGER.error("Exception occurred while getting all teams under admin {}: {}", memberId, e.getMessage());
			throw new AppException("Exception occurred while fetching teams under admin: " + e.getMessage());
		}
	}

	public List<CommonTeamResponseDto> searchTeams(String teamName) {
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Team> query = cb.createQuery(Team.class);
			Root<Team> root = query.from(Team.class);
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.equal(root.get("status"), Status.ACTIVE.name()));

			if (teamName != null) {
				Predicate team = cb.like(root.get("teamName"), "%" + teamName + "%");
				Predicate id = cb.like(root.join("members").get("id"), "%" + teamName + "%");
				Predicate fname = cb.like(root.join("members").get("firstName"), "%" + teamName + "%");
				Predicate lname = cb.like(root.join("members").get("lastName"), "%" + teamName + "%");
				predicates.add(cb.or(team, id, fname, lname));
			}
			query.select(root).distinct(true).where(predicates.toArray(new Predicate[0]));

			query.orderBy(cb.desc(root.get("createdDate")));
			List<Team> teams = entityManager.createQuery(query).getResultList();
			return convertTeamsToDto(teams);
		} catch (Exception e) {
			throw new AppException("Exception occurred while searching team: " + e.getMessage());
		}
	}

	List<CommonTeamResponseDto> convertTeamsToDto(List<Team> teams) {
		return teams.stream().map(this::convertTeamToDto).collect(Collectors.toList());
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

	private CommonMemberResponseDto convertMemberToDto(Member member) {
		if (member == null)
			return null;
		CommonMemberResponseDto commonMemberResponseDto = new CommonMemberResponseDto();
		commonMemberResponseDto.setId(member.getId());
		commonMemberResponseDto.setFirstName(member.getFirstName());
		commonMemberResponseDto.setLastName(member.getLastName());
		commonMemberResponseDto.setRole(member.getRole());
		return commonMemberResponseDto;
	}

}
