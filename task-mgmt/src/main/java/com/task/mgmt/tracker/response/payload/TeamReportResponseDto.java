package com.task.mgmt.tracker.response.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamReportResponseDto {

	private String teamName;

	private CommonMemberResponseDto teamLead;

	private ReportResponseDto reportResponseDto;

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public CommonMemberResponseDto getTeamLead() {
		return teamLead;
	}

	public void setTeamLead(CommonMemberResponseDto teamLead) {
		this.teamLead = teamLead;
	}

	public ReportResponseDto getReportResponseDto() {
		return reportResponseDto;
	}

	public void setReportResponseDto(ReportResponseDto reportResponseDto) {
		this.reportResponseDto = reportResponseDto;
	}

}
