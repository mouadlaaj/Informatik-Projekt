package com.task.mgmt.tracker.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.task.mgmt.tracker.constant.RoleType;
import com.task.mgmt.tracker.constant.Status;
import com.task.mgmt.tracker.entity.Member;
import com.task.mgmt.tracker.entity.Team;
import com.task.mgmt.tracker.exception.AlreadyExistsException;
import com.task.mgmt.tracker.exception.AppException;
import com.task.mgmt.tracker.exception.NotFoundException;
import com.task.mgmt.tracker.repository.MemberRepository;
import com.task.mgmt.tracker.repository.TaskRepository;
import com.task.mgmt.tracker.repository.TeamRepository;
import com.task.mgmt.tracker.request.payload.CreateMemberRequesteDto;
import com.task.mgmt.tracker.request.payload.UpdateMemberRequestDto;
import com.task.mgmt.tracker.response.payload.CommonMemberResponseDto;
import com.task.mgmt.tracker.response.payload.MemberResponseDto;
import com.task.mgmt.tracker.response.payload.MemberResponsePayload;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class MemberService {

	@Autowired
	MemberRepository memberRepo;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	TeamRepository teamRepo;

	@Autowired
	TaskRepository taskRepo;

	@Autowired
	KeycloakService keycloakService;

	@Autowired
	EntityManager entityManager;

	@Value("${app.default.member.password}")
	private String defaultPassword;

	private static final Logger LOGGER = LoggerFactory.getLogger(MemberService.class);

	private static final List<String> STATUS = Arrays.asList(Status.ACTIVE.name(), Status.INACTIVE.name());

	public MemberResponseDto addMember(CreateMemberRequesteDto memberRequestDto, String createdMember) {
		try {
			Member creatingAdmin = validMemberCheck(createdMember, "creating member", RoleType.MANAGER.name());

			if (creatingAdmin.getRole().equals(RoleType.MANAGER.name())
					&& !memberRequestDto.getRole().equals(RoleType.EMPLOYEE.name())) {
				throw new AppException("Manager cannot create non-employee roles");
			}

			boolean isUserNameExists = memberRepo.existsById(memberRequestDto.getMemberId());

			if (isUserNameExists) {
				throw new AlreadyExistsException("Member Id already exists");
			} else {
				Member member = new Member();

				member.setId(memberRequestDto.getMemberId().toLowerCase());
				member.setFirstName(memberRequestDto.getFirstName());
				member.setLastName(memberRequestDto.getLastName());
				member.setPhoneNumber(memberRequestDto.getPhoneNumber());
				member.setEmailId(memberRequestDto.getEmailId());
				member.setPassword(passwordEncoder.encode(defaultPassword));
				member.setRole(memberRequestDto.getRole());
				member.setDesignation(memberRequestDto.getDesignation());
				member.setStatus(Status.ACTIVE.name());
				member.setCreatedDate(LocalDateTime.now());
				member.setAddress(memberRequestDto.getAddress());
				member.setDob(memberRequestDto.getDob());
				member.setGender(memberRequestDto.getGender());
				member.setJoiningDate(memberRequestDto.getDateOfJoining());
				member.setYearsOfExperience(memberRequestDto.getYearsOfExperience());
				member.setCreatedBy(creatingAdmin);
				member.setModifiedDate(LocalDateTime.now());
				member.setModifiedBy(creatingAdmin);

				keycloakService.createUser(memberRequestDto.getMemberId(), memberRequestDto.getEmailId(),
						memberRequestDto.getFirstName(), memberRequestDto.getLastName(), defaultPassword,
						memberRequestDto.getRole());

				Member savedMember = memberRepo.save(member);
				return convertToMemberResponseDto(savedMember);
			}

		} catch (Exception e) {
			LOGGER.error("Exception occurred while creating member: {}", e.getMessage());
			throw new AppException(e.getMessage());
		}
	}

	public MemberResponseDto updateMember(String memberId, String modifyedMember,
			UpdateMemberRequestDto memberRequestDto) {
		try {

			Member modifyingAdmin = validMemberCheck(modifyedMember, "updating member", RoleType.MANAGER.name());

			Member member = memberRepo.findById(memberId).orElseThrow(() -> {
				LOGGER.error("Exception occurred while updating member, member id not found : {}", memberId);
				return new NotFoundException("Member id not found: " + memberId);
			});

			if (memberRequestDto.getFirstName() != null) {
				member.setFirstName(memberRequestDto.getFirstName());
			}

			if (memberRequestDto.getLastName() != null) {
				member.setLastName(memberRequestDto.getLastName());
			}

			if (memberRequestDto.getPhoneNumber() != null) {
				member.setPhoneNumber(memberRequestDto.getPhoneNumber());
			}

			if (memberRequestDto.getEmailId() != null) {
				member.setEmailId(memberRequestDto.getEmailId());
			}

			if (memberRequestDto.getAddress() != null) {
				member.setAddress(memberRequestDto.getAddress());
			}

			if (memberRequestDto.getDateOfJoining() != null) {
				member.setJoiningDate(memberRequestDto.getDateOfJoining());
			}

			if (memberRequestDto.getDob() != null) {
				member.setDob(memberRequestDto.getDob());
			}

			if (memberRequestDto.getGender() != null) {
				member.setGender(memberRequestDto.getGender());
			}

			if (memberRequestDto.getYearsOfExperience() >= 0.0) {
				member.setYearsOfExperience(memberRequestDto.getYearsOfExperience());
			}

			member.setModifiedDate(LocalDateTime.now());
			member.setModifiedBy(modifyingAdmin);

			Member savedMember = memberRepo.save(member);
			return convertToMemberResponseDto(savedMember);

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception occurred while updating user: {}", e.getMessage());
			throw new AppException(e.getMessage());
		}
	}

	public List<Member> getAllAdmins() {
		try {
			return memberRepo.findAllByStatusAndRoleOrderByCreatedDateDesc(Status.ACTIVE.name(),
					RoleType.MANAGER.name());
		} catch (Exception e) {
			LOGGER.error("Exception occured while getting all the admins..");
			throw new AppException(e.getMessage());
		}

	}

	public List<Member> getAllActiveMembers() {
		try {
			return memberRepo.findAllByStatusOrderByCreatedDateDesc(Status.ACTIVE.name());
		} catch (Exception e) {
			LOGGER.error("Exception occured while getting all the admins..");
			throw new AppException(e.getMessage());
		}

	}

	public List<Member> getAllTeamMembers(String adminId) {
		try {
			List<Team> teams = teamRepo.findAllByTeamLeadIdId(adminId);
			return teams.stream().flatMap(team -> team.getMembers().stream())
					.filter(f -> f.getStatus().equals(Status.ACTIVE.name())).map(e -> e).distinct()
					.collect(Collectors.toList());
		} catch (Exception e) {
			LOGGER.error("Exception occured while getting all the admins..");
			throw new AppException(e.getMessage());
		}
	}

	public MemberResponseDto getMember(String memberId) {
		try {
			Optional<Member> member = memberRepo.findById(memberId);
			if (member.isPresent()) {
				return convertToMemberResponseDto(member.get());
			} else {
				LOGGER.error("Exception occurred while getting member, Member id not found: {}", memberId);
				throw new NotFoundException("Member id not found");
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception occurred while getting user: {}", e.getMessage());
			throw new AppException(e.getMessage());
		}
	}

	public List<MemberResponsePayload> getAllMembersByRoleAndSearch(String memberId, String searchTerm) {
		try {
			Member user = memberRepo.findByIdAndStatus(memberId, Status.ACTIVE.name());
			if (user == null) {
				LOGGER.error("Member not found with ID: {}", memberId);
				throw new NotFoundException("Member not found");
			}
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Member> query = cb.createQuery(Member.class);
			Root<Member> root = query.from(Member.class);

			Predicate rolePredicate;

			if (user.getRole().equals(RoleType.MANAGER.name())) {
				rolePredicate = cb.or(cb.like(cb.lower(root.get("firstName")), "%" + searchTerm.toLowerCase() + "%"),
						cb.like(cb.lower(root.get("lastName")), "%" + searchTerm.toLowerCase() + "%"),
						cb.like(cb.lower(root.get("designation")), "%" + searchTerm.toLowerCase() + "%"),
						cb.like(root.get("id"), "%" + searchTerm + "%"));

			} else {
				rolePredicate = cb.and(root.get("status").in(STATUS),
						cb.or(cb.like(cb.lower(root.get("firstName")), "%" + searchTerm.toLowerCase() + "%"),
								cb.like(cb.lower(root.get("lastName")), "%" + searchTerm.toLowerCase() + "%"),
								cb.like(cb.lower(root.get("designation")), "%" + searchTerm.toLowerCase() + "%"),
								cb.like(root.get("id"), "%" + searchTerm + "%")));
			}

			query.select(root).where(rolePredicate);

			query.orderBy(
					cb.asc(cb.selectCase().when(cb.equal(root.get("status"), Status.ACTIVE.name()), 1)
							.when(cb.equal(root.get("status"), Status.INACTIVE.name()), 2)
							.when(cb.equal(root.get("status"), Status.DROP.name()), 3)),
					cb.desc(root.get("createdDate")));

			List<Member> resultList = entityManager.createQuery(query).getResultList();
			return mapToResponseDto(resultList, user);
		} catch (Exception e) {
			LOGGER.error("Exception occurred while getting all user: {}", e.getMessage());
			throw new AppException(e.getMessage());
		}
	}

	private List<MemberResponsePayload> mapToResponseDto(List<Member> members, Member loggedMember) {
		Member teamLead = null;
		List<Member> teamMembers = new ArrayList<>();
		if (loggedMember.getRole().equals(RoleType.EMPLOYEE.name())) {
			Team team = teamRepo.findByMembersId(loggedMember.getId());
			teamLead = team.getTeamLeadId();
			teamMembers.addAll(team.getMembers());
		} else if (loggedMember.getRole().equals(RoleType.MANAGER.name())) {
			List<Team> teams = teamRepo.findAllByTeamLeadIdId(loggedMember.getId());
			teamLead = loggedMember;
			teamMembers.addAll(teams.stream().flatMap(e -> e.getMembers().stream()).collect(Collectors.toSet()));
		}
		List<MemberResponsePayload> responsePayloads = new ArrayList<>();
		List<Member> sortedTeamMembers = members.parallelStream().filter(teamMembers::contains)
				.sorted(Comparator.comparing(Member::getFirstName)).collect(Collectors.toList());
		for (Member member : sortedTeamMembers) {
			MemberResponsePayload responseDto = createResponseDto(member, teamLead);
			responsePayloads.add(responseDto);
		}
		List<Member> sortedNonTeamMembers = members.parallelStream()
				.filter(member -> !teamMembers.contains(member) && member.getStatus().equals(Status.ACTIVE.name()))
				.sorted(Comparator.comparing(Member::getFirstName)).collect(Collectors.toList());
		for (Member member : sortedNonTeamMembers) {
			MemberResponsePayload responseDto = createResponseDto(member, null);
			responsePayloads.add(responseDto);
		}

		List<Member> sortedInactiveMembers = members.parallelStream()
				.filter(member -> member.getStatus().equals(Status.INACTIVE.name()))
				.sorted(Comparator.comparing(Member::getFirstName)).collect(Collectors.toList());
		for (Member member : sortedInactiveMembers) {
			MemberResponsePayload responseDto = createResponseDto(member, null);
			responsePayloads.add(responseDto);
		}

		List<Member> sortedDropMembers = members.parallelStream()
				.filter(member -> member.getStatus().equals(Status.DROP.name()))
				.sorted(Comparator.comparing(Member::getFirstName)).collect(Collectors.toList());
		for (Member member : sortedDropMembers) {
			MemberResponsePayload responseDto = createResponseDto(member, null);
			responsePayloads.add(responseDto);
		}

		return responsePayloads;
	}

	private MemberResponsePayload createResponseDto(Member member, Member teamLead) {
		MemberResponsePayload responseDto = new MemberResponsePayload();
		responseDto.setId(member.getId());
		responseDto.setFirstName(member.getFirstName());
		responseDto.setLastName(member.getLastName());
		responseDto.setPhoneNumber(member.getPhoneNumber());
		responseDto.setEmailId(member.getEmailId());
		responseDto.setGender(member.getGender());
		responseDto.setRole(member.getRole());
		responseDto.setDesignation(member.getDesignation());
		responseDto.setStatus(member.getStatus());
		responseDto.setAddress(member.getAddress());
		responseDto.setDateOfRelieving(member.getRelievingDate());
		responseDto.setDateOfJoining(member.getJoiningDate());
		responseDto.setDob(member.getDob());
		responseDto.setYearsOfExperience(member.getYearsOfExperience());
		responseDto.setCreatedDate(member.getCreatedDate());
		responseDto.setModifiedDate(member.getModifiedDate());

		if (teamLead != null) {
			CommonMemberResponseDto teamLeadDto = new CommonMemberResponseDto();
			teamLeadDto.setId(teamLead.getId());
			teamLeadDto.setFirstName(teamLead.getFirstName());
			teamLeadDto.setLastName(teamLead.getLastName());
			teamLeadDto.setRole(teamLead.getRole());
			responseDto.setTeamLead(teamLeadDto);
		}

		return responseDto;
	}

	public MemberResponseDto convertToMemberResponseDto(Member member) {
		MemberResponseDto responseDto = new MemberResponseDto();
		responseDto.setId(member.getId());
		responseDto.setFirstName(member.getFirstName());
		responseDto.setLastName(member.getLastName());
		responseDto.setPhoneNumber(member.getPhoneNumber());
		responseDto.setEmailId(member.getEmailId());
		responseDto.setRole(member.getRole());
		responseDto.setDesignation(member.getDesignation());
		responseDto.setStatus(member.getStatus());
		responseDto.setAddress(member.getAddress());
		responseDto.setDateOfJoining(member.getJoiningDate());
		responseDto.setDob(member.getDob());
		responseDto.setYearsOfExperience(member.getYearsOfExperience());
		responseDto.setGender(member.getGender());
		responseDto.setCreatedDate(member.getCreatedDate());
		responseDto.setCreatedBy(member.getCreatedBy());
		responseDto.setModifiedDate(member.getModifiedDate());
		responseDto.setModifiedBy(member.getModifiedBy());
		return responseDto;
	}

	public Member validMemberCheck(String memberId, String errorType, String roleType) {

		Member manager = memberRepo.findByIdAndStatusAndRole(memberId, Status.ACTIVE.name(), roleType);

		if (manager == null) {
			LOGGER.error("Exception occurred while {}. Member ID: {}", errorType, memberId);
			throw new NotFoundException("Manager not found or you don't have permission");
		}

		return manager;

	}
}
