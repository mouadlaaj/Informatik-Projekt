package com.task.mgmt.tracker.response.payload;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonTaskStatusTrackResponseDto {
	
    private String id;
	
	private CommonMemberResponseDto member;
	
	private String changedStatus;
	
	private LocalDateTime changedTime;
	
	private String message;
	
	private String type;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public CommonMemberResponseDto getMember() {
		return member;
	}

	public void setMember(CommonMemberResponseDto member) {
		this.member = member;
	}

	public String getChangedStatus() {
		return changedStatus;
	}

	public void setChangedStatus(String changedStatus) {
		this.changedStatus = changedStatus;
	}

	public LocalDateTime getChangedTime() {
		return changedTime;
	}

	public void setChangedTime(LocalDateTime changedTime) {
		this.changedTime = changedTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
