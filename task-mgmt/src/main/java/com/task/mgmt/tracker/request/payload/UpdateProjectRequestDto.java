package com.task.mgmt.tracker.request.payload;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProjectRequestDto {

	@Size(max = 30, message = "Project name must be given less than 30 characters")
	private String projectName;

	@Size(max = 500, message = "Description must be given less than 500 characters")
	private String description;

	@NotEmpty(message = "Team is required")
	@Size(min = 1, message = "At least one team details must be assigned")
	private List<String> teams;

	private List<String> tags;

	private List<String> attachments;

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

	public List<String> getTeams() {
		return teams;
	}

	public void setTeams(List<String> teams) {
		this.teams = teams;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<String> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<String> attachments) {
		this.attachments = attachments;
	};

}
