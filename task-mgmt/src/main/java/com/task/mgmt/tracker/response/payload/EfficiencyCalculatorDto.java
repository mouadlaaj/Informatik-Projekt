package com.task.mgmt.tracker.response.payload;

import lombok.Data;

@Data
public class EfficiencyCalculatorDto {
	
	private String totalTasks;
	
	private String totalEstimatedTime;
	
	private String totalTimeTaken;
	
	private String timeDifference;
	
	private long reworkCount;
	
	private double efficiencyPercentage;

}
