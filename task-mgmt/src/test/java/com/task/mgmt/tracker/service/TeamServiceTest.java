
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
import com.task.mgmt.tracker.service.MemberService;
import com.task.mgmt.tracker.service.TeamService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

class TeamServiceTest {

	@Mock
	private MemberRepository memberRepo;

	@Mock
	private TeamRepository teamRepo;

	@InjectMocks
	private TeamService teamService;

	@Mock
	private MemberService memberService;

	@Mock
	private EntityManager entityManager;

	@Mock
	private Root<Team> root;

	@Mock
	private Join<Object, Object> memberJoin;

	@Mock
	private TypedQuery<Team> typedQuery;
	

	@Mock
	private CriteriaBuilder criteriaBuilder;

	@Mock
	private CriteriaQuery<Team> criteriaQuery;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testAddNewTeam_Success_AllValid() {
		String adminId = "admin123";
		String leaderId = "lead001";

		CreateTeamRequestDto dto = new CreateTeamRequestDto();
		dto.setTeamName("Alpha Team");
		dto.setTeamLeadId(leaderId);
		dto.setTeamMembers(List.of(leaderId, "mem002"));

		Member leader = new Member();
		leader.setId(leaderId);
		leader.setRole(RoleType.MANAGER.name());
		leader.setStatus(Status.ACTIVE.name());

		Member member = new Member();
		member.setId("mem002");
		member.setRole(RoleType.EMPLOYEE.name());
		member.setStatus(Status.ACTIVE.name());

		when(memberRepo.findByIdAndStatusAndRole(eq(leaderId), eq(Status.ACTIVE.name()), eq(RoleType.MANAGER.name())))
				.thenReturn(leader);
		when(memberRepo.findAllByIdInAndStatusAndRoleIn(anyList(), eq(Status.ACTIVE.name()), anyList()))
				.thenReturn(List.of(leader, member));

		Team savedTeam = new Team();
		savedTeam.setId("team001");
		savedTeam.setTeamName("Alpha Team");
		savedTeam.setTeamLeadId(leader);
		savedTeam.setMembers(List.of(leader, member));
		savedTeam.setStatus(Status.ACTIVE.name());

		when(teamRepo.save(any(Team.class))).thenReturn(savedTeam);

		CommonTeamResponseDto response = teamService.addNewTeam(adminId, dto);
		assertNotNull(response);
		assertEquals("Alpha Team", response.getTeamName());
	}

	@Test
	void testAddNewTeam_Success_LeaderAlreadyInTeamMembers() {
		String adminId = "admin456";
		String leaderId = "lead002";

		CreateTeamRequestDto dto = new CreateTeamRequestDto();
		dto.setTeamName("Delta Squad");
		dto.setTeamLeadId(leaderId);
		dto.setTeamMembers(List.of(leaderId, "mem003"));

		Member leader = new Member();
		leader.setId(leaderId);
		leader.setRole(RoleType.MANAGER.name());
		leader.setStatus(Status.ACTIVE.name());

		Member member = new Member();
		member.setId("mem003");
		member.setRole(RoleType.EMPLOYEE.name());
		member.setStatus(Status.ACTIVE.name());

		when(memberService.validMemberCheck(eq(adminId), anyString(), eq(RoleType.MANAGER.name()))).thenReturn(leader);
		when(memberRepo.findByIdAndStatusAndRole(leaderId, Status.ACTIVE.name(), RoleType.MANAGER.name()))
				.thenReturn(leader);

		when(memberRepo.findAllByIdInAndStatusAndRoleIn(anyList(), eq(Status.ACTIVE.name()), anyList()))
				.thenReturn(List.of(leader, member));

		Team savedTeam = new Team();
		savedTeam.setId("team002");
		savedTeam.setTeamName("Delta Squad");
		savedTeam.setTeamLeadId(leader);
		savedTeam.setMembers(List.of(leader, member));
		savedTeam.setStatus(Status.ACTIVE.name());

		when(teamRepo.save(any(Team.class))).thenReturn(savedTeam);

		CommonTeamResponseDto response = teamService.addNewTeam(adminId, dto);

		assertNotNull(response);
		assertEquals("Delta Squad", response.getTeamName());
		assertEquals(2, response.getMembers().size());
	}

