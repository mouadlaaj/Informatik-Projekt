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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.task.mgmt.tracker.entity.Team;
import com.task.mgmt.tracker.request.payload.CreateTeamRequestDto;
import com.task.mgmt.tracker.request.payload.UpdateTeamMemberRequestDto;
import com.task.mgmt.tracker.request.payload.UpdateTeamRequestDto;
import com.task.mgmt.tracker.response.payload.CommonTeamResponseDto;
import com.task.mgmt.tracker.response.payload.MessageDto;
import com.task.mgmt.tracker.service.TeamService;
import com.task.mgmt.tracker.utils.AppUtils;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/teams")
@CrossOrigin("*")
@SecurityRequirement(name = "token")
public class TeamController {

	private static final Logger logger = LoggerFactory.getLogger(TeamController.class);

	@Autowired
	TeamService teamService;

	@PostMapping
	public ResponseEntity<CommonTeamResponseDto> addNewTeam(@Valid @RequestBody CreateTeamRequestDto dto) {
		String loggedUser = AppUtils.getLoggedInUser();
		logger.info("Adding a new team by: {}", loggedUser);
		CommonTeamResponseDto addNewTeam = teamService.addNewTeam(loggedUser,dto);
		logger.info("New team added successfully: {}", addNewTeam);
		return new ResponseEntity<>(addNewTeam, HttpStatus.CREATED);
	}

	@PostMapping("/member")
	public ResponseEntity<CommonTeamResponseDto> addTeamMember(@Valid @RequestBody UpdateTeamMemberRequestDto dto) {
		String loggedUser = AppUtils.getLoggedInUser();
		CommonTeamResponseDto team = teamService.addTeamMember(loggedUser,dto);
		return new ResponseEntity<>(team, HttpStatus.OK);
	}

	@PutMapping("/{teamId}")
	public ResponseEntity<CommonTeamResponseDto> updateTeam(@Valid @RequestBody UpdateTeamRequestDto updateTeamDto,
			@PathVariable("teamId") String teamId) {
		String loggedUser = AppUtils.getLoggedInUser();
		logger.info("Updating team with ID {} by member ID: {}", teamId);
		CommonTeamResponseDto updateTeam = teamService.updateTeamById(teamId,loggedUser,updateTeamDto);
		logger.info("Team with ID {} successfully updated by member with ID: {}", teamId);
		return new ResponseEntity<>(updateTeam, HttpStatus.OK);
	}

	@GetMapping("/in")
	public ResponseEntity<List<String>> getAllTeamsByMemberId() {
		String loggedUser = AppUtils.getLoggedInUser();
		List<String> teams = teamService.getAllTeamsByMemberId(loggedUser);
		return ResponseEntity.ok(teams);
	}
	
	@GetMapping("/admin")
	public ResponseEntity<List<Team>> getAllTeamsUnderAdmin() {
		String loggedUser = AppUtils.getLoggedInUser();
		List<Team> teams = teamService.getAllTeamsUnderAdmin(loggedUser);
		return ResponseEntity.ok(teams);
	}

	@GetMapping("/{teamId}")
	public ResponseEntity<CommonTeamResponseDto> getTeamDetails(@PathVariable String teamId) {
		logger.info("Fetching team details for team ID: {}", teamId);
		CommonTeamResponseDto teamDetails = teamService.getTeamDetails(teamId);
		logger.info("Retrieved team details successfully for team ID: {}", teamId);
		return new ResponseEntity<>(teamDetails, HttpStatus.OK);
	}

	@DeleteMapping("/{teamId}")
	public ResponseEntity<MessageDto> deleteTeamById(@PathVariable("teamId") String teamId) {
		String loggedUser = AppUtils.getLoggedInUser();
		logger.info("Deleting the team {} by adminId: {}", teamId, loggedUser);
		teamService.deleteTeamById(teamId, loggedUser);
		logger.info("Team with ID {} successfully deleted by member with ID {}", teamId, loggedUser);
		MessageDto deleteTeamMessage = new MessageDto("Team successfully deleted", LocalDateTime.now());
		return new ResponseEntity<MessageDto>(deleteTeamMessage, HttpStatus.OK);
	}

	@DeleteMapping("/member/{teamId}/{teamMemberId}")
	public ResponseEntity<?> removeTeamMember(@PathVariable(name = "teamId") String teamId, @PathVariable("teamMemberId") String teamMemberId) {
		String loggedUser = AppUtils.getLoggedInUser();
		teamService.removeTeamMember(teamId, loggedUser, teamMemberId);
		MessageDto deleteMember = new MessageDto("Team member removed successfully", LocalDateTime.now());
		return new ResponseEntity<MessageDto>(deleteMember, HttpStatus.ACCEPTED);
	}

	@GetMapping
	public ResponseEntity<List<CommonTeamResponseDto>> searchTeams(@RequestParam(required = false, name = "search") String searchText) {
		logger.info("Searching teams with filters");
		List<CommonTeamResponseDto> teams = teamService.searchTeams(searchText);
		logger.info("Successfully fetched teams with filters : {}", teams.size());
		return new ResponseEntity<List<CommonTeamResponseDto>>(teams, HttpStatus.OK);
	}

}
