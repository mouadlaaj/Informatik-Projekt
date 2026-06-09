package com.task.mgmt.tracker.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.task.mgmt.tracker.entity.Member;
import com.task.mgmt.tracker.request.payload.CreateMemberRequesteDto;
import com.task.mgmt.tracker.request.payload.UpdateMemberRequestDto;
import com.task.mgmt.tracker.response.payload.MemberResponseDto;
import com.task.mgmt.tracker.response.payload.MemberResponsePayload;
import com.task.mgmt.tracker.service.MemberService;
import com.task.mgmt.tracker.utils.AppUtils;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/members")
@CrossOrigin("*")
@SecurityRequirement(name = "token")
public class MemberController {

	@Autowired
	MemberService memberService;

	private static final Logger LOGGER = LoggerFactory.getLogger(MemberController.class);

	@PostMapping
	public ResponseEntity<MemberResponseDto> addMember(@Valid @RequestBody CreateMemberRequesteDto memberRequesteDto) {
		String loggedUser = AppUtils.getLoggedInUser();
		LOGGER.info("Adding new member, Added by: {}", loggedUser);
		MemberResponseDto addMember = memberService.addMember(memberRequesteDto, loggedUser);
		LOGGER.info("New member added successfully, Added by: {}", loggedUser);
		return new ResponseEntity<MemberResponseDto>(addMember, HttpStatus.CREATED);
	}

	@PutMapping("/{memberId}")
	public ResponseEntity<MemberResponseDto> updateMember(@PathVariable("memberId") String memberId,
			@Valid @RequestBody UpdateMemberRequestDto memberRequesteDto) {
		String loggedUser = AppUtils.getLoggedInUser();
		LOGGER.info("Updating member by ID: {} and updated by: {}", memberId, loggedUser);
		MemberResponseDto updateMember = memberService.updateMember(memberId, loggedUser, memberRequesteDto);
		LOGGER.info("Member with ID {} updated successfully. Updated by: {}", memberId, loggedUser);
		return new ResponseEntity<MemberResponseDto>(updateMember, HttpStatus.OK);
	}

	@GetMapping("/admins")
	public ResponseEntity<List<Member>> getAllAdmins() {
		LOGGER.info("Received request to get all admins.");
		List<Member> admins = memberService.getAllAdmins();
		LOGGER.info("Retrieved {} admins successfully.", admins.size());
		return ResponseEntity.ok(admins);
	}

	@GetMapping
	public ResponseEntity<List<Member>> getAllActiveMembers() {
		LOGGER.info("Received request to get all active users.");
		List<Member> admins = memberService.getAllActiveMembers();
		LOGGER.info("Retrieved {} admins successfully.", admins.size());
		return ResponseEntity.ok(admins);
	}

	@GetMapping("/team-members/{adminId}")
	public ResponseEntity<List<Member>> getAllTeamMembers(@PathVariable("adminId") String adminId) {
		LOGGER.info("Received request to get all active users.");
		List<Member> admins = memberService.getAllTeamMembers(adminId);
		LOGGER.info("Retrieved {} admins successfully.", admins.size());
		return ResponseEntity.ok(admins);
	}

	@GetMapping("/{memberId}")
	public ResponseEntity<MemberResponseDto> getMember(@PathVariable("memberId") String memberId) {
		LOGGER.info("Retrieving member by ID: {} f", memberId);
		MemberResponseDto member = memberService.getMember(memberId);
		LOGGER.info("Member by ID {} retrieved successfully", memberId);
		return new ResponseEntity<MemberResponseDto>(member, HttpStatus.OK);
	}

	@GetMapping("/search")
	public ResponseEntity<List<MemberResponsePayload>> getAllMembersByRoleAndSearch(
			@RequestParam(name = "searchTerm", required = false) String searchTerm) {
		String loggedUser = AppUtils.getLoggedInUser();
		LOGGER.info("Fetching all members for memberId: {} with searchTerm: {}", loggedUser, searchTerm);

		List<MemberResponsePayload> members = memberService.getAllMembersByRoleAndSearch(loggedUser, searchTerm);

		LOGGER.info("Successfully fetched {} members for memberId: {} with searchTerm: {}", members.size(), loggedUser,
				searchTerm);

		return new ResponseEntity<>(members, HttpStatus.OK);
	}

}