	@Test
	void testAddNewTeam_LeaderNotFound() {
		CreateTeamRequestDto dto = new CreateTeamRequestDto();
		dto.setTeamName("Team X");
		dto.setTeamLeadId("leadX");
		dto.setTeamMembers(List.of("mem1"));

		when(memberRepo.findByIdAndStatusAndRole("leadX", Status.ACTIVE.name(), RoleType.MANAGER.name()))
				.thenReturn(null);

		assertThrows(AppException.class, () -> teamService.addNewTeam("admin", dto));
	}

	@Test
	void testAddNewTeam_MemberMismatch() {
		CreateTeamRequestDto dto = new CreateTeamRequestDto();
		dto.setTeamName("Team Y");
		dto.setTeamLeadId("leadY");
		dto.setTeamMembers(List.of("memY"));

		Member leader = new Member();
		leader.setId("leadY");
		leader.setRole(RoleType.MANAGER.name());
		leader.setStatus(Status.ACTIVE.name());

		when(memberRepo.findByIdAndStatusAndRole("leadY", Status.ACTIVE.name(), RoleType.MANAGER.name()))
				.thenReturn(leader);

		when(memberRepo.findAllByIdInAndStatusAndRoleIn(anyList(), eq(Status.ACTIVE.name()), anyList()))
				.thenReturn(List.of());

		assertThrows(AppException.class, () -> teamService.addNewTeam("admin", dto));
	}

	@Test
	void testGetTeamDetails_Success() {
		Team team = new Team();
		team.setId("team123");
		team.setStatus(Status.ACTIVE.name());

		when(teamRepo.findByIdAndStatus("team123", Status.ACTIVE.name())).thenReturn(team);

		CommonTeamResponseDto result = teamService.getTeamDetails("team123");
		assertNotNull(result);
		assertEquals("team123", result.getId());
	}

	@Test
	void testGetTeamDetails_NotFound() {
		when(teamRepo.findByIdAndStatus("team123", Status.ACTIVE.name())).thenReturn(null);

		NotFoundException ex = assertThrows(NotFoundException.class, () -> {
			teamService.getTeamDetails("team123");
		});
		assertTrue(ex.getMessage().contains("Team Id not found"));
	}

	@Test
	void testGetTeamDetails_ExceptionThrown() {
		when(teamRepo.findByIdAndStatus(anyString(), anyString())).thenThrow(new RuntimeException("DB Error"));

		NotFoundException ex = assertThrows(NotFoundException.class, () -> {
			teamService.getTeamDetails("anyId");
		});
		assertTrue(ex.getMessage().contains("Exception occurred while getting team details"));
	}

	@Test
	void testDeleteTeamById_Success() {
		String teamId = "team123";
		String memberId = "admin123";

		Member manager = new Member();
		manager.setId(memberId);
		manager.setRole(RoleType.MANAGER.name());
		manager.setStatus(Status.ACTIVE.name());

		Team team = new Team();
		team.setId(teamId);
		team.setStatus(Status.ACTIVE.name());

		when(memberRepo.findByIdAndStatusAndRole(memberId, Status.ACTIVE.name(), RoleType.MANAGER.name()))
				.thenReturn(manager);
		when(teamRepo.findByIdAndStatus(teamId, Status.ACTIVE.name())).thenReturn(team);

		assertDoesNotThrow(() -> teamService.deleteTeamById(teamId, memberId));
		verify(teamRepo, times(1)).save(team);
	}

	@Test
	void testDeleteTeamById_TeamNotFound() {
		String teamId = "teamX";
		String memberId = "adminX";

		Member manager = new Member();
		manager.setId(memberId);
		manager.setRole(RoleType.MANAGER.name());
		manager.setStatus(Status.ACTIVE.name());

		when(memberRepo.findByIdAndStatusAndRole(memberId, Status.ACTIVE.name(), RoleType.MANAGER.name()))
				.thenReturn(manager);
		when(teamRepo.findByIdAndStatus(teamId, Status.ACTIVE.name())).thenReturn(null);

		AppException ex = assertThrows(AppException.class, () -> {
			teamService.deleteTeamById(teamId, memberId);
		});
		assertEquals("Team id not found", ex.getMessage());
	}

