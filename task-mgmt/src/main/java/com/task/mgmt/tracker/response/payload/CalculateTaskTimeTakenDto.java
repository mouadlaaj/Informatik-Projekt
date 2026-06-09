package com.task.mgmt.tracker.response.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalculateTaskTimeTakenDto {

	private long totalTaskCount;

	private long inprogressEstimatedTime;

	private long actualInprogressTime;

	private long qcEstimatedTime;

	private long actualQcTime;

	private long inprogressReworkCount;

	private double inprogressEfficiencyPercentage;

	private double qcEfficiencyPercentage;

	private String memberId;

	private String Name;

	private long overAllMinsInOffice;

	private int membersCount;

	private long numberOfDaysWorkingInOffice;

	public long getTotalTaskCount() {
		return totalTaskCount;
	}

	public void setTotalTaskCount(long totalTaskCount) {
		this.totalTaskCount = totalTaskCount;
	}

	public long getInprogressEstimatedTime() {
		return inprogressEstimatedTime;
	}

	public void setInprogressEstimatedTime(long inprogressEstimatedTime) {
		this.inprogressEstimatedTime = inprogressEstimatedTime;
	}

	public long getActualInprogressTime() {
		return actualInprogressTime;
	}

	public void setActualInprogressTime(long actualInprogressTime) {
		this.actualInprogressTime = actualInprogressTime;
	}

	public long getQcEstimatedTime() {
		return qcEstimatedTime;
	}

	public void setQcEstimatedTime(long qcEstimatedTime) {
		this.qcEstimatedTime = qcEstimatedTime;
	}

	public long getActualQcTime() {
		return actualQcTime;
	}

	public void setActualQcTime(long actualQcTime) {
		this.actualQcTime = actualQcTime;
	}

	public long getInprogressReworkCount() {
		return inprogressReworkCount;
	}

	public void setInprogressReworkCount(long inprogressReworkCount) {
		this.inprogressReworkCount = inprogressReworkCount;
	}

	public double getInprogressEfficiencyPercentage() {
		return inprogressEfficiencyPercentage;
	}

	public void setInprogressEfficiencyPercentage(double inprogressEfficiencyPercentage) {
		this.inprogressEfficiencyPercentage = inprogressEfficiencyPercentage;
	}

	public double getQcEfficiencyPercentage() {
		return qcEfficiencyPercentage;
	}

	public void setQcEfficiencyPercentage(double qcEfficiencyPercentage) {
		this.qcEfficiencyPercentage = qcEfficiencyPercentage;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public long getOverAllMinsInOffice() {
		return overAllMinsInOffice;
	}

	public void setOverAllMinsInOffice(long overAllMinsInOffice) {
		this.overAllMinsInOffice = overAllMinsInOffice;
	}

	public int getMembersCount() {
		return membersCount;
	}

	public void setMembersCount(int membersCount) {
		this.membersCount = membersCount;
	}

	public long getNumberOfDaysWorkingInOffice() {
		return numberOfDaysWorkingInOffice;
	}

	public void setNumberOfDaysWorkingInOffice(long numberOfDaysWorkingInOffice) {
		this.numberOfDaysWorkingInOffice = numberOfDaysWorkingInOffice;
	}

}
