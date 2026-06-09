package com.task.mgmt.tracker.response.payload;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonBugResponseDto {
	private Long id;
	private String title;
	private String description;
	private String severity;
	private String status;
	private LocalDateTime reportedAt;
	private CommonMemberResponseDto createdBy;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getReportedAt() {
		return reportedAt;
	}

	public void setReportedAt(LocalDateTime reportedAt) {
		this.reportedAt = reportedAt;
	}

	public CommonMemberResponseDto getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(CommonMemberResponseDto createdBy) {
		this.createdBy = createdBy;
	}

}