	@Test
	void testDeleteTeamById_AdminNotFound() {
		String teamId = "team123";
		String memberId = "admin123";

		when(memberRepo.findByIdAndStatusAndRole(memberId, Status.ACTIVE.name(), RoleType.MANAGER.name()))
				.thenReturn(null);

		AppException ex = assertThrows(AppException.class, () -> {
			teamService.deleteTeamById(teamId, memberId);
		});
		assertEquals("Admin id not found: admin123", ex.getMessage());
	}

	@Test
	void testUpdateTeamById_Positive_UpdateOnlyName() {
		String teamId = "team001";
		String adminId = "admin001";

		UpdateTeamRequestDto dto = new UpdateTeamRequestDto();
		dto.setTeamName("New Team Name");

		Team team = new Team();
		team.setId(teamId);
		team.setTeamName("Old Team Name");

		Member admin = new Member();
		admin.setId(adminId);
		admin.setRole(RoleType.MANAGER.name());
		admin.setStatus(Status.ACTIVE.name());

		when(teamRepo.findByIdAndStatus(teamId, Status.ACTIVE.name())).thenReturn(team);
		when(memberRepo.findByIdAndStatusAndRole(adminId, Status.ACTIVE.name(), RoleType.MANAGER.name()))
				.thenReturn(admin);

		team.setTeamName(dto.getTeamName());
		team.setModifiedBy(admin);
		team.setModifiedDate(LocalDateTime.now());

		when(teamRepo.save(any())).thenReturn(team);

		CommonTeamResponseDto response = teamService.updateTeamById(teamId, adminId, dto);

		assertNotNull(response);
		assertEquals("New Team Name", response.getTeamName());
	}

	@Test
	void testUpdateTeamById_Negative_TeamNotFound() {
		String teamId = "invalidTeam";
		String adminId = "admin001";
		UpdateTeamRequestDto dto = new UpdateTeamRequestDto();

		when(teamRepo.findByIdAndStatus(teamId, Status.ACTIVE.name())).thenReturn(null);

		AppException exception = assertThrows(AppException.class, () -> {
			teamService.updateTeamById(teamId, adminId, dto);
		});

		assertEquals("Team id not found.", exception.getMessage());
	}

	@Test
	void testAddTeamMember_Positive_AllValid() {
		String teamId = "team001";
		String adminId = "admin001";
		List<String> newMemberIds = List.of("mem002");

		UpdateTeamMemberRequestDto dto = new UpdateTeamMemberRequestDto();
		dto.setTeamId(teamId);
		dto.setTeamMembers(newMemberIds);

		Member existingMember = new Member();
		existingMember.setId("mem001");

		Team team = new Team();
		team.setId(teamId);
		team.setTeamName("Alpha");
		team.setMembers(new ArrayList<>(List.of(existingMember)));

		Member admin = new Member();
		admin.setId(adminId);
		admin.setRole(RoleType.MANAGER.name());
		admin.setStatus(Status.ACTIVE.name());

		Member newMember = new Member();
		newMember.setId("mem002");
		newMember.setRole(RoleType.EMPLOYEE.name());
		newMember.setStatus(Status.ACTIVE.name());

		when(teamRepo.findByIdAndStatus(teamId, Status.ACTIVE.name())).thenReturn(team);
		when(memberRepo.findByIdAndStatusAndRole(adminId, Status.ACTIVE.name(), RoleType.MANAGER.name()))
				.thenReturn(admin);
		when(teamRepo.findByIdAndMembersIdInAndMembersStatus(teamId, newMemberIds, Status.ACTIVE.name()))
				.thenReturn(null);
		when(memberRepo.findAllByIdInAndStatusAndRoleIn(newMemberIds, Status.ACTIVE.name(),
				Arrays.asList("MANAGER", "EMPLOYEE"))).thenReturn(List.of(newMember));
		when(teamRepo.save(any())).thenReturn(team);

		CommonTeamResponseDto response = teamService.addTeamMember(adminId, dto);

		assertNotNull(response);
		assertEquals("Alpha", response.getTeamName());
		assertEquals(2, response.getMembers().size());
	}

