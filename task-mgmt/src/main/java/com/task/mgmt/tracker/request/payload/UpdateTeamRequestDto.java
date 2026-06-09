package com.task.mgmt.tracker.request.payload;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTeamRequestDto {

	@NotEmpty(message = "Team lead ID is required")
	private String teamLeadId;

	@NotEmpty(message = "Team name is required")
	@Size(max = 30, message = "Team name must be given less than 30 characters")
	private String teamName;

	@NotEmpty(message = "Team members are required")
	@Size(min = 1, message = "At least one team member must be assigned")
	private List<String> teamMembers;

	public String getTeamLeadId() {
		return teamLeadId;
	}

	public void setTeamLeadId(String teamLeadId) {
		this.teamLeadId = teamLeadId;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public List<String> getTeamMembers() {
		return teamMembers;
	}

	public void setTeamMembers(List<String> teamMembers) {
		this.teamMembers = teamMembers;
	}

}
