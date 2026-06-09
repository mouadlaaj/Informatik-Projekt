package com.task.mgmt.tracker.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.task.mgmt.tracker.entity.Member;
import com.task.mgmt.tracker.request.payload.CreateMemberRequesteDto;
import com.task.mgmt.tracker.request.payload.UpdateMemberRequestDto;
import com.task.mgmt.tracker.response.payload.MemberResponseDto;
import com.task.mgmt.tracker.response.payload.MemberResponsePayload;
import com.task.mgmt.tracker.service.MemberService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class MemberControllerTest {

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    private CreateMemberRequesteDto createDto;
    private UpdateMemberRequestDto updateDto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        createDto = new CreateMemberRequesteDto();
        createDto.setMemberId("mem001");
        createDto.setFirstName("John");
        createDto.setLastName("Doe");
        createDto.setPhoneNumber("1234567890");
        createDto.setEmailId("john123@email.com");
        createDto.setRole("EMPLOYEE");
        createDto.setDesignation("Developer");
        createDto.setAddress("Some Address");
        createDto.setDob(LocalDate.of(1995, 1, 1));
        createDto.setGender("Male");

        updateDto = new UpdateMemberRequestDto();
        updateDto.setPhoneNumber("9876543210");
        updateDto.setAddress("Updated Address");
    }

    @Test
    void addMember_ShouldReturn201_WhenValid() {
        MemberResponseDto mockResponse = new MemberResponseDto();
        mockResponse.setId("mem001");

        when(memberService.addMember(any(), any())).thenReturn(mockResponse);

        ResponseEntity<MemberResponseDto> response = memberController.addMember(createDto);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("mem001", response.getBody().getId());
    }
    @Test
    void addMember_ShouldFailValidation_WhenInvalidFields() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        CreateMemberRequesteDto invalidDto = new CreateMemberRequesteDto();
        invalidDto.setMemberId("");
        invalidDto.setFirstName("John");
        invalidDto.setLastName("");
        invalidDto.setPhoneNumber("123");
        invalidDto.setEmailId("invalidEmail");
        invalidDto.setRole("UNKNOWN");
        invalidDto.setDesignation("");
        invalidDto.setAddress("");
        invalidDto.setDateOfJoining(null);
        invalidDto.setDob(LocalDate.now().plusDays(1));
        invalidDto.setGender("");

        Set<ConstraintViolation<CreateMemberRequesteDto>> violations = validator.validate(invalidDto);

        violations.forEach(v -> System.out.println(v.getPropertyPath() + ": " + v.getMessage()));

        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 5);
    }

    @Test
    void addMember_ShouldThrowException_WhenServiceFails() {
        when(memberService.addMember(any(), any())).thenThrow(new RuntimeException("Failed"));

        assertThrows(RuntimeException.class, () -> memberController.addMember(createDto));
    }

    @Test
    void updateMember_ShouldReturn200_WhenValid() {
        MemberResponseDto mockResponse = new MemberResponseDto();
        mockResponse.setId("mem001");

        when(memberService.updateMember(eq("mem001"), any(), eq(updateDto))).thenReturn(mockResponse);

        ResponseEntity<MemberResponseDto> response = memberController.updateMember("mem001", updateDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("mem001", response.getBody().getId());
    }
    @Test
    void updateMember_ShouldPassValidation_WhenValidInput() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        UpdateMemberRequestDto validDto = new UpdateMemberRequestDto();
        validDto.setFirstName("John");
        validDto.setLastName("Doe");
        validDto.setPhoneNumber("9876543210");
        validDto.setEmailId("john@example.com");
        validDto.setAddress("Short and valid address");
        validDto.setDob(LocalDate.of(1990, 1, 1));
        validDto.setDateOfJoining(LocalDate.of(2020, 1, 1));
        validDto.setGender("Male");

        Set<ConstraintViolation<UpdateMemberRequestDto>> violations = validator.validate(validDto);

        assertTrue(violations.isEmpty());
    }


    @Test
    void updateMember_ShouldFailValidation_WhenInvalidEmailAndFields() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        UpdateMemberRequestDto invalidDto = new UpdateMemberRequestDto();
        invalidDto.setFirstName("ThisIsWayTooLongNameExceedingLimit");
        invalidDto.setLastName("AlsoTooLongForLimit");
        invalidDto.setEmailId("invalid-email");
        invalidDto.setAddress("A".repeat(4000));

        Set<ConstraintViolation<UpdateMemberRequestDto>> violations = validator.validate(invalidDto);

        violations.forEach(v -> System.out.println(v.getPropertyPath() + ": " + v.getMessage()));

        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 2);
    }
    
    @Test
    void updateMember_ShouldThrowException_WhenMemberNotFound() {
        when(memberService.updateMember(eq("mem001"), any(), eq(updateDto))).thenThrow(new RuntimeException("Not Found"));

        assertThrows(RuntimeException.class, () -> memberController.updateMember("mem001", updateDto));
    }

    @Test
    void getAllAdmins_ShouldReturnList() {
        when(memberService.getAllAdmins()).thenReturn(Arrays.asList(new Member(), new Member()));

        ResponseEntity<List<Member>> response = memberController.getAllAdmins();

        assertEquals(2, response.getBody().size());
    }

    @Test
    void getAllAdmins_ShouldReturnEmptyList_WhenNoAdmins() {
        when(memberService.getAllAdmins()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Member>> response = memberController.getAllAdmins();

        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getAllActiveMembers_ShouldReturnList() {
        when(memberService.getAllActiveMembers()).thenReturn(Arrays.asList(new Member()));

        ResponseEntity<List<Member>> response = memberController.getAllActiveMembers();

        assertEquals(1, response.getBody().size());
    }

    @Test
    void getAllActiveMembers_ShouldReturnEmptyList_WhenNoneFound() {
        when(memberService.getAllActiveMembers()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Member>> response = memberController.getAllActiveMembers();

        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getAllTeamMembers_ShouldReturnList() {
        when(memberService.getAllTeamMembers("admin001")).thenReturn(Arrays.asList(new Member()));

        ResponseEntity<List<Member>> response = memberController.getAllTeamMembers("admin001");

        assertEquals(1, response.getBody().size());
    }

    @Test
    void getAllTeamMembers_ShouldThrowException_WhenInvalidAdminId() {
        when(memberService.getAllTeamMembers("invalid")).thenThrow(new RuntimeException("Admin not found"));

        assertThrows(RuntimeException.class, () -> memberController.getAllTeamMembers("invalid"));
    }

    @Test
    void getMember_ShouldReturn200_WhenValid() {
        MemberResponseDto dto = new MemberResponseDto();
        dto.setId("mem001");

        when(memberService.getMember("mem001")).thenReturn(dto);

        ResponseEntity<MemberResponseDto> response = memberController.getMember("mem001");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("mem001", response.getBody().getId());
    }

    @Test
    void getMember_ShouldThrowException_WhenNotFound() {
        when(memberService.getMember("mem001")).thenThrow(new RuntimeException("Not Found"));

        assertThrows(RuntimeException.class, () -> memberController.getMember("mem001"));
    }

    @Test
    void getAllMembersByRoleAndSearch_ShouldReturnList() {
        when(memberService.getAllMembersByRoleAndSearch(any(), eq("john")))
                .thenReturn(Arrays.asList(new MemberResponsePayload()));

        ResponseEntity<List<MemberResponsePayload>> response = memberController.getAllMembersByRoleAndSearch("john");

        assertEquals(1, response.getBody().size());
    }

    @Test
    void getAllMembersByRoleAndSearch_ShouldReturnEmptyList_WhenNoneMatch() {
        when(memberService.getAllMembersByRoleAndSearch(any(), eq("xxx"))).thenReturn(Collections.emptyList());

        ResponseEntity<List<MemberResponsePayload>> response = memberController.getAllMembersByRoleAndSearch("xxx");

        assertTrue(response.getBody().isEmpty());
    }
}
