package com.task.mgmt.tracker.request.payload;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTeamMemberRequestDto {
	
	@NotEmpty(message = "Team Id is required")
	private String teamId;
	
	@NotEmpty(message = "Team members are required")
	@Size(min = 1, message = "At least one team member must be assigned")
	private List<String> teamMembers;

	public String getTeamId() {
		return teamId;
	}

	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	public List<String> getTeamMembers() {
		return teamMembers;
	}

	public void setTeamMembers(List<String> teamMembers) {
		this.teamMembers = teamMembers;
	} 

}

