package com.task.mgmt.tracker.entity;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class TaskReport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "task_id")
	private String taskId;

	@Column(name = "inprogress_estimated_time")
	private long inprogressEstimatedTime;

	@Column(name = "actual_inprogress_time")
	private long actualInprogressTime;

	@Column(name = "qc_estimated_time")
	private long qcEstimatedTime;

	@Column(name = "actual_qc_time")
	private long actualQcTime;

	@Column(name = "inprogress_rework_count")
	private long inprogressReworkCount;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Column(name = "changed_time")
	private LocalDateTime changedTime;

	@Column(name = "project_id")
	private String projectId;

	@Column(name = "in_progress_break_start_time")
	private LocalDateTime inProgressBreakStartTime;

//	@Column(name = "in_progress_break_end_time")
//	private String inProgressBreakEndTime;

	@Column(name = "in_progress_break_time")
	private long inProgressBreakTime;

	@Column(name = "qc_break_start_time")
	private LocalDateTime qcBreakStartTime;

//	@Column(name = "qc_break_end_time")
//	private String qcBreakEndTime;

	@Column(name = "qc_break_time")
	private long qcBreakTime;

	@Column(name = "complexity")
	private int complexity;

	@Column(name = "inprogress_rating")
	private double inprogressRating;

	@Column(name = "qa_rating")
	private double qaRating;

	@Column(name = "team_id")
	private String teamId;

	private boolean inprogressPerson;

	private boolean qaPerson;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
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

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public LocalDateTime getChangedTime() {
		return changedTime;
	}

	public void setChangedTime(LocalDateTime changedTime) {
		this.changedTime = changedTime;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public LocalDateTime getInProgressBreakStartTime() {
		return inProgressBreakStartTime;
	}

	public void setInProgressBreakStartTime(LocalDateTime inProgressBreakStartTime) {
		this.inProgressBreakStartTime = inProgressBreakStartTime;
	}

	public long getInProgressBreakTime() {
		return inProgressBreakTime;
	}

	public void setInProgressBreakTime(long inProgressBreakTime) {
		this.inProgressBreakTime = inProgressBreakTime;
	}

	public LocalDateTime getQcBreakStartTime() {
		return qcBreakStartTime;
	}

	public void setQcBreakStartTime(LocalDateTime qcBreakStartTime) {
		this.qcBreakStartTime = qcBreakStartTime;
	}

	public long getQcBreakTime() {
		return qcBreakTime;
	}

	public void setQcBreakTime(long qcBreakTime) {
		this.qcBreakTime = qcBreakTime;
	}

	public int getComplexity() {
		return complexity;
	}

	public void setComplexity(int complexity) {
		this.complexity = complexity;
	}

	public double getInprogressRating() {
		return inprogressRating;
	}

	public void setInprogressRating(double inprogressRating) {
		this.inprogressRating = inprogressRating;
	}

	public double getQaRating() {
		return qaRating;
	}

	public void setQaRating(double qaRating) {
		this.qaRating = qaRating;
	}

	public String getTeamId() {
		return teamId;
	}

	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	public boolean isInprogressPerson() {
		return inprogressPerson;
	}

	public void setInprogressPerson(boolean inprogressPerson) {
		this.inprogressPerson = inprogressPerson;
	}

	public boolean isQaPerson() {
		return qaPerson;
	}

	public void setQaPerson(boolean qaPerson) {
		this.qaPerson = qaPerson;
	}

}
