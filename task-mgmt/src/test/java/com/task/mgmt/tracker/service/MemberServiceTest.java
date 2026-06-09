package com.task.mgmt.tracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.task.mgmt.tracker.constant.RoleType;
import com.task.mgmt.tracker.constant.Status;
import com.task.mgmt.tracker.entity.Member;
import com.task.mgmt.tracker.entity.Team;
import com.task.mgmt.tracker.exception.AppException;
import com.task.mgmt.tracker.repository.MemberRepository;
import com.task.mgmt.tracker.repository.TeamRepository;
import com.task.mgmt.tracker.request.payload.CreateMemberRequesteDto;
import com.task.mgmt.tracker.request.payload.UpdateMemberRequestDto;
import com.task.mgmt.tracker.response.payload.MemberResponseDto;
import com.task.mgmt.tracker.response.payload.MemberResponsePayload;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class MemberServiceTest {

	@InjectMocks
	private MemberService memberService;

	@Mock
	private MemberRepository memberRepo;

	@Mock
	private TeamRepository teamRepo;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private KeycloakService keycloakService;

	@Mock
	private EntityManager entityManager;

	@Mock
	private CriteriaBuilder criteriaBuilder;

	@Mock
	private CriteriaQuery<Member> criteriaQuery;

	@Mock
	private Root<Member> root;

	@Mock
	private TypedQuery<Member> typedQuery;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void addMember_PositiveCase_EmployeeCreation() {
		CreateMemberRequesteDto dto = buildCreateMemberDto(RoleType.EMPLOYEE.name());

		Member createdByManager = new Member();
		createdByManager.setId("M001");
		createdByManager.setRole(RoleType.MANAGER.name());
		createdByManager.setStatus(Status.ACTIVE.name());

		Mockito.when(memberRepo.findByIdAndStatusAndRole("M001", Status.ACTIVE.name(), RoleType.MANAGER.name()))
				.thenReturn(createdByManager);
		Mockito.when(memberRepo.existsById(dto.getMemberId())).thenReturn(false);
		Mockito.when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
		Mockito.when(memberRepo.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

		MemberResponseDto result = memberService.addMember(dto, "M001");

		assertNotNull(result);
	}

	@Test
	void addMember_NegativeCase_MemberAlreadyExists() {
		CreateMemberRequesteDto dto = buildCreateMemberDto(RoleType.EMPLOYEE.name());
		Member createdByManager = new Member();
		createdByManager.setId("M001");
		createdByManager.setRole(RoleType.MANAGER.name());
		createdByManager.setStatus(Status.ACTIVE.name());

		Mockito.when(memberRepo.findByIdAndStatusAndRole(anyString(), anyString(), anyString()))
				.thenReturn(createdByManager);
		Mockito.when(memberRepo.existsById(dto.getMemberId())).thenReturn(true);

		assertThrows(AppException.class, () -> memberService.addMember(dto, "M001"));
	}

	@Test
	void addMember_NegativeCase_ManagerCannotCreateManager() {
		CreateMemberRequesteDto dto = buildCreateMemberDto(RoleType.MANAGER.name());

		Member manager = new Member();
		manager.setId("M001");
		manager.setRole(RoleType.MANAGER.name());
		manager.setStatus(Status.ACTIVE.name());

		Mockito.when(memberRepo.findByIdAndStatusAndRole("M001", Status.ACTIVE.name(), RoleType.MANAGER.name()))
				.thenReturn(manager);

		AppException exception = assertThrows(AppException.class, () -> memberService.addMember(dto, "M001"));
		assertTrue(exception.getMessage().contains("Manager cannot create"));
	}

	@Test
	void addMember_NegativeCase_CreatingManagerNotFound() {
		CreateMemberRequesteDto dto = buildCreateMemberDto(RoleType.EMPLOYEE.name());

		Mockito.when(memberRepo.findByIdAndStatusAndRole("M001", Status.ACTIVE.name(), RoleType.MANAGER.name()))
				.thenReturn(null);

		AppException exception = assertThrows(AppException.class, () -> memberService.addMember(dto, "M001"));
		assertTrue(exception.getMessage().toLowerCase().contains("manager not found"));
	}

	private CreateMemberRequesteDto buildCreateMemberDto(String role) {
		CreateMemberRequesteDto dto = new CreateMemberRequesteDto();
		dto.setMemberId("emp001");
		dto.setFirstName("John");
		dto.setLastName("Doe");
		dto.setPhoneNumber("1234567890");
		dto.setEmailId("john@example.com");
		dto.setRole(role);
		dto.setDesignation("Developer");
		dto.setAddress("Test Address");
		dto.setYearsOfExperience(3.5f);
		dto.setDateOfJoining(LocalDate.of(2022, 1, 1));
		dto.setDob(LocalDate.of(1995, 1, 1));
		dto.setGender("Male");
		return dto;
	}

	@Test
	void updateMember_Positive_AllFields() {
		String memberId = "EMP001";
		String modifierId = "MAN001";

		Member existingMember = new Member();
		existingMember.setId(memberId);
		existingMember.setRole("EMPLOYEE");

		Member modifyingManager = new Member();
		modifyingManager.setId(modifierId);
		modifyingManager.setRole("MANAGER");

		UpdateMemberRequestDto updateDto = new UpdateMemberRequestDto();
		updateDto.setFirstName("UpdatedFirst");
		updateDto.setLastName("UpdatedLast");
		updateDto.setPhoneNumber("9876543210");
		updateDto.setEmailId("updated@example.com");
		updateDto.setAddress("Updated address");
		updateDto.setDob(LocalDate.of(1990, 1, 1));
		updateDto.setDateOfJoining(LocalDate.of(2020, 1, 1));
		updateDto.setGender("Female");
		updateDto.setYearsOfExperience(5.0f);

		Mockito.when(memberRepo.findById(memberId)).thenReturn(Optional.of(existingMember));
		Mockito.when(memberRepo.findByIdAndStatusAndRole(modifierId, "ACTIVE", "MANAGER")).thenReturn(modifyingManager);
		Mockito.when(memberRepo.save(any(Member.class))).thenAnswer(i -> i.getArgument(0));

		MemberResponseDto result = memberService.updateMember(memberId, modifierId, updateDto);

		assertEquals("UpdatedFirst", result.getFirstName());
	}

	@Test
	void updateMember_Positive_PartialUpdate() {
		String memberId = "EMP002";
		String modifierId = "MAN002";

		Member existingMember = new Member();
		existingMember.setId(memberId);
		existingMember.setRole("EMPLOYEE");

		Member modifyingManager = new Member();
		modifyingManager.setId(modifierId);
		modifyingManager.setRole("MANAGER");

		UpdateMemberRequestDto updateDto = new UpdateMemberRequestDto();
		updateDto.setFirstName("NewName");

		Mockito.when(memberRepo.findById(memberId)).thenReturn(Optional.of(existingMember));
		Mockito.when(memberRepo.findByIdAndStatusAndRole(modifierId, "ACTIVE", "MANAGER")).thenReturn(modifyingManager);
		Mockito.when(memberRepo.save(any(Member.class))).thenAnswer(i -> i.getArgument(0));

		MemberResponseDto result = memberService.updateMember(memberId, modifierId, updateDto);

		assertEquals("NewName", result.getFirstName());
	}

	@Test
	void updateMember_Positive_OnlyExperienceUpdate() {
		String memberId = "EMP003";
		String modifierId = "MAN003";

		Member existingMember = new Member();
		existingMember.setId(memberId);
		existingMember.setRole("EMPLOYEE");

		Member modifyingManager = new Member();
		modifyingManager.setId(modifierId);
		modifyingManager.setRole("MANAGER");

		UpdateMemberRequestDto updateDto = new UpdateMemberRequestDto();
		updateDto.setYearsOfExperience(3.5f);

		Mockito.when(memberRepo.findById(memberId)).thenReturn(Optional.of(existingMember));
		Mockito.when(memberRepo.findByIdAndStatusAndRole(modifierId, "ACTIVE", "MANAGER")).thenReturn(modifyingManager);
		Mockito.when(memberRepo.save(any(Member.class))).thenAnswer(i -> i.getArgument(0));

		MemberResponseDto result = memberService.updateMember(memberId, modifierId, updateDto);

		assertEquals(3.5f, result.getYearsOfExperience());
	}

	@Test
	void updateMember_Negative_MemberNotFound() {
		Mockito.when(memberRepo.findById("INVALID")).thenReturn(Optional.empty());

		AppException exception = assertThrows(AppException.class,
				() -> memberService.updateMember("INVALID", "MAN001", new UpdateMemberRequestDto()));

		assertTrue(exception.getMessage().contains("Manager not found"));
	}

	@Test
	void updateMember_Negative_ModifierNotFound() {
		Member existingMember = new Member();
		existingMember.setId("EMP004");
		existingMember.setRole("EMPLOYEE");

		Mockito.when(memberRepo.findById("EMP004")).thenReturn(Optional.of(existingMember));
		Mockito.when(memberRepo.findByIdAndStatusAndRole("MAN_INVALID", "ACTIVE", "MANAGER")).thenReturn(null);

		AppException exception = assertThrows(AppException.class,
				() -> memberService.updateMember("EMP004", "MAN_INVALID", new UpdateMemberRequestDto()));

		assertTrue(exception.getMessage().contains("Manager not found"));
	}

	@Test
	void updateMember_Negative_ExceptionOnSave() {
		String memberId = "EMP005";
		String modifierId = "MAN005";

		Member existingMember = new Member();
		existingMember.setId(memberId);
		existingMember.setRole("EMPLOYEE");

		Member modifyingManager = new Member();
		modifyingManager.setId(modifierId);
		modifyingManager.setRole("MANAGER");

		UpdateMemberRequestDto updateDto = new UpdateMemberRequestDto();
		updateDto.setFirstName("CrashTest");

		Mockito.when(memberRepo.findById(memberId)).thenReturn(Optional.of(existingMember));
		Mockito.when(memberRepo.findByIdAndStatusAndRole(modifierId, "ACTIVE", "MANAGER")).thenReturn(modifyingManager);
		Mockito.when(memberRepo.save(any(Member.class))).thenThrow(new RuntimeException("DB save failed"));

		AppException exception = assertThrows(AppException.class,
				() -> memberService.updateMember(memberId, modifierId, updateDto));

		assertTrue(exception.getMessage().contains("DB save failed"));
	}

	@Test
	void getAllActiveMembers_Positive_ReturnsList() {
		Member m1 = new Member();
		m1.setId("EMP001");
		m1.setStatus(Status.ACTIVE.name());

		Member m2 = new Member();
		m2.setId("EMP002");
		m2.setStatus(Status.ACTIVE.name());

		List<Member> activeMembers = Arrays.asList(m1, m2);

		Mockito.when(memberRepo.findAllByStatusOrderByCreatedDateDesc(Status.ACTIVE.name())).thenReturn(activeMembers);

		List<Member> result = memberService.getAllActiveMembers();

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("EMP001", result.get(0).getId());
	}

	@Test
	void getAllActiveMembers_Positive_ReturnsEmptyList() {
		Mockito.when(memberRepo.findAllByStatusOrderByCreatedDateDesc(Status.ACTIVE.name()))
				.thenReturn(Collections.emptyList());

		List<Member> result = memberService.getAllActiveMembers();

		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	void getAllActiveMembers_Negative_RepositoryThrowsException() {
		Mockito.when(memberRepo.findAllByStatusOrderByCreatedDateDesc(Status.ACTIVE.name()))
				.thenThrow(new RuntimeException("Database error"));

		AppException exception = assertThrows(AppException.class, () -> memberService.getAllActiveMembers());

		assertTrue(exception.getMessage().contains("Database error"));
	}

	@Test
	void getAllActiveMembers_Negative_RepositoryReturnsNull() {
		Mockito.when(memberRepo.findAllByStatusOrderByCreatedDateDesc(Status.ACTIVE.name())).thenReturn(null);

		List<Member> result = memberService.getAllActiveMembers();
		assertNull(result);
	}

	@Test
	void getAllTeamMembers_Positive_MultipleTeamsWithActiveMembers() {
		String adminId = "ADMIN001";

		Member m1 = new Member();
		m1.setId("EMP001");
		m1.setStatus(Status.ACTIVE.name());

		Member m2 = new Member();
		m2.setId("EMP002");
		m2.setStatus(Status.ACTIVE.name());

		Team team1 = new Team();
		team1.setMembers(Arrays.asList(m1));

		Team team2 = new Team();
		team2.setMembers(Arrays.asList(m2));

		Mockito.when(teamRepo.findAllByTeamLeadIdId(adminId)).thenReturn(Arrays.asList(team1, team2));

		List<Member> result = memberService.getAllTeamMembers(adminId);

		assertEquals(2, result.size());
		assertTrue(result.contains(m1));
		assertTrue(result.contains(m2));
	}

	@Test
	void getAllTeamMembers_Positive_FilterInactiveMembers() {
		String adminId = "ADMIN002";

		Member activeMember = new Member();
		activeMember.setId("EMP_ACTIVE");
		activeMember.setStatus(Status.ACTIVE.name());

		Member inactiveMember = new Member();
		inactiveMember.setId("EMP_INACTIVE");
		inactiveMember.setStatus(Status.INACTIVE.name());

		Team team = new Team();
		team.setMembers(Arrays.asList(activeMember, inactiveMember));

		Mockito.when(teamRepo.findAllByTeamLeadIdId(adminId)).thenReturn(Collections.singletonList(team));

		List<Member> result = memberService.getAllTeamMembers(adminId);

		assertEquals(1, result.size());
		assertEquals("EMP_ACTIVE", result.get(0).getId());
	}

	@Test
	void getAllTeamMembers_Negative_RepositoryThrowsException() {
		String adminId = "ADMIN003";

		Mockito.when(teamRepo.findAllByTeamLeadIdId(adminId)).thenThrow(new RuntimeException("DB error"));

		AppException exception = assertThrows(AppException.class, () -> memberService.getAllTeamMembers(adminId));

		assertTrue(exception.getMessage().contains("DB error"));
	}

	@Test
	void getAllTeamMembers_Negative_NoTeamsFound() {
		String adminId = "ADMIN004";

		Mockito.when(teamRepo.findAllByTeamLeadIdId(adminId)).thenReturn(Collections.emptyList());

		List<Member> result = memberService.getAllTeamMembers(adminId);

		assertTrue(result.isEmpty());
	}

	@Test
	void getMember_Positive_ValidMemberId() {
		Member member = new Member();
		member.setId("EMP001");
		member.setFirstName("John");

		Mockito.when(memberRepo.findById("EMP001")).thenReturn(Optional.of(member));

		MemberResponseDto result = memberService.getMember("EMP001");

		assertNotNull(result);
		assertEquals("EMP001", result.getId());
		assertEquals("John", result.getFirstName());
	}

	@Test
	void getMember_Positive_AllFieldsPopulated() {
		Member member = new Member();
		member.setId("EMP002");
		member.setFirstName("Jane");
		member.setLastName("Doe");
		member.setEmailId("jane@example.com");
		member.setStatus(Status.ACTIVE.name());

		Mockito.when(memberRepo.findById("EMP002")).thenReturn(Optional.of(member));

		MemberResponseDto result = memberService.getMember("EMP002");

		assertEquals("EMP002", result.getId());
		assertEquals("Jane", result.getFirstName());
		assertEquals("Doe", result.getLastName());
		assertEquals("jane@example.com", result.getEmailId());
	}

	@Test
	void getMember_Negative_MemberNotFound() {
		Mockito.when(memberRepo.findById("INVALID_ID")).thenReturn(Optional.empty());

		AppException exception = assertThrows(AppException.class, () -> memberService.getMember("INVALID_ID"));

		assertEquals("Member id not found", exception.getMessage());
	}

	@Test
	void getMember_Negative_RepositoryThrowsException() {
		Mockito.when(memberRepo.findById("EMP003")).thenThrow(new RuntimeException("Database error"));

		AppException exception = assertThrows(AppException.class, () -> memberService.getMember("EMP003"));

		assertTrue(exception.getMessage().contains("Database error"));
	}

	@Test
	void getAllMembersByRoleAndSearch_Positive_ManagerSearchByName() {
		Member manager = new Member();
		manager.setId("MAN001");
		manager.setRole(RoleType.MANAGER.name());
		manager.setStatus(Status.ACTIVE.name());

		Member employee = new Member();
		employee.setId("EMP001");
		employee.setFirstName("Alice");
		employee.setLastName("Smith");
		employee.setDesignation("QA");
		employee.setStatus(Status.ACTIVE.name());

		CriteriaBuilder cb = mock(CriteriaBuilder.class);
		CriteriaQuery<Member> cq = mock(CriteriaQuery.class);
		Root<Member> root = mock(Root.class);
		Predicate predicate = mock(Predicate.class);

		Path<String> firstNamePath = mock(Path.class);
		Path<String> lastNamePath = mock(Path.class);
		Path<String> designationPath = mock(Path.class);
		Path<String> idPath = mock(Path.class);
		Path<String> statusPath = mock(Path.class);
		Path<LocalDateTime> createdDatePath = mock(Path.class);

		CriteriaBuilder.Case<Object> caseExpression = mock(CriteriaBuilder.Case.class);
		Order ascOrder = mock(Order.class);
		Order descOrder = mock(Order.class);

		when(memberRepo.findByIdAndStatus("MAN001", Status.ACTIVE.name())).thenReturn(manager);

		when(entityManager.getCriteriaBuilder()).thenReturn(cb);
		when(cb.createQuery(Member.class)).thenReturn(cq);
		when(cq.from(Member.class)).thenReturn(root);

		when(root.<String>get("firstName")).thenReturn(firstNamePath);
		when(root.<String>get("lastName")).thenReturn(lastNamePath);
		when(root.<String>get("designation")).thenReturn(designationPath);
		when(root.<String>get("id")).thenReturn(idPath);
		when(root.<String>get("status")).thenReturn(statusPath);
		when(root.<LocalDateTime>get("createdDate")).thenReturn(createdDatePath);

		when(cb.lower(firstNamePath)).thenReturn(firstNamePath);
		when(cb.lower(lastNamePath)).thenReturn(lastNamePath);
		when(cb.lower(designationPath)).thenReturn(designationPath);
		when(cb.like(firstNamePath, "%ali%")).thenReturn(predicate);
		when(cb.like(lastNamePath, "%ali%")).thenReturn(predicate);
		when(cb.like(designationPath, "%ali%")).thenReturn(predicate);
		when(cb.like(idPath, "%Ali%")).thenReturn(predicate);
		when(cb.or(predicate, predicate, predicate, predicate)).thenReturn(predicate);

		when(cb.selectCase()).thenReturn(caseExpression);
		when(caseExpression.when(cb.equal(statusPath, Status.ACTIVE.name()), 1)).thenReturn(caseExpression);
		when(caseExpression.when(cb.equal(statusPath, Status.INACTIVE.name()), 2)).thenReturn(caseExpression);
		when(caseExpression.when(cb.equal(statusPath, Status.DROP.name()), 3)).thenReturn(caseExpression);

		when(cb.asc(caseExpression)).thenReturn(ascOrder);
		when(cb.desc(createdDatePath)).thenReturn(descOrder);
		when(cq.select(root)).thenReturn(cq);
		when(cq.where(predicate)).thenReturn(cq);
		when(cq.orderBy(ascOrder, descOrder)).thenReturn(cq);

		TypedQuery<Member> typedQuery = mock(TypedQuery.class);
		when(entityManager.createQuery(cq)).thenReturn(typedQuery);
		when(typedQuery.getResultList()).thenReturn(List.of(employee));

		List<MemberResponsePayload> result = memberService.getAllMembersByRoleAndSearch("MAN001", "Ali");

		assertEquals(1, result.size());
		assertEquals("EMP001", result.get(0).getId());
	}

	@Test
	void getAllMembersByRoleAndSearch_Positive_EmployeeSearchByDesignation() {
		Member emp = new Member();
		emp.setId("EMP002");
		emp.setRole(RoleType.EMPLOYEE.name());
		emp.setStatus(Status.ACTIVE.name());

		Member match = new Member();
		match.setId("EMP003");
		match.setFirstName("David");
		match.setDesignation("Developer");
		match.setStatus(Status.ACTIVE.name());

		Member manager = new Member();
		manager.setId("MAN001");
		manager.setRole(RoleType.MANAGER.name());
		manager.setStatus(Status.ACTIVE.name());

		Team mockTeam = new Team();
		mockTeam.setTeamLeadId(manager);
		Mockito.when(memberRepo.findByIdAndStatus("MAN001", Status.ACTIVE.name())).thenReturn(manager);


		CriteriaBuilder cb = Mockito.mock(CriteriaBuilder.class);
		CriteriaQuery<Member> cq = Mockito.mock(CriteriaQuery.class);
		Root<Member> root = Mockito.mock(Root.class);
		TypedQuery<Member> typedQuery = Mockito.mock(TypedQuery.class);

		Predicate likeFirstName = Mockito.mock(Predicate.class);
		Predicate likeLastName = Mockito.mock(Predicate.class);
		Predicate likeDesignation = Mockito.mock(Predicate.class);
		Predicate likeId = Mockito.mock(Predicate.class);
		Predicate orPredicate = Mockito.mock(Predicate.class);
		Predicate statusPredicate = Mockito.mock(Predicate.class);
		Predicate finalPredicate = Mockito.mock(Predicate.class);

		@SuppressWarnings("unchecked")
		CriteriaBuilder.Case<Object> caseExpression = Mockito.mock(CriteriaBuilder.Case.class);

		Order ascOrder = Mockito.mock(Order.class);
		Order descOrder = Mockito.mock(Order.class);

		Path<String> firstNamePath = Mockito.mock(Path.class);
		Path<String> lastNamePath = Mockito.mock(Path.class);
		Path<String> designationPath = Mockito.mock(Path.class);
		Path<String> idPath = Mockito.mock(Path.class);
		Path<String> statusPath = Mockito.mock(Path.class);
		Path<LocalDateTime> createdDatePath = Mockito.mock(Path.class);

		Mockito.when(entityManager.getCriteriaBuilder()).thenReturn(cb);
		Mockito.when(cb.createQuery(Member.class)).thenReturn(cq);
		Mockito.when(cq.from(Member.class)).thenReturn(root);

		when(root.<String>get("firstName")).thenReturn(firstNamePath);
		when(root.<String>get("lastName")).thenReturn(lastNamePath);
		when(root.<String>get("designation")).thenReturn(designationPath);
		when(root.<String>get("id")).thenReturn(idPath);
		when(root.<String>get("status")).thenReturn(statusPath);
		when(root.<LocalDateTime>get("createdDate")).thenReturn(createdDatePath);

		Mockito.when(cb.lower(firstNamePath)).thenReturn(firstNamePath);
		Mockito.when(cb.lower(lastNamePath)).thenReturn(lastNamePath);
		Mockito.when(cb.lower(designationPath)).thenReturn(designationPath);

		Mockito.when(cb.like(Mockito.eq(firstNamePath), Mockito.anyString())).thenReturn(likeFirstName);
		Mockito.when(cb.like(Mockito.eq(lastNamePath), Mockito.anyString())).thenReturn(likeLastName);
		Mockito.when(cb.like(Mockito.eq(designationPath), Mockito.anyString())).thenReturn(likeDesignation);
		Mockito.when(cb.like(Mockito.eq(idPath), Mockito.anyString())).thenReturn(likeId);

		Mockito.when(cb.or(likeFirstName, likeLastName, likeDesignation, likeId)).thenReturn(orPredicate);

		Mockito.when(root.get("status").in(Status.ACTIVE.name(), Status.INACTIVE.name())).thenReturn(statusPredicate);
		Mockito.when(cb.and(statusPredicate, orPredicate)).thenReturn(finalPredicate);

		Mockito.when(cq.select(root)).thenReturn(cq);
		Mockito.when(cq.where(finalPredicate)).thenReturn(cq);

		Mockito.when(cb.selectCase()).thenReturn(caseExpression);
		Mockito.when(caseExpression.when(Mockito.any(), Mockito.anyInt())).thenReturn(caseExpression);
		Mockito.when(cb.asc(caseExpression)).thenReturn(ascOrder);
		Mockito.when(cb.desc(createdDatePath)).thenReturn(descOrder);
		Mockito.when(cq.orderBy(ascOrder, descOrder)).thenReturn(cq);

		Mockito.when(entityManager.createQuery(cq)).thenReturn(typedQuery);
		Mockito.when(typedQuery.getResultList()).thenReturn(List.of(match));

		List<MemberResponsePayload> result = memberService.getAllMembersByRoleAndSearch("MAN001", "Dev");

		assertEquals(1, result.size());
		assertEquals("EMP003", result.get(0).getId());
	}

	@Test
	void getAllMembersByRoleAndSearch_Negative_MemberNotFound() {
		Mockito.when(memberRepo.findByIdAndStatus("INVALID", Status.ACTIVE.name())).thenReturn(null);

		AppException exception = assertThrows(AppException.class,
				() -> memberService.getAllMembersByRoleAndSearch("INVALID", "search"));

		assertEquals("Member not found", exception.getMessage());
	}

	@Test
	void getAllMembersByRoleAndSearch_Negative_InternalError() {
		Member manager = new Member();
		manager.setId("MAN002");
		manager.setRole(RoleType.MANAGER.name());
		manager.setStatus(Status.ACTIVE.name());

		Mockito.when(memberRepo.findByIdAndStatus("MAN002", Status.ACTIVE.name())).thenReturn(manager);
		Mockito.when(entityManager.getCriteriaBuilder()).thenThrow(new RuntimeException("DB failure"));

		AppException exception = assertThrows(AppException.class,
				() -> memberService.getAllMembersByRoleAndSearch("MAN002", "QA"));

		assertTrue(exception.getMessage().contains("DB failure"));
	}

}