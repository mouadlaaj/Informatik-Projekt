package com.task.mgmt.tracker.request.payload;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityRequestDto {

	private String title;

	private String shortDescription;

	private String description;

	private String projectId;

	private String status;

	private String createdBy;

	private LocalDateTime createdDate;

	private String modifiedBy;

	private LocalDateTime modifiedDate;

	private LocalDateTime completedDate;

	private LocalDateTime actualStartDate;

	private LocalDateTime actualCompletedDate;

	private LocalDateTime startDate;

	private LocalDateTime endDate;

	private String priority;

	private List<String> assignedTo;

	private String assignedBy;

	private LocalDateTime assignedDate;

	private List<String> tags;

	private List<String> comments;

	private long commentsCount;

	private String attachments;

	private long attachmentsCount;

	private int position;

	private boolean isActive;

	private int complexity;

	private String reason;

	private String beforePriority;

	private int beforeTaskComplexity;

	private LocalDateTime beforeStartDate;

	private LocalDateTime beforeEndDate;

	private String memberName;

	private String type;

	private String message;

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

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(LocalDateTime modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public LocalDateTime getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(LocalDateTime completedDate) {
		this.completedDate = completedDate;
	}

	public LocalDateTime getActualStartDate() {
		return actualStartDate;
	}

	public void setActualStartDate(LocalDateTime actualStartDate) {
		this.actualStartDate = actualStartDate;
	}

	public LocalDateTime getActualCompletedDate() {
		return actualCompletedDate;
	}

	public void setActualCompletedDate(LocalDateTime actualCompletedDate) {
		this.actualCompletedDate = actualCompletedDate;
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

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}


	public List<String> getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(List<String> assignedTo) {
		this.assignedTo = assignedTo;
	}

	public String getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(String assignedBy) {
		this.assignedBy = assignedBy;
	}

	public LocalDateTime getAssignedDate() {
		return assignedDate;
	}

	public void setAssignedDate(LocalDateTime assignedDate) {
		this.assignedDate = assignedDate;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<String> getComments() {
		return comments;
	}

	public void setComments(List<String> comments) {
		this.comments = comments;
	}

	public long getCommentsCount() {
		return commentsCount;
	}

	public void setCommentsCount(long commentsCount) {
		this.commentsCount = commentsCount;
	}

	public String getAttachments() {
		return attachments;
	}

	public void setAttachments(String attachments) {
		this.attachments = attachments;
	}

	public long getAttachmentsCount() {
		return attachmentsCount;
	}

	public void setAttachmentsCount(long attachmentsCount) {
		this.attachmentsCount = attachmentsCount;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public int getComplexity() {
		return complexity;
	}

	public void setComplexity(int complexity) {
		this.complexity = complexity;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getBeforePriority() {
		return beforePriority;
	}

	public void setBeforePriority(String beforePriority) {
		this.beforePriority = beforePriority;
	}

	public int getBeforeTaskComplexity() {
		return beforeTaskComplexity;
	}

	public void setBeforeTaskComplexity(int beforeTaskComplexity) {
		this.beforeTaskComplexity = beforeTaskComplexity;
	}

	public LocalDateTime getBeforeStartDate() {
		return beforeStartDate;
	}

	public void setBeforeStartDate(LocalDateTime beforeStartDate) {
		this.beforeStartDate = beforeStartDate;
	}

	public LocalDateTime getBeforeEndDate() {
		return beforeEndDate;
	}

	public void setBeforeEndDate(LocalDateTime beforeEndDate) {
		this.beforeEndDate = beforeEndDate;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
