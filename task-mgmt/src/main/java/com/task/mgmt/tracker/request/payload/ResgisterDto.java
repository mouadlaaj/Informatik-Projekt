package com.task.mgmt.tracker.request.payload;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResgisterDto {
	
	@NotEmpty(message = "memberId is required")
	@JsonProperty(value = "memberId")
	private String memberId;
	
	@NotEmpty(message = "First name is required")
	@Size(max = 15, message = "First name must be at most 15 characters")
	private String firstName;

	@NotEmpty(message = "Last name is required")
	@Size(max = 15, message = "Last name must be at most 15 characters")
	private String lastName;

	@NotEmpty(message = "phoneNumber is required")
//	@Pattern(regexp = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$", message = "phone number must be 10 digits")
	private String phoneNumber;

	@NotEmpty(message = "password is required")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
	         message = "password must contains one capital letter,one small letter,one digites,one special character and 8-20 characters and you can use these special characters only  @ # $ % & ")
	private String password;

	@NotEmpty(message = "emailId is required")
	@Email(message = "Invalid email format")
	private String emailId;

	@NotEmpty(message = "Role is required")
	@Pattern(regexp = "^(MANAGER|EMPLOYEE)$", message = "Role must be one of the following: MANAGER or EMPLOYEE")
	private String role;

	@NotEmpty(message = "designation is required")
	@Size(max = 30, message = "Designation must be given less than 30 characters")
	private String designation;

	@NotEmpty(message = "address is required")
	@Size(max = 3000, message = "Address must be given less than 3000 characters")
	private String address;

	private float yearsOfExperience;

	@NotNull(message = "Date of birth cannot be null")
	@Past(message = "Date of birth must be in the past")
	private LocalDate dob;

	@NotEmpty(message = "gender is required")
	private String gender;

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public float getYearsOfExperience() {
		return yearsOfExperience;
	}

	public void setYearsOfExperience(float yearsOfExperience) {
		this.yearsOfExperience = yearsOfExperience;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	
	
}
