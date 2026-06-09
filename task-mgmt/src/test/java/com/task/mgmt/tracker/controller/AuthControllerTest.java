package com.task.mgmt.tracker.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;

import com.task.mgmt.tracker.request.payload.LoginDto;
import com.task.mgmt.tracker.request.payload.ResgisterDto;
import com.task.mgmt.tracker.response.payload.LoginResponseDto;
import com.task.mgmt.tracker.response.payload.MemberResponseDto;
import com.task.mgmt.tracker.service.AuthService;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private Validator validator;

    private ResgisterDto registerDto;
    private LoginDto loginDto;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        registerDto = new ResgisterDto();
        registerDto.setMemberId("mem001");
        registerDto.setFirstName("John");
        registerDto.setLastName("Doe");
        registerDto.setPhoneNumber("1234567890");
        registerDto.setPassword("Password@1");
        registerDto.setEmailId("john1234@email.com");
        registerDto.setRole("EMPLOYEE");
        registerDto.setDesignation("Developer");
        registerDto.setAddress("Some Address");
        registerDto.setDob(LocalDate.of(1995, 1, 1));
        registerDto.setGender("Male");

        loginDto = new LoginDto();
        loginDto.setUserName("john123@email.com");
        loginDto.setPassword("Password@1");
    }

    @Test
    void register_ShouldReturnMemberResponse_WhenValidInput() {
        MemberResponseDto mockResponse = new MemberResponseDto();
        mockResponse.setId("mem001");

        when(authService.handleSignIn(registerDto)).thenReturn(mockResponse);

        ResponseEntity<MemberResponseDto> response = authController.handleSignIn(registerDto);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("mem001", response.getBody().getId());
    }

    @Test
    void register_ShouldFailValidation_WhenInvalidEmail() {
        ResgisterDto dto = new ResgisterDto();
        dto.setEmailId("invalid-email");

        Set<ConstraintViolation<ResgisterDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void login_ShouldReturnToken_WhenValidInput() {
        LoginResponseDto mockResponse = new LoginResponseDto();
        mockResponse.setId("mem001");
        mockResponse.setEmail("john123@email.com");
        mockResponse.setToken("mock-token");

        when(authService.login(loginDto)).thenReturn(mockResponse);

        ResponseEntity<LoginResponseDto> response = authController.loginUser(loginDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("mock-token", response.getBody().getToken());
    }

    @Test
    void login_ShouldFailValidation_WhenPasswordIsBlank() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUserName("john123@email.com");
        loginDto.setPassword(""); // Blank password

        Set<ConstraintViolation<LoginDto>> violations = validator.validate(loginDto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void logout_ShouldReturnSuccessMessage() {
        when(authService.logoutUser()).thenReturn("Logout successful");

        ResponseEntity<String> response = authController.logout();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Logout successful", response.getBody());
    }

    @Test
    void logout_ShouldThrowException_WhenErrorOccurs() {
        when(authService.logoutUser()).thenThrow(new RuntimeException("Failed"));

        assertThrows(RuntimeException.class, () -> authController.logout());
    }
}
