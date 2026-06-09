package com.task.mgmt.tracker.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Table(name = "attachments")
@Entity
@Getter
@Setter
public class Attachment {
	
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;
	
	@Column(name = "file_path_url")
	private String url;
	
	@Column(name = "created_date")
	private LocalDateTime uploadedDate;
	
	@ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
	@JoinColumn(name = "modified_by")
	private Member uploadedBy;
	
	@Column(name = "status")
	private String status;
	
	@JsonIgnore
	@ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
	private Project project;
	
	@JsonIgnore
	@ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
	@JoinColumn(name = "task_id")
	private Task task;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public LocalDateTime getUploadedDate() {
		return uploadedDate;
	}

	public void setUploadedDate(LocalDateTime uploadedDate) {
		this.uploadedDate = uploadedDate;
	}

	public Member getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(Member uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}
	
	
}
