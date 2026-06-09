package com.task.mgmt.tracker.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.task.mgmt.tracker.constant.Status;
import com.task.mgmt.tracker.entity.Member;
import com.task.mgmt.tracker.entity.Team;
import com.task.mgmt.tracker.exception.AlreadyExistsException;
import com.task.mgmt.tracker.exception.AppException;
import com.task.mgmt.tracker.exception.LogOutFailedException;
import com.task.mgmt.tracker.repository.MemberRepository;
import com.task.mgmt.tracker.repository.TeamRepository;
import com.task.mgmt.tracker.request.payload.LoginDto;
import com.task.mgmt.tracker.request.payload.ResgisterDto;
import com.task.mgmt.tracker.response.payload.LoginResponseDto;
import com.task.mgmt.tracker.response.payload.MemberResponseDto;

@Service
public class AuthService {

	@Autowired
	MemberRepository memberRepo;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	KeycloakService keycloakService;

	@Autowired
	MemberService memberService;

	@Autowired
	TeamRepository teamRepo;

	@Value("${keycloak.token-uri}")
	private String tokenUri;

	@Value("${keycloak.client-id}")
	private String clientId;

	@Value("${keycloak.client-secret}")
	private String clientSecret;

	@Value("${keycloak.server-url}")
	private String serverUrl;

	@Value("${keycloak.realm}")
	private String realm;

	@Autowired
	private RestTemplate restTemplate;

	private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

	public LoginResponseDto login(LoginDto loginRequest) {
		logger.info("Attempting login for email: {}", loginRequest.getUserName());

		try {
			Member member = memberRepo.findByIdAndStatus(loginRequest.getUserName(), Status.ACTIVE.name());
			if (member == null) {
				logger.error("User not found or inactive for email: {}", loginRequest.getUserName());
				throw new AppException("User not found or inactive");
			}

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
			body.add("grant_type", "password");
			body.add("client_id", clientId);
			body.add("client_secret", clientSecret);
			body.add("username", member.getEmailId());
			body.add("password", loginRequest.getPassword());

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

			ResponseEntity<Map> response = restTemplate.exchange(tokenUri, HttpMethod.POST, request,
					new ParameterizedTypeReference<>() {
					});

			if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
				logger.error("Authentication failed for email: {}", loginRequest.getUserName());
				throw new AppException("Authentication failed");
			}

			Object accessToken = response.getBody().get("access_token");
			if (accessToken == null) {
				logger.error("Access token missing in response for email: {}", loginRequest.getUserName());
				throw new AppException("Access token missing from Keycloak");
			}

			LoginResponseDto dto = new LoginResponseDto();
			dto.setToken(accessToken.toString());
			dto.setId(member.getId());
			dto.setFirstName(member.getFirstName());
			dto.setLastName(member.getLastName());
			dto.setEmail(member.getEmailId());
			dto.setGender(member.getGender());
			dto.setRole(member.getRole());
			dto.setDesignation(member.getDesignation());
			Team team = teamRepo.findFirstByTeamLeadIdId(member.getId());
			dto.setTeamLead(team != null);

			logger.info("Login successful for email: {}", loginRequest.getUserName());
			return dto;

		} catch (HttpClientErrorException.Unauthorized ex) {
			logger.error("Invalid credentials for email: {}", loginRequest.getUserName(), ex);
			throw new AppException("Invalid credentials");
		} catch (AppException e) {
			logger.error("Application exception during login: {}", loginRequest.getUserName(), e);
			throw e;
		} catch (Exception e) {
			logger.error("Unexpected error during login: {}", loginRequest.getUserName(), e);
			throw new AppException("Unexpected error during login: " + e.getMessage());
		}
	}

	public String logoutUser() {
		try {
			SecurityContextHolder.getContext().getAuthentication().getName();
			SecurityContextHolder.clearContext();
			return "logout successfully";
		} catch (LogOutFailedException e) {
			e.printStackTrace();
			logger.error("Exception occurred while LogOut: {}", e.getMessage());
			throw new LogOutFailedException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception occurred while LogOut: {}", e.getMessage());
			throw new AppException(e.getMessage());
		}
	}

	public MemberResponseDto handleSignIn(ResgisterDto resgisterDto) {
		try {
			boolean isUserNameExists = memberRepo.existsById(resgisterDto.getEmailId());

			if (isUserNameExists) {
				throw new AlreadyExistsException("Email already exists");
			} else {
				Member member = new Member();

				member.setId(resgisterDto.getMemberId());
				member.setFirstName(resgisterDto.getFirstName());
				member.setLastName(resgisterDto.getLastName());
				member.setPhoneNumber(resgisterDto.getPhoneNumber());
				member.setEmailId(resgisterDto.getEmailId());
				member.setPassword(passwordEncoder.encode(resgisterDto.getPassword()));
				member.setRole(resgisterDto.getRole());
				member.setDesignation(resgisterDto.getDesignation());
				member.setStatus(Status.ACTIVE.name());
				member.setCreatedDate(LocalDateTime.now());
				member.setAddress(resgisterDto.getAddress());
				member.setDob(resgisterDto.getDob());
				member.setGender(resgisterDto.getGender());
				member.setYearsOfExperience(resgisterDto.getYearsOfExperience());
				member.setModifiedDate(LocalDateTime.now());

				keycloakService.createUser(resgisterDto.getMemberId(), resgisterDto.getEmailId(),
						resgisterDto.getFirstName(), resgisterDto.getLastName(), resgisterDto.getPassword(),
						resgisterDto.getRole());

				Member savedMember = memberRepo.save(member);
				return memberService.convertToMemberResponseDto(savedMember);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception occurred while register member: {}", e.getMessage());
			throw new AppException("Exception occurred while register: " + e.getMessage());
		}
	}
}
