package com.task.mgmt.tracker.response.payload;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.task.mgmt.tracker.entity.Member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberResponsePayload {
	
	private String id;

	private String firstName;

	private String lastName;

	private String phoneNumber;
	
	private String emailId;
	
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
	
	private CommonMemberResponseDto teamLead;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public LocalDate getDateOfJoining() {
		return dateOfJoining;
	}

	public void setDateOfJoining(LocalDate dateOfJoining) {
		this.dateOfJoining = dateOfJoining;
	}

	public LocalDateTime getDateOfRelieving() {
		return dateOfRelieving;
	}

	public void setDateOfRelieving(LocalDateTime dateOfRelieving) {
		this.dateOfRelieving = dateOfRelieving;
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

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public Member getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Member createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(LocalDateTime modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Member getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Member modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public CommonMemberResponseDto getTeamLead() {
		return teamLead;
	}

	public void setTeamLead(CommonMemberResponseDto teamLead) {
		this.teamLead = teamLead;
	}
	
}
