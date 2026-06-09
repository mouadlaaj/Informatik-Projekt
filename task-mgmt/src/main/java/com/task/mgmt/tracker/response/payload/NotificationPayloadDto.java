package com.task.mgmt.tracker.response.payload;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationPayloadDto {
	public String id;
	public MemberDto from;
	public MemberDto to;
	public String message;
	public boolean viewStatus;
	public LocalDateTime time;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public MemberDto getFrom() {
		return from;
	}
	public void setFrom(MemberDto from) {
		this.from = from;
	}
	public MemberDto getTo() {
		return to;
	}
	public void setTo(MemberDto to) {
		this.to = to;
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
	public LocalDateTime getTime() {
		return time;
	}
	public void setTime(LocalDateTime time) {
		this.time = time;
	}
	
	
}
