package com.task.mgmt.tracker.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Notification {
	
	@Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;
	
	@JsonIgnore
    @ManyToOne
    @JoinColumn(name = "sender")
    private Member sender;
	
	@JsonIgnore
    @ManyToOne
    @JoinColumn(name = "receiver")
    private Member receiver;
	
	@Column(name = "message")
    private String message;
	
	@Column(name = "view_status")
	private boolean viewStatus;
	
	@Column(name ="date")
	private LocalDateTime date;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Member getSender() {
		return sender;
	}

	public void setSender(Member sender) {
		this.sender = sender;
	}

	public Member getReceiver() {
		return receiver;
	}

	public void setReceiver(Member receiver) {
		this.receiver = receiver;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isViewStatus() {
		return viewStatus;
	}

	public void setViewStatus(boolean viewStatus) {
		this.viewStatus = viewStatus;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	
	
	
}
