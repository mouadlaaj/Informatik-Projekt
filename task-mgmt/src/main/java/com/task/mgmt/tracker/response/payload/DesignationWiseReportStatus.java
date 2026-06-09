package com.task.mgmt.tracker.response.payload;

import java.util.List;

public class DesignationWiseReportStatus {
	private Designer designer;
	private Developer developer;
	private Tester tester;

	public Designer getDesigner() {
		return designer;
	}

	public void setDesigner(Designer designer) {
		this.designer = designer;
	}

	public Developer getDeveloper() {
		return developer;
	}

	public void setDeveloper(Developer developer) {
		this.developer = developer;
	}

	public Tester getTester() {
		return tester;
	}

	public void setTester(Tester tester) {
		this.tester = tester;
	}

	private List<MemberEfficiencyDto> members;

	public List<MemberEfficiencyDto> getMembers() {
		return members;
	}

	public void setMembers(List<MemberEfficiencyDto> members) {
		this.members = members;
	}

	public static class MemberEfficiencyDto {
		private String memberId;
		private double actualTime;
		private double estimationTime;
		private String efficiency;

		public String getMemberId() {
			return memberId;
		}

		public void setMemberId(String memberId) {
			this.memberId = memberId;
		}

		public double getActualTime() {
			return actualTime;
		}

		public void setActualTime(double actualTime) {
			this.actualTime = actualTime;
		}

		public double getEstimationTime() {
			return estimationTime;
		}

		public void setEstimationTime(double estimationTime) {
			this.estimationTime = estimationTime;
		}

		public String getEfficiency() {
			return efficiency;
		}

		public void setEfficiency(String efficiency) {
			this.efficiency = efficiency;
		}

	}

	public static class Designer {
		private long totalCount;

		public long getTotalCount() {
			return totalCount;
		}

		public void setTotalCount(long totalCount) {
			this.totalCount = totalCount;
		}
	}

	public static class Developer {
		private long totalCount;

		public long getTotalCount() {
			return totalCount;
		}

		public void setTotalCount(long totalCount) {
			this.totalCount = totalCount;
		}
	}

	public static class Tester {
		private long totalCount;

		public long getTotalCount() {
			return totalCount;
		}

		public void setTotalCount(long totalCount) {
			this.totalCount = totalCount;
		}
	}

}