	@Test
	void testAddTeamMember_Positive_AddMultiple() {
		String teamId = "team002";
		String adminId = "admin001";
		List<String> newMemberIds = List.of("mem010", "mem011");

		UpdateTeamMemberRequestDto dto = new UpdateTeamMemberRequestDto();
		dto.setTeamId(teamId);
		dto.setTeamMembers(newMemberIds);

		Team team = new Team();
		team.setId(teamId);
		team.setMembers(new ArrayList<>());

		Member admin = new Member();
		admin.setId(adminId);
		admin.setRole(RoleType.MANAGER.name());
		admin.setStatus(Status.ACTIVE.name());

		Member m1 = new Member();
		m1.setId("mem010");
		m1.setRole("EMPLOYEE");
		m1.setStatus("ACTIVE");
		Member m2 = new Member();
		m2.setId("mem011");
		m2.setRole("EMPLOYEE");
		m2.setStatus("ACTIVE");

		when(teamRepo.findByIdAndStatus(teamId, Status.ACTIVE.name())).thenReturn(team);
		when(memberRepo.findByIdAndStatusAndRole(adminId, Status.ACTIVE.name(), RoleType.MANAGER.name()))
				.thenReturn(admin);
		when(teamRepo.findByIdAndMembersIdInAndMembersStatus(teamId, newMemberIds, Status.ACTIVE.name()))
				.thenReturn(null);
		when(memberRepo.findAllByIdInAndStatusAndRoleIn(newMemberIds, Status.ACTIVE.name(),
				Arrays.asList("MANAGER", "EMPLOYEE"))).thenReturn(List.of(m1, m2));
		when(teamRepo.save(any())).thenReturn(team);

		CommonTeamResponseDto response = teamService.addTeamMember(adminId, dto);
		assertEquals(2, response.getMembers().size());
	}

	@Test
	void testAddTeamMember_Negative_AdminNotFound() {
		UpdateTeamMemberRequestDto dto = new UpdateTeamMemberRequestDto();
		dto.setTeamId("team001");
		dto.setTeamMembers(List.of("mem002"));

		Team team = new Team();
		team.setId("team001");

		when(teamRepo.findByIdAndStatus("team001", Status.ACTIVE.name())).thenReturn(team);
		when(memberRepo.findByIdAndStatusAndRole("admin001", Status.ACTIVE.name(), RoleType.MANAGER.name()))
				.thenReturn(null);

		AppException ex = assertThrows(AppException.class, () -> teamService.addTeamMember("admin001", dto));

		assertTrue(ex.getMessage().contains("You are not authorized"));
	}

	@Test
	void testAddTeamMember_Negative_MemberAlreadyExists() {
		UpdateTeamMemberRequestDto dto = new UpdateTeamMemberRequestDto();
		dto.setTeamId("team001");
		dto.setTeamMembers(List.of("mem002"));

		Team team = new Team();
		team.setId("team001");

		Member admin = new Member();
		admin.setId("admin001");
		admin.setRole(RoleType.MANAGER.name());
		admin.setStatus(Status.ACTIVE.name());

		when(teamRepo.findByIdAndStatus("team001", Status.ACTIVE.name())).thenReturn(team);
		when(memberRepo.findByIdAndStatusAndRole("admin001", Status.ACTIVE.name(), RoleType.MANAGER.name()))
				.thenReturn(admin);
		when(teamRepo.findByIdAndMembersIdInAndMembersStatus("team001", List.of("mem002"), Status.ACTIVE.name()))
				.thenReturn(team);

		AppException ex = assertThrows(AppException.class, () -> teamService.addTeamMember("admin001", dto));

		assertTrue(ex.getMessage().contains("already exists"));
	}

