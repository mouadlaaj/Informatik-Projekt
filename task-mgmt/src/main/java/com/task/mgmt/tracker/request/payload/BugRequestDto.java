package com.task.mgmt.tracker.request.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BugRequestDto {

	@NotBlank(message = "Task ID is required")
	private String taskId;

	@NotEmpty(message = "Bug list cannot be empty")
	private List<BugItemDto> bugs;

	@Getter
	@Setter
	public static class BugItemDto {

		private Long id;

		@NotBlank(message = "Bug title is required")
		private String title;

		@NotBlank(message = "Bug description is required")
		private String description;

		@NotBlank(message = "Severity is required")
		private String severity;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getSeverity() {
			return severity;
		}

		public void setSeverity(String severity) {
			this.severity = severity;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}


	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public List<BugItemDto> getBugs() {
		return bugs;
	}

	public void setBugs(List<BugItemDto> bugs) {
		this.bugs = bugs;
	}

}
