package com.task.mgmt.tracker.request.payload;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStatusChangeDto {

	@NotEmpty(message = "Status is required")
	@Pattern(regexp = "^(TODO|DESIGN|DEVELOPMENT|TESTING|DONE|BLOCKER)$", message = "Status must be one of the following: TODO, DESIGN, DEVELOPMENT, TESTING, DONE, BLOCKER")
	private String status;

	private String message;

	private String type;

	private String qcFeedback;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getQcFeedback() {
		return qcFeedback;
	}

	public void setQcFeedback(String qcFeedback) {
		this.qcFeedback = qcFeedback;
	}

}