	@Test
	void testRemoveTeamMember_Positive() {
		String teamId = "team001";
		String adminId = "admin001";
		String memberId = "mem001";

		Member admin = new Member();
		admin.setId(adminId);
		admin.setRole("MANAGER");
		admin.setStatus("ACTIVE");
		Member memberToRemove = new Member();
		memberToRemove.setId(memberId);

		Team team = new Team();
		team.setId(teamId);
		team.setMembers(new ArrayList<>(List.of(memberToRemove)));

		when(teamRepo.findByIdAndStatus(teamId, "ACTIVE")).thenReturn(team);
		when(memberRepo.findByIdAndStatusAndRole(adminId, "ACTIVE", "MANAGER")).thenReturn(admin);
		when(teamRepo.save(any())).thenReturn(team);

		assertDoesNotThrow(() -> teamService.removeTeamMember(teamId, adminId, memberId));
		verify(teamRepo).save(any());
	}

	@Test
	void testRemoveTeamMember_TeamNotFound() {
		when(teamRepo.findByIdAndStatus(anyString(), eq("ACTIVE"))).thenReturn(null);

		AppException ex = assertThrows(AppException.class,
				() -> teamService.removeTeamMember("invalidTeam", "admin001", "mem001"));

		assertTrue(ex.getMessage().contains("Team not found"));
	}

	@Test
	void testGetAllTeamsByMemberId_Positive() {
		String memberId = "mem001";
		Member member = new Member();
		member.setId(memberId);
		member.setStatus("ACTIVE");

		Team team1 = new Team();
		team1.setTeamName("Alpha");
		Team team2 = new Team();
		team2.setTeamName("Beta");

		when(memberRepo.findByIdAndStatus(memberId, "ACTIVE")).thenReturn(member);
		when(teamRepo.findAllByStatusAndMembersId("ACTIVE", memberId)).thenReturn(List.of(team1, team2));

		List<String> result = teamService.getAllTeamsByMemberId(memberId);

		assertEquals(2, result.size());
		assertTrue(result.contains("Alpha"));
		assertTrue(result.contains("Beta"));
	}

	@Test
	void testGetAllTeamsByMemberId_MemberNotFound() {
		when(memberRepo.findByIdAndStatus("invalidId", "ACTIVE")).thenReturn(null);

		AppException ex = assertThrows(AppException.class, () -> teamService.getAllTeamsByMemberId("invalidId"));

		assertTrue(ex.getMessage().contains("Member Id not found"));
	}

	@Test
	void testGetAllTeamsUnderAdmin_Positive() {
		String adminId = "admin001";
		Member admin = new Member();
		admin.setId(adminId);
		admin.setStatus("ACTIVE");

		Team team1 = new Team();
		team1.setId("T1");
		Team team2 = new Team();
		team2.setId("T2");

		when(memberRepo.findByIdAndStatus(adminId, "ACTIVE")).thenReturn(admin);
		when(teamRepo.findAllByStatusAndMembersId("ACTIVE", adminId)).thenReturn(List.of(team1, team2));

	}

	@Test
	void testGetAllTeamsUnderAdmin_AdminNotFound() {
		when(memberRepo.findByIdAndStatus("invalidAdmin", "ACTIVE")).thenReturn(null);

		AppException ex = assertThrows(AppException.class, () -> teamService.getAllTeamsUnderAdmin("invalidAdmin"));

		assertTrue(ex.getMessage().contains("Member Id not found"));
	}

	@Test
	void testSearchTeams_Exception() {
		when(entityManager.getCriteriaBuilder()).thenThrow(new RuntimeException("Database error"));

		AppException ex = assertThrows(AppException.class, () -> teamService.searchTeams("Alpha"));

		assertTrue(ex.getMessage().contains("Exception occurred while searching team"));
	}

