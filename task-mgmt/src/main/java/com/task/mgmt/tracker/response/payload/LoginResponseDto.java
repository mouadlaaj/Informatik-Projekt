package com.task.mgmt.tracker.response.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {

	private String token;
	private String id;
	private String email;
	private String firstName;
	private String lastName;
	private String gender;
	private String role;
	private String designation;
	private boolean isTeamLead;

	public LoginResponseDto(String token, String id, String email, String firstName, String lastName, String gender,
			String role) {
		super();
		this.token = token;
		this.id = id;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.role = role;
	}

	public LoginResponseDto() {
		super();
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isTeamLead() {
		return isTeamLead;
	}

	public void setTeamLead(boolean isTeamLead) {
		this.isTeamLead = isTeamLead;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

}
