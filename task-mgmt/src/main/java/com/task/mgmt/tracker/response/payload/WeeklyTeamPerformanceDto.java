package com.task.mgmt.tracker.response.payload;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WeeklyTeamPerformanceDto {
	private String teamName;
	private LocalDate weekStart;
	private LocalDate weekEnd;
	private int totalEstimatedTime;
	private int totalActualTime;
	private double efficiency;
	private List<DailyPerformanceDto> dailyStats = new ArrayList<>();

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public LocalDate getWeekStart() {
		return weekStart;
	}

	public void setWeekStart(LocalDate weekStart) {
		this.weekStart = weekStart;
	}

	public LocalDate getWeekEnd() {
		return weekEnd;
	}

	public void setWeekEnd(LocalDate weekEnd) {
		this.weekEnd = weekEnd;
	}

	public int getTotalEstimatedTime() {
		return totalEstimatedTime;
	}

	public void setTotalEstimatedTime(int totalEstimatedTime) {
		this.totalEstimatedTime = totalEstimatedTime;
	}

	public int getTotalActualTime() {
		return totalActualTime;
	}

	public void setTotalActualTime(int totalActualTime) {
		this.totalActualTime = totalActualTime;
	}

	public double getEfficiency() {
		return efficiency;
	}

	public void setEfficiency(double efficiency) {
		this.efficiency = efficiency;
	}

	public List<DailyPerformanceDto> getDailyStats() {
		return dailyStats;
	}

	public void setDailyStats(List<DailyPerformanceDto> dailyStats) {
		this.dailyStats = dailyStats;
	}

	public static class DailyPerformanceDto {
		private LocalDate date;
		private int estimatedTime;
		private int actualTime;



		public DailyPerformanceDto(LocalDate date, int estimatedTime, int actualTime) {
			super();
			this.date = date;
			this.estimatedTime = estimatedTime;
			this.actualTime = actualTime;
		}

		public LocalDate getDate() {
			return date;
		}

		public void setDate(LocalDate date) {
			this.date = date;
		}

		public int getEstimatedTime() {
			return estimatedTime;
		}

		public void setEstimatedTime(int estimatedTime) {
			this.estimatedTime = estimatedTime;
		}

		public int getActualTime() {
			return actualTime;
		}

		public void setActualTime(int actualTime) {
			this.actualTime = actualTime;
		}
	}

}
