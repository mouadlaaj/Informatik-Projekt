package com.task.mgmt.tracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.task.mgmt.tracker.constant.Status;
import com.task.mgmt.tracker.entity.Member;
import com.task.mgmt.tracker.entity.Team;
import com.task.mgmt.tracker.exception.AppException;
import com.task.mgmt.tracker.repository.MemberRepository;
import com.task.mgmt.tracker.repository.TeamRepository;
import com.task.mgmt.tracker.request.payload.LoginDto;
import com.task.mgmt.tracker.request.payload.ResgisterDto;
import com.task.mgmt.tracker.response.payload.LoginResponseDto;
import com.task.mgmt.tracker.response.payload.MemberResponseDto;

public class AuthServiceTest {

	@InjectMocks
	private AuthService authService;

	@Mock
	private MemberRepository memberRepo;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private KeycloakService keycloakService;

	@Mock
	private MemberService memberService;

	@Mock
	private TeamRepository teamRepo;

	private RestTemplate mockRestTemplate;

	@BeforeEach
	public void setup() throws Exception {
		MockitoAnnotations.openMocks(this);

		injectField(authService, "tokenUri", "http://localhost:8080/realms/demo/protocol/openid-connect/token");
		injectField(authService, "clientId", "test-client");
		injectField(authService, "clientSecret", "test-secret");

		mockRestTemplate = Mockito.mock(RestTemplate.class);
		injectField(authService, "restTemplate", mockRestTemplate);
	}

	@Test
	public void login_PositiveCase_ValidCredentials() throws Exception {
		LoginDto loginDto = new LoginDto();
		loginDto.setUserName("test@example.com");
		loginDto.setPassword("Password@123");

		Member member = new Member();
		member.setId("M001");
		member.setEmailId("test@example.com");
		member.setFirstName("John");
		member.setLastName("Doe");
		member.setGender("Male");
		member.setRole("EMPLOYEE");
		member.setDesignation("Developer");

		Mockito.when(memberRepo.findByIdAndStatus(eq("test@example.com"), eq(Status.ACTIVE.name()))).thenReturn(member);
		Mockito.when(teamRepo.findFirstByTeamLeadIdId("M001")).thenReturn(new Team());

		Map<String, Object> tokenMap = Map.of("access_token", "mock-token");
		ResponseEntity<Map> tokenResponse = ResponseEntity.ok(tokenMap);

		Mockito.when(mockRestTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
				any(ParameterizedTypeReference.class))).thenReturn(tokenResponse);

		LoginResponseDto response = authService.login(loginDto);

