package com.task.mgmt.tracker.request.payload;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Size;

public class UpdateTaskRequestDto {

	@Size(max = 70, message = "Title must be less than 70 characters")
	private String title;

	@Size(max = 90, message = "Short Description must be less than 90 characters")
	private String shortDescription;

	@Size(max = 10000, message = "Description must be less than 10000 characters")
	private String description;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startDate;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime endDate;

	private String priority;
	
	private String reason;

	private int complexity;

	private List<AssigneeDto> assignedTo;

	private List<String> tags;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

//	public String getProjectId() {
//		return projectId;
//	}
//
//	public void setProjectId(String projectId) {
//		this.projectId = projectId;
//	}

//	public LocalDateTime getStartDate() {
//		return startDate;
//	}
//
//	public void setStartDate(LocalDateTime startDate) {
//		this.startDate = startDate;
//	}
//
//	public LocalDateTime getEndDate() {
//		return endDate;
//	}
//
//	public void setEndDate(LocalDateTime endDate) {
//		this.endDate = endDate;
//	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public int getComplexity() {
		return complexity;
	}

	public void setComplexity(int complexity) {
		this.complexity = complexity;
	}

	public List<AssigneeDto> getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(List<AssigneeDto> assignedTo) {
		this.assignedTo = assignedTo;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}
