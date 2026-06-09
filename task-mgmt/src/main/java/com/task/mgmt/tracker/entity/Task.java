
package com.task.mgmt.tracker.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "task")
public class Task {

	@Id
	@GenericGenerator(name = "sequence_task_id", strategy = "com.task.mgmt.tracker.config.CustomTaskIdGenerator")
	@GeneratedValue(generator = "sequence_task_id")
	private String id;

	@Column(length = 70)
	private String title;

	@Column(name = "short_description", length = 90)
	private String shortDescription;

	@Column(length = 10000)
	private String description;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private Project project;

	@Column(length = 30)
	private String status;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private Member createdBy;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Column(name = "modified_date")
	private LocalDateTime modifiedDate;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private Member modifiedBy;

	@Column(name = "start_date")
	private LocalDateTime startDate;

	@Column(name = "end_date")
	private LocalDateTime endDate;

	@Column(name = "actual_start_date")
	private LocalDateTime actualStartDate;

	@Column(name = "actual_completed_date")
	private LocalDateTime actualCompletedDate;

	private String priority;

	private int position;

	private int complexity;

	@Column(name = "is_active")
	private boolean isActive;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private Member assignedBy;

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
	@JoinTable(name = "task_assigned_tags", joinColumns = @JoinColumn(name = "task_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private Set<Tag> tags = new HashSet<>();

	@OneToMany(mappedBy = "task", cascade = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REMOVE }, orphanRemoval = true)
	private List<TaskAssignment> assignments = new ArrayList<>();

	@OneToMany(mappedBy = "task", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
	@OrderBy("createdDate")
	private List<Comments> comments = new ArrayList<>();

	@OneToMany(mappedBy = "task", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
	@OrderBy("uploadedDate")
	private List<Attachment> attachment = new ArrayList<>();

	@OneToMany(mappedBy = "task", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
	@OrderBy("createdDate")
	private List<Activity> activity = new ArrayList<>();

	@OneToMany(mappedBy = "task", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
	@OrderBy("changedTime")
	private List<TaskStatusTrack> taskStatusTrack = new ArrayList<>();

	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
	@OrderBy("reportedAt")
	private List<Bug> bugs = new ArrayList<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Member getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Member createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(LocalDateTime modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Member getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Member modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public LocalDateTime getActualStartDate() {
		return actualStartDate;
	}

	public void setActualStartDate(LocalDateTime actualStartDate) {
		this.actualStartDate = actualStartDate;
	}

	public LocalDateTime getActualCompletedDate() {
		return actualCompletedDate;
	}

	public void setActualCompletedDate(LocalDateTime actualCompletedDate) {
		this.actualCompletedDate = actualCompletedDate;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getComplexity() {
		return complexity;
	}

	public void setComplexity(int complexity) {
		this.complexity = complexity;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	public List<TaskAssignment> getAssignments() {
		return assignments;
	}

	public void setAssignments(List<TaskAssignment> assignments) {
		this.assignments = assignments;
	}

	public List<Comments> getComments() {
		return comments;
	}

	public void setComments(List<Comments> comments) {
		this.comments = comments;
	}

	public List<Attachment> getAttachment() {
		return attachment;
	}

	public void setAttachment(List<Attachment> attachment) {
		this.attachment = attachment;
	}

	public List<Activity> getActivity() {
		return activity;
	}

	public void setActivity(List<Activity> activity) {
		this.activity = activity;
	}

	public List<TaskStatusTrack> getTaskStatusTrack() {
		return taskStatusTrack;
	}

	public void setTaskStatusTrack(List<TaskStatusTrack> taskStatusTrack) {
		this.taskStatusTrack = taskStatusTrack;
	}

	public Member getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(Member assignedBy) {
		this.assignedBy = assignedBy;
	}

	public List<Bug> getBugs() {
		return bugs;
	}

	public void setBugs(List<Bug> bugs) {
		this.bugs = bugs;
	}

}
