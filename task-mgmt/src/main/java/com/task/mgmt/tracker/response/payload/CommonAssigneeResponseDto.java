package com.task.mgmt.tracker.response.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonAssigneeResponseDto {

	private CommonMemberResponseDto member;

	private String designation;

	private Integer estimationTime;

	public CommonMemberResponseDto getMember() {
		return member;
	}

	public void setMember(CommonMemberResponseDto member) {
		this.member = member;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public Integer getEstimationTime() {
		return estimationTime;
	}

	public void setEstimationTime(Integer estimationTime) {
		this.estimationTime = estimationTime;
	}

}