		assertNotNull(response);
		assertEquals("mock-token", response.getToken());
		assertEquals("M001", response.getId());
	}

	private void injectField(Object target, String fieldName, Object value) throws Exception {
		Field field = target.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(target, value);
	}

	@Test
	void login_NegativeCase_UserNotFound() {
		Mockito.when(memberRepo.findByIdAndStatus(anyString(), anyString())).thenReturn(null);
		LoginDto dto = new LoginDto();
		dto.setUserName("wrong@example.com");
		dto.setPassword("Password@123");
		assertThrows(AppException.class, () -> authService.login(dto));
	}

	@Test
	void login_NegativeCase_MissingToken() {
		Member member = new Member();
		member.setId("M001");
		member.setEmailId("test@example.com");
		Mockito.when(memberRepo.findByIdAndStatus(anyString(), anyString())).thenReturn(member);

		Map<String, Object> responseMap = Map.of();
		ResponseEntity<Map> response = ResponseEntity.ok(responseMap);

		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), any(Class.class)))
				.thenReturn(response);

		LoginDto dto = new LoginDto();
		dto.setUserName("test@example.com");
		dto.setPassword("Password@123");
		assertThrows(AppException.class, () -> authService.login(dto));
	}


	@Test
	void logoutUser_PositiveCase() {
		var authentication = new UsernamePasswordAuthenticationToken("testUser", null);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		String result = authService.logoutUser();

		assertEquals("logout successfully", result);

		SecurityContextHolder.clearContext();
	}

	@Test
	void logoutUser_NegativeCase_ThrowsAppException() {
		SecurityContextHolder.clearContext();

		AppException ex = assertThrows(AppException.class, () -> {
			authService.logoutUser();
		});

		System.out.println("Actual exception message: " + ex.getMessage());
		assertEquals(
				"Cannot invoke \"org.springframework.security.core.Authentication.getName()\" because the return value of \"org.springframework.security.core.context.SecurityContext.getAuthentication()\" is null",
				ex.getMessage());
	}


	@Test
	void handleSignIn_PositiveCase() {
		ResgisterDto dto = new ResgisterDto();
		dto.setEmailId("new@example.com");
		dto.setPassword("Password@123");
		dto.setMemberId("M002");
		dto.setFirstName("Jane");
		dto.setLastName("Smith");
		dto.setPhoneNumber("1234567890");
		dto.setGender("Female");
		dto.setDesignation("Tester");
		dto.setRole("EMPLOYEE");
		dto.setAddress("Some address");
		dto.setDob(LocalDate.of(1995, 2, 10));

		Mockito.when(memberRepo.existsById("new@example.com")).thenReturn(false);
		Mockito.when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
		Mockito.when(memberRepo.save(any(Member.class))).thenReturn(new Member());
		Mockito.when(memberService.convertToMemberResponseDto(any(Member.class))).thenReturn(new MemberResponseDto());

		MemberResponseDto result = authService.handleSignIn(dto);
		assertNotNull(result);
	}

	@Test
	void handleSignIn_PositiveCase_Manager() {
		ResgisterDto dto = new ResgisterDto();
		dto.setEmailId("manager@example.com");
		dto.setPassword("Password@123");
		dto.setMemberId("M003");
		dto.setFirstName("Manager");
		dto.setLastName("Lead");
		dto.setPhoneNumber("9876543210");
		dto.setGender("Male");
		dto.setDesignation("Manager");
		dto.setRole("MANAGER");
		dto.setAddress("HQ Office");
		dto.setDob(LocalDate.of(1988, 7, 15));
		dto.setYearsOfExperience(8.5f);

		Mockito.when(memberRepo.existsById("manager@example.com")).thenReturn(false);
		Mockito.when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
		Mockito.when(memberRepo.save(any(Member.class))).thenReturn(new Member());
		Mockito.when(memberService.convertToMemberResponseDto(any(Member.class))).thenReturn(new MemberResponseDto());

		MemberResponseDto result = authService.handleSignIn(dto);

		assertNotNull(result);
	}

	@Test
	void handleSignIn_NegativeCase_EmailExists() {
		ResgisterDto dto = new ResgisterDto();
		dto.setEmailId("exists@example.com");
		Mockito.when(memberRepo.existsById("exists@example.com")).thenReturn(true);
		assertThrows(AppException.class, () -> authService.handleSignIn(dto));
	}

	@Test
	void handleSignIn_NegativeCase_SaveFails() {
		ResgisterDto dto = new ResgisterDto();
		dto.setEmailId("fail@example.com");
		dto.setPassword("Password@123");
		dto.setMemberId("M004");
		dto.setFirstName("Fail");
		dto.setLastName("Case");
		dto.setPhoneNumber("1234567890");
		dto.setGender("Other");
		dto.setDesignation("QA");
		dto.setRole("EMPLOYEE");
		dto.setAddress("Another Address");
		dto.setDob(LocalDate.of(1990, 1, 1));

		Mockito.when(memberRepo.existsById("fail@example.com")).thenReturn(false);
		Mockito.when(memberRepo.save(any())).thenThrow(new RuntimeException("DB Error"));
		assertThrows(AppException.class, () -> authService.handleSignIn(dto));
	}
}