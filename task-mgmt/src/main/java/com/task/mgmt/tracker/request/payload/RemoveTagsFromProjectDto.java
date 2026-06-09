package com.task.mgmt.tracker.request.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemoveTagsFromProjectDto {
	@NotBlank(message = "Project ID is required")
	private String projectId;

	@NotBlank(message = "Tag ID is required")
	private String tagId;

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

}
