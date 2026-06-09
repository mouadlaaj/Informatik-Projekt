package com.task.mgmt.tracker.response.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectResponseDto {

	private String id;

	private String projectName;

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

//	private String description;
//
//	private String status;
//
//	private LocalDateTime createdDate;
//
//	private CommonMemberResponseDto createdBy;
//
//	private LocalDateTime modifiedDate;
//
//	private CommonMemberResponseDto modifiedBy;
//
//	private List<CommonTeamResponseDto> teams = new ArrayList<>();
//
//	private List<Tag> tags = new ArrayList<>();
//
//	private List<CommonAttachmentResponseDto> attachments = new ArrayList<>();

}
