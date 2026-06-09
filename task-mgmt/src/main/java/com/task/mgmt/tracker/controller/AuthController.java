package com.task.mgmt.tracker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.task.mgmt.tracker.request.payload.LoginDto;
import com.task.mgmt.tracker.request.payload.ResgisterDto;
import com.task.mgmt.tracker.response.payload.LoginResponseDto;
import com.task.mgmt.tracker.response.payload.MemberResponseDto;
import com.task.mgmt.tracker.service.AuthService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin("*")
public class AuthController {

	@Autowired
	AuthService authService;

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	@PostMapping("/register")
	public ResponseEntity<MemberResponseDto> handleSignIn(@Valid @RequestBody ResgisterDto resgisterDto) {
		logger.info("Resiter new member");
		MemberResponseDto addMember = authService.handleSignIn(resgisterDto);
		logger.info("Register member added successfully");
		return new ResponseEntity<MemberResponseDto>(addMember, HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> loginUser(@Valid @RequestBody LoginDto loginDto) {
		logger.info("User('{}')  try to login...!", loginDto.getUserName());
		LoginResponseDto loginUser = authService.login(loginDto);
		logger.info("User '{}' successfully logged in", loginDto.getUserName());
		return new ResponseEntity<LoginResponseDto>(loginUser, HttpStatus.OK);
	}


	@SecurityRequirement(name = "token")
	@PostMapping("/logout")
	public ResponseEntity<String> logout() {
		logger.info("Logging out ...!");
		String logoutUser = authService.logoutUser();
		logger.info("Logout user successfully...");
		return new ResponseEntity<>(logoutUser, HttpStatus.OK);
	}

}
