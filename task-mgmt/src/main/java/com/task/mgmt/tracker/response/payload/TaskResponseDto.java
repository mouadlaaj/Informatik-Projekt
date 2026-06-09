package com.task.mgmt.tracker.response.payload;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.task.mgmt.tracker.entity.Tag;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskResponseDto {

	private String id;

	private String title;

	private String shortDescription;

	private ProjectResponseDto projectId;

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

	private List<CommonAssigneeResponseDto> assignedTo;

	private CommonMemberResponseDto assignedBy;

	private LocalDateTime assignedDate;

	private Set<Tag> tags;

	private long commentsCount;

	private long attachmentsCount;

	private LocalDateTime actualStartDate;

	private LocalDateTime actualCompletedDate;
	
	private long bugsCount;

//	private int position;

//	private Task parentTask;

//	private List<CommonActivityResponseDto> activity;

//	private List<TaskRating> ratings;

	private long ratingsCount;

	private LocalDateTime qcActualStartDate;

	private LocalDateTime qcActualCompletedDate;

	private LocalDateTime qcStartDate;

	private LocalDateTime qcEndDate;

	private CommonMemberResponseDto qcAssignedTo;

	private CommonMemberResponseDto qcAssignedBy;

	private String taskStatusTrack;

	private double assignedToRating;

	private double qcAssignedToRating;

	private String assignedToRatingFeedBack;

	private String qcAssignedToRatingFeedBack;

	private String qcPersonFeedBack;

	private long inProgressEstimatedTime;
	
	private long actualInProgressTime;
	
	private long qcEstimatedTime;
	
	private long actualQcTime;
	
	private long reworkCount;

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

	public ProjectResponseDto getProjectId() {
		return projectId;
	}

	public void setProjectId(ProjectResponseDto projectId) {
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


	public List<CommonAssigneeResponseDto> getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(List<CommonAssigneeResponseDto> assignedTo) {
		this.assignedTo = assignedTo;
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

	public long getCommentsCount() {
		return commentsCount;
	}

	public void setCommentsCount(long commentsCount) {
		this.commentsCount = commentsCount;
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

	public long getRatingsCount() {
		return ratingsCount;
	}

	public void setRatingsCount(long ratingsCount) {
		this.ratingsCount = ratingsCount;
	}

	public LocalDateTime getQcActualStartDate() {
		return qcActualStartDate;
	}

	public void setQcActualStartDate(LocalDateTime qcActualStartDate) {
		this.qcActualStartDate = qcActualStartDate;
	}

	public LocalDateTime getQcActualCompletedDate() {
		return qcActualCompletedDate;
	}

	public void setQcActualCompletedDate(LocalDateTime qcActualCompletedDate) {
		this.qcActualCompletedDate = qcActualCompletedDate;
	}

	public LocalDateTime getQcStartDate() {
		return qcStartDate;
	}

	public void setQcStartDate(LocalDateTime qcStartDate) {
		this.qcStartDate = qcStartDate;
	}

	public LocalDateTime getQcEndDate() {
		return qcEndDate;
	}

	public void setQcEndDate(LocalDateTime qcEndDate) {
		this.qcEndDate = qcEndDate;
	}

	public CommonMemberResponseDto getQcAssignedTo() {
		return qcAssignedTo;
	}

	public void setQcAssignedTo(CommonMemberResponseDto qcAssignedTo) {
		this.qcAssignedTo = qcAssignedTo;
	}

	public CommonMemberResponseDto getQcAssignedBy() {
		return qcAssignedBy;
	}

	public void setQcAssignedBy(CommonMemberResponseDto qcAssignedBy) {
		this.qcAssignedBy = qcAssignedBy;
	}

	public String getTaskStatusTrack() {
		return taskStatusTrack;
	}

	public void setTaskStatusTrack(String taskStatusTrack) {
		this.taskStatusTrack = taskStatusTrack;
	}

	public double getAssignedToRating() {
		return assignedToRating;
	}

	public void setAssignedToRating(double assignedToRating) {
		this.assignedToRating = assignedToRating;
	}

	public double getQcAssignedToRating() {
		return qcAssignedToRating;
	}

	public void setQcAssignedToRating(double qcAssignedToRating) {
		this.qcAssignedToRating = qcAssignedToRating;
	}

	public String getAssignedToRatingFeedBack() {
		return assignedToRatingFeedBack;
	}

	public void setAssignedToRatingFeedBack(String assignedToRatingFeedBack) {
		this.assignedToRatingFeedBack = assignedToRatingFeedBack;
	}

	public String getQcAssignedToRatingFeedBack() {
		return qcAssignedToRatingFeedBack;
	}

	public void setQcAssignedToRatingFeedBack(String qcAssignedToRatingFeedBack) {
		this.qcAssignedToRatingFeedBack = qcAssignedToRatingFeedBack;
	}

	public String getQcPersonFeedBack() {
		return qcPersonFeedBack;
	}

	public void setQcPersonFeedBack(String qcPersonFeedBack) {
		this.qcPersonFeedBack = qcPersonFeedBack;
	}

	public long getInProgressEstimatedTime() {
		return inProgressEstimatedTime;
	}

	public void setInProgressEstimatedTime(long inProgressEstimatedTime) {
		this.inProgressEstimatedTime = inProgressEstimatedTime;
	}

	public long getActualInProgressTime() {
		return actualInProgressTime;
	}

	public void setActualInProgressTime(long actualInProgressTime) {
		this.actualInProgressTime = actualInProgressTime;
	}

	public long getQcEstimatedTime() {
		return qcEstimatedTime;
	}

	public void setQcEstimatedTime(long qcEstimatedTime) {
		this.qcEstimatedTime = qcEstimatedTime;
	}

	public long getActualQcTime() {
		return actualQcTime;
	}

	public void setActualQcTime(long actualQcTime) {
		this.actualQcTime = actualQcTime;
	}

	public long getReworkCount() {
		return reworkCount;
	}

	public void setReworkCount(long reworkCount) {
		this.reworkCount = reworkCount;
	}

	public long getBugsCount() {
		return bugsCount;
	}

	public void setBugsCount(long bugsCount) {
		this.bugsCount = bugsCount;
	}
	
	
}

