package com.task.mgmt.tracker.response.payload;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamWiseTaskTimeTakenResponseDto {

	private String teamName;

	private List<CalculateTaskTimeTakenDto> taskTimeTaken;

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public List<CalculateTaskTimeTakenDto> getTaskTimeTaken() {
		return taskTimeTaken;
	}

	public void setTaskTimeTaken(List<CalculateTaskTimeTakenDto> taskTimeTaken) {
		this.taskTimeTaken = taskTimeTaken;
	}
	
	
}
