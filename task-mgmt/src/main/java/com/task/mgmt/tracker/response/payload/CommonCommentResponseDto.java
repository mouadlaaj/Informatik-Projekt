package com.task.mgmt.tracker.response.payload;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonCommentResponseDto {
	
	private String id;
	
	private String message;
	
	private LocalDateTime createdDate;

	private CommonMemberResponseDto createdBy;

	private LocalDateTime modifiedDate;

	private CommonMemberResponseDto modifiedBy;

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

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(LocalDateTime modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public CommonMemberResponseDto getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(CommonMemberResponseDto modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	
	
}
