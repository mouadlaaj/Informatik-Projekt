package com.task.mgmt.tracker.response.payload;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.task.mgmt.tracker.entity.Member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MembersResponseDto {
	
	private String id;

	private String firstName;

	private String lastName;

	private String phoneNumber;
	
	private String emailId;
	
	private String password;

	private String role;

	private String designation;

	private String status;
	
	private String address;
	
	private float yearsOfExperience;
	
	private LocalDate dateOfJoining;
	
	private LocalDateTime dateOfRelieving;
	
	private LocalDate dob;
	
	private String gender;

	private LocalDateTime createdDate;

	private Member createdBy;

	private LocalDateTime modifiedDate;
	
	private Member modifiedBy;
	
}
