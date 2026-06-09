package com.task.mgmt.tracker.request.payload;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkStatusUpdateDto {
	private String taskId;

	@NotNull(message = "Start time is required")
	@FutureOrPresent(message = "Start time must be in the present or future")
	private LocalDateTime startTime;

	@NotNull(message = "End time is required")
	@Future(message = "End time must be in the future")
	private LocalDateTime endTime;

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

}