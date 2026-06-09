package com.task.mgmt.tracker.request.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAttachmentRequestDto {
	
	private String taskId;

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	
}
