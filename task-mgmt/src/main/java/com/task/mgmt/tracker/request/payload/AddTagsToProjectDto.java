package com.task.mgmt.tracker.request.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddTagsToProjectDto {
	
	@NotBlank(message = "Project ID is required")
    private String projectId;

    @NotBlank(message = "Tag name is required")
    private String tagName;

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
}
