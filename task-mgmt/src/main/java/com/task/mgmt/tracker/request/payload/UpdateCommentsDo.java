package com.task.mgmt.tracker.request.payload;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCommentsDo {
	
	@NotEmpty(message = "Task ID is required")
	private String taskId;

	@NotEmpty(message = "Comment ID is required")
	private String commentId;

	@NotEmpty(message = "Message is required")
	private String message;
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getCommentId() {
		return commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
