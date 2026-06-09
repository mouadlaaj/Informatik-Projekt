package com.task.mgmt.tracker.response.payload;

public class TaskCountDetailsResponseDto {

	private long completedTask;

	private long pendingTask;

	private long completedTaskPercentage;

	private long pendingTaskPercentage;

	public long getCompletedTask() {
		return completedTask;
	}

	public void setCompletedTask(long completedTask) {
		this.completedTask = completedTask;
	}

	public long getPendingTask() {
		return pendingTask;
	}

	public void setPendingTask(long pendingTask) {
		this.pendingTask = pendingTask;
	}

	public long getCompletedTaskPercentage() {
		return completedTaskPercentage;
	}

	public void setCompletedTaskPercentage(long completedTaskPercentage) {
		this.completedTaskPercentage = completedTaskPercentage;
	}

	public long getPendingTaskPercentage() {
		return pendingTaskPercentage;
	}

	public void setPendingTaskPercentage(long pendingTaskPercentage) {
		this.pendingTaskPercentage = pendingTaskPercentage;
	}

}
