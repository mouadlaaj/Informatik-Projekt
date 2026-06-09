package com.task.mgmt.tracker.response.payload;

import java.time.LocalDateTime;

public class TaskReportResponseDto {

	private String teamId;
	private String teamName;
	private long teamOrder;

	private TaskMemberWorkInfo workInfo;

	public String getTeamId() {
		return teamId;
	}

	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	public String getTeamName() {
		return teamName;
	}

	public long getTeamOrder() {
		return teamOrder;
	}

	public void setTeamOrder(long teamOrder) {
		this.teamOrder = teamOrder;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public TaskMemberWorkInfo getWorkInfo() {
		return workInfo;
	}

	public void setWorkInfo(TaskMemberWorkInfo workInfo) {
		this.workInfo = workInfo;
	}

	public static class TaskMemberWorkInfo {
		private LocalDateTime date;
		private String memberId;
		private String memberName;
		private String designation;
		private String taskId;
		private String taskTitle;
		private String estimatedTime;
		private String actualSpentTime;
		private String percentage;

		public LocalDateTime getDate() {
			return date;
		}

		public void setDate(LocalDateTime date) {
			this.date = date;
		}

		public String getMemberId() {
			return memberId;
		}

		public void setMemberId(String memberId) {
			this.memberId = memberId;
		}

		public String getMemberName() {
			return memberName;
		}

		public void setMemberName(String memberName) {
			this.memberName = memberName;
		}

		public String getDesignation() {
			return designation;
		}

		public void setDesignation(String designation) {
			this.designation = designation;
		}

		public String getTaskId() {
			return taskId;
		}

		public void setTaskId(String taskId) {
			this.taskId = taskId;
		}

		public String getTaskTitle() {
			return taskTitle;
		}

		public void setTaskTitle(String taskTitle) {
			this.taskTitle = taskTitle;
		}

		public String getEstimatedTime() {
			return estimatedTime;
		}

		public void setEstimatedTime(String estimatedTime) {
			this.estimatedTime = estimatedTime;
		}

		public String getActualSpentTime() {
			return actualSpentTime;
		}

		public void setActualSpentTime(String actualSpentTime) {
			this.actualSpentTime = actualSpentTime;
		}

		public String getPercentage() {
			return percentage;
		}

		public void setPercentage(String percentage) {
			this.percentage = percentage;
		}
	}
}
