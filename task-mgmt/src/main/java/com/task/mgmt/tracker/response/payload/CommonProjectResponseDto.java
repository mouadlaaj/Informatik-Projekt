package com.task.mgmt.tracker.response.payload;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.task.mgmt.tracker.entity.Tag;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonProjectResponseDto {
	
	private String id;
	
	private String projectName;
	
	private String description;
	
	private String status;
	
	private LocalDateTime createdDate;

	private CommonMemberResponseDto createdBy;

	private LocalDateTime modifiedDate;

	private CommonMemberResponseDto modifiedBy;
	
    private List<CommonTeamResponseDto> teams = new ArrayList<>();
	
	private List<Tag> tags = new ArrayList<>();
	
	private List<CommonAttachmentResponseDto> attachments = new ArrayList<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public List<CommonTeamResponseDto> getTeams() {
		return teams;
	}

	public void setTeams(List<CommonTeamResponseDto> teams) {
		this.teams = teams;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public List<CommonAttachmentResponseDto> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<CommonAttachmentResponseDto> attachments) {
		this.attachments = attachments;
	}
	
}
