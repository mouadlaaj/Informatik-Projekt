package com.task.mgmt.tracker.response.payload;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonAttachmentResponseDto {
	
	private String id;
	
	private String url;
	
	private LocalDateTime uploadedDate;
	
	private CommonMemberResponseDto uploadedBy;
	
	private String status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public LocalDateTime getUploadedDate() {
		return uploadedDate;
	}

	public void setUploadedDate(LocalDateTime uploadedDate) {
		this.uploadedDate = uploadedDate;
	}

	public CommonMemberResponseDto getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(CommonMemberResponseDto uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
}
