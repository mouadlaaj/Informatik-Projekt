package com.task.mgmt.tracker.response.payload;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.task.mgmt.tracker.entity.Tag;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetailedTaskResponseDto {

	public List<CommonAssigneeResponseDto> getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(List<CommonAssigneeResponseDto> assignedTo) {
		this.assignedTo = assignedTo;
	}

	private String id;

	private String title;

	private String shortDescription;

	private String description;

	private CommonProjectResponseDto projectId;

	private String status;

	private int complexity;

	private CommonMemberResponseDto createdBy;

	private LocalDateTime createdDate;

	private CommonMemberResponseDto modifiedBy;

	private LocalDateTime modifiedDate;

	private LocalDateTime completedDate;

	private LocalDateTime startDate;

	private LocalDateTime endDate;

	private String priority;

	private CommonMemberResponseDto assignedBy;

	private LocalDateTime assignedDate;

	private Set<Tag> tags;

	private List<CommonCommentResponseDto> comments;

	private long commentsCount;

	private List<CommonAttachmentResponseDto> attachments;

	private long attachmentsCount;

	private List<CommonAssigneeResponseDto> assignedTo;

	private LocalDateTime actualStartDate;

	private LocalDateTime actualCompletedDate;

	private List<CommonActivityResponseDto> activity;

	private List<CommonBugResponseDto> bugs;

	private List<CommonTaskStatusTrackResponseDto> taskStatusTrack;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public CommonProjectResponseDto getProjectId() {
		return projectId;
	}

	public void setProjectId(CommonProjectResponseDto projectId) {
		this.projectId = projectId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getComplexity() {
		return complexity;
	}

	public void setComplexity(int complexity) {
		this.complexity = complexity;
	}

	public CommonMemberResponseDto getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(CommonMemberResponseDto createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public CommonMemberResponseDto getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(CommonMemberResponseDto modifiedBy) {
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

	public CommonMemberResponseDto getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(CommonMemberResponseDto assignedBy) {
		this.assignedBy = assignedBy;
	}

	public LocalDateTime getAssignedDate() {
		return assignedDate;
	}

	public void setAssignedDate(LocalDateTime assignedDate) {
		this.assignedDate = assignedDate;
	}

	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	public List<CommonCommentResponseDto> getComments() {
		return comments;
	}

	public void setComments(List<CommonCommentResponseDto> comments) {
		this.comments = comments;
	}

	public long getCommentsCount() {
		return commentsCount;
	}

	public void setCommentsCount(long commentsCount) {
		this.commentsCount = commentsCount;
	}

	public List<CommonAttachmentResponseDto> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<CommonAttachmentResponseDto> attachments) {
		this.attachments = attachments;
	}

	public long getAttachmentsCount() {
		return attachmentsCount;
	}

	public void setAttachmentsCount(long attachmentsCount) {
		this.attachmentsCount = attachmentsCount;
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

	public List<CommonActivityResponseDto> getActivity() {
		return activity;
	}

	public void setActivity(List<CommonActivityResponseDto> activity) {
		this.activity = activity;
	}

	public List<CommonTaskStatusTrackResponseDto> getTaskStatusTrack() {
		return taskStatusTrack;
	}

	public void setTaskStatusTrack(List<CommonTaskStatusTrackResponseDto> taskStatusTrack) {
		this.taskStatusTrack = taskStatusTrack;
	}

	public List<CommonBugResponseDto> getBugs() {
		return bugs;
	}

	public void setBugs(List<CommonBugResponseDto> bugs) {
		this.bugs = bugs;
	}

}
