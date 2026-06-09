package com.task.mgmt.tracker.request.payload;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentsRequestDto {

    @NotEmpty(message = "Task ID is required")
    private String taskId;

    @NotEmpty(message = "Message is required")
    private String message;

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
