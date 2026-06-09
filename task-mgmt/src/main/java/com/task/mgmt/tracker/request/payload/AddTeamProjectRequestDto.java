package com.task.mgmt.tracker.request.payload;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AddTeamProjectRequestDto {

	
	@NotEmpty(message = "Teams are required")
	@Size(min = 1, message = "At least one team details must be assigned")
	private List<String> teams;
	
	@NotEmpty(message = "project id are required")
	private String projectId;

	public List<String> getTeams() {
		return teams;
	}

	public void setTeams(List<String> teams) {
		this.teams = teams;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	
	
}