	@Test
	void testSearchTeams_SuccessfulSearch() {
	    Member testMember = new Member();
	    testMember.setId("member123");
	    testMember.setFirstName("John");
	    testMember.setLastName("Doe");
	    testMember.setStatus(Status.ACTIVE.name());

	    Team testTeam = new Team();
	    testTeam.setId("team123");
	    testTeam.setTeamName("Development Team");
	    testTeam.setStatus(Status.ACTIVE.name());
	    testTeam.setCreatedDate(LocalDateTime.now());
	    testTeam.setModifiedDate(LocalDateTime.now());
	    testTeam.setMembers(List.of(testMember));

	    String teamName = "Development";

	    setupCriteriaBuilderMocks();
	    when(typedQuery.getResultList()).thenReturn(List.of(testTeam));


	    List<CommonTeamResponseDto> result = teamService.searchTeams(teamName);

	    assertNotNull(result);
	    assertEquals(1, result.size());
	    assertEquals("team123", result.get(0).getId());
	    assertEquals("Development Team", result.get(0).getTeamName());

	    verify(criteriaBuilder).equal(root.get("status"), Status.ACTIVE.name());
	    verify(criteriaBuilder).like(root.get("teamName"), "%" + teamName + "%");
	    verify(criteriaBuilder).like(memberJoin.get("id"), "%" + teamName + "%");
	    verify(criteriaBuilder).like(memberJoin.get("firstName"), "%" + teamName + "%");
	    verify(criteriaBuilder).like(memberJoin.get("lastName"), "%" + teamName + "%");
	    verify(criteriaBuilder).or(any(), any(), any(), any());
	    verify(criteriaQuery).select(root);
	    verify(criteriaQuery).distinct(true);
	    verify(criteriaQuery).where(any(Predicate[].class));
	}


	private void setupCriteriaBuilderMocks() {
		when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
		when(criteriaBuilder.createQuery(Team.class)).thenReturn(criteriaQuery);
		when(criteriaQuery.from(Team.class)).thenReturn(root);
		when(root.join("members")).thenReturn(memberJoin);

		when(root.get("status")).thenReturn(mock(jakarta.persistence.criteria.Path.class));
		when(root.get("teamName")).thenReturn(mock(jakarta.persistence.criteria.Path.class));
		when(root.get("createdDate")).thenReturn(mock(jakarta.persistence.criteria.Path.class));
		when(memberJoin.get("id")).thenReturn(mock(jakarta.persistence.criteria.Path.class));
		when(memberJoin.get("firstName")).thenReturn(mock(jakarta.persistence.criteria.Path.class));
		when(memberJoin.get("lastName")).thenReturn(mock(jakarta.persistence.criteria.Path.class));

		when(criteriaBuilder.equal(any(), any())).thenReturn(mock(Predicate.class));
		when(criteriaBuilder.like(any(), anyString())).thenReturn(mock(Predicate.class));
		when(criteriaBuilder.or(any(), any(), any(), any())).thenReturn(mock(Predicate.class));
		when(criteriaBuilder.desc(any())).thenReturn(mock(jakarta.persistence.criteria.Order.class));

		when(criteriaQuery.select(root)).thenReturn(criteriaQuery);
		when(criteriaQuery.distinct(true)).thenReturn(criteriaQuery);
		when(criteriaQuery.where(any(Predicate[].class))).thenReturn(criteriaQuery);
		when(criteriaQuery.orderBy(any(jakarta.persistence.criteria.Order.class))).thenReturn(criteriaQuery);

		when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
	}

	private CommonTeamResponseDto createExpectedDto() {
		CommonTeamResponseDto dto = new CommonTeamResponseDto();
		dto.setId("team123");
		dto.setTeamName("Development Team");
		dto.setStatus(Status.ACTIVE.name());
		dto.setCreatedDate(LocalDateTime.now());
		dto.setModifiedDate(LocalDateTime.now());

		CommonMemberResponseDto memberDto = new CommonMemberResponseDto();
		memberDto.setId("member123");
		memberDto.setFirstName("John");
		memberDto.setLastName("Doe");

		Set<CommonMemberResponseDto> memberDtos = new HashSet<>();
		memberDtos.add(memberDto);
		dto.setMembers(memberDtos);

		return dto;
	}

}
