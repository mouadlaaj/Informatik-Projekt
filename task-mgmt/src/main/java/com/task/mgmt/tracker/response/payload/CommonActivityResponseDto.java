package com.task.mgmt.tracker.response.payload;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonActivityResponseDto {
	
    private String id;
	
	private String message;
	
	private String detailedMessage;
	
	private LocalDateTime createdDate;
	
	private CommonMemberResponseDto createdBy;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDetailedMessage() {
		return detailedMessage;
	}

	public void setDetailedMessage(String detailedMessage) {
		this.detailedMessage = detailedMessage;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public CommonMemberResponseDto getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(CommonMemberResponseDto createdBy) {
		this.createdBy = createdBy;
	}
	
	
}


