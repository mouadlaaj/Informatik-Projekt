package com.task.mgmt.tracker.request.payload;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamTaskReportDto {
	private String teamName;
	private List<MemberTaskReportDto> members;
	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	public List<MemberTaskReportDto> getMembers() {
		return members;
	}
	public void setMembers(List<MemberTaskReportDto> members) {
		this.members = members;
	}
	
	
}
