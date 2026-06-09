package com.task.mgmt.tracker.response.payload;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskReportDto {

    @Getter
    @Setter
    public static class TeamTaskReportDto {
        private String teamName;
        private List<MemberReportDto> members;
		public String getTeamName() {
			return teamName;
		}
		public void setTeamName(String teamName) {
			this.teamName = teamName;
		}
		public List<MemberReportDto> getMembers() {
			return members;
		}
		public void setMembers(List<MemberReportDto> members) {
			this.members = members;
		}
        
    }

    @Getter
    @Setter
    public static class MemberReportDto {
        private CommonMemberResponseDto member;
        private Map<YearMonth, MonthlyTaskReportDto> monthlyData;
		public CommonMemberResponseDto getMember() {
			return member;
		}
		public void setMember(CommonMemberResponseDto member) {
			this.member = member;
		}
		public Map<YearMonth, MonthlyTaskReportDto> getMonthlyData() {
			return monthlyData;
		}
		public void setMonthlyData(Map<YearMonth, MonthlyTaskReportDto> monthlyData) {
			this.monthlyData = monthlyData;
		}
        
        
    }

    @Getter
    @Setter
    public static class MonthlyTaskReportDto {
        private int totalAssignedTasks;
        private long totalCompletedTasks;
        private float completionPercentage;
		public int getTotalAssignedTasks() {
			return totalAssignedTasks;
		}
		public void setTotalAssignedTasks(int totalAssignedTasks) {
			this.totalAssignedTasks = totalAssignedTasks;
		}
		public long getTotalCompletedTasks() {
			return totalCompletedTasks;
		}
		public void setTotalCompletedTasks(long totalCompletedTasks) {
			this.totalCompletedTasks = totalCompletedTasks;
		}
		public float getCompletionPercentage() {
			return completionPercentage;
		}
		public void setCompletionPercentage(float completionPercentage) {
			this.completionPercentage = completionPercentage;
		}
        
        
    }
}
