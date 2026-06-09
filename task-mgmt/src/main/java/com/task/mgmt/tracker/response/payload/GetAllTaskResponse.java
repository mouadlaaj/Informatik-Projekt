package com.task.mgmt.tracker.response.payload;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.task.mgmt.tracker.entity.Comments;
import com.task.mgmt.tracker.entity.Member;
import com.task.mgmt.tracker.entity.Project;
import com.task.mgmt.tracker.entity.Team;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetAllTaskResponse {
	
	private String id;

	private String title;

	private String shortDescription;

	private String description;

	private Project projectId;

	private String status;

	private Member createdBy;

	private LocalDateTime createdDate;

	private Member modifiedBy;

	private LocalDateTime modifiedDate;
	
	private LocalDateTime completedDate;

	private LocalDateTime startDate;

	private LocalDateTime endDate;

	private String priority;

	private List<Member> assignedTo;

	private Member assignedBy;

	private LocalDateTime assignedDate;

	private List<Team> assignedTeams;

//	private List<Task> subTasks;
	
	private List<Comments> comments;
}
