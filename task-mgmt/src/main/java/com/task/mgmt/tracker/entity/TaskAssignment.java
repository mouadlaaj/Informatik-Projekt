package com.task.mgmt.tracker.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.task.mgmt.tracker.constant.WorkStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "task_assignment")
public class TaskAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 80)
    private String designation;       

    @Column(name = "estimated_time")    
    private Integer estimatedTime;

    @Column(name = "actual_spent_time") 
    private Integer actualSpentTime;

    @Column(length = 30)
    private String stage;

    @Column(name = "stage_start")
    private LocalDateTime stageStart;

    @Column(name = "stage_end")
    private LocalDateTime stageEnd;

    @Column(length = 500)
    private String reason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    @JsonIgnore
    private Task task;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "work_status", length = 15)
    private WorkStatus workStatus = WorkStatus.NOT_TAKEN; 

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "assigned_to")
    private Member assignedTo;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "assigned_by")
    private Member assignedBy;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public Integer getEstimatedTime() {
		return estimatedTime;
	}

	public void setEstimatedTime(Integer estimatedTime) {
		this.estimatedTime = estimatedTime;
	}

	public Integer getActualSpentTime() {
		return actualSpentTime;
	}

	public void setActualSpentTime(Integer actualSpentTime) {
		this.actualSpentTime = actualSpentTime;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public LocalDateTime getStageStart() {
		return stageStart;
	}

	public void setStageStart(LocalDateTime stageStart) {
		this.stageStart = stageStart;
	}

	public LocalDateTime getStageEnd() {
		return stageEnd;
	}

	public void setStageEnd(LocalDateTime stageEnd) {
		this.stageEnd = stageEnd;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(LocalDateTime modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public WorkStatus getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(WorkStatus workStatus) {
		this.workStatus = workStatus;
	}

	public Member getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(Member assignedTo) {
		this.assignedTo = assignedTo;
	}

	public Member getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(Member assignedBy) {
		this.assignedBy = assignedBy;
	}

    
    
}
