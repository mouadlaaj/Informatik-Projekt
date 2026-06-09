package com.task.mgmt.tracker.response.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamTaskTimeTakenResponseDto {

	private String teamName;

	private CalculateTaskTimeTakenDto taskTimeTaken;

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public CalculateTaskTimeTakenDto getTaskTimeTaken() {
		return taskTimeTaken;
	}

	public void setTaskTimeTaken(CalculateTaskTimeTakenDto taskTimeTaken) {
		this.taskTimeTaken = taskTimeTaken;
	}
	
	
}
