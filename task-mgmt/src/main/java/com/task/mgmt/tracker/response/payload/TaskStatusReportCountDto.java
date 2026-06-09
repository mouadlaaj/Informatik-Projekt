package com.task.mgmt.tracker.response.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStatusReportCountDto {
	private Long todo;
	
	private float todoPercentage;
	
	private Long inprogress;
	
	private float inprogressPercentage;
	
	private Long qa;
	
	private float qaPercentage;
	
	private Long done;
	
	private float donePercentage;
	
	private Long hold;
	
	private Long drop;
	
	private float completionPercentage;

	public Long getTodo() {
		return todo;
	}

	public void setTodo(Long todo) {
		this.todo = todo;
	}

	public float getTodoPercentage() {
		return todoPercentage;
	}

	public void setTodoPercentage(float todoPercentage) {
		this.todoPercentage = todoPercentage;
	}

	public Long getInprogress() {
		return inprogress;
	}

	public void setInprogress(Long inprogress) {
		this.inprogress = inprogress;
	}

	public float getInprogressPercentage() {
		return inprogressPercentage;
	}

	public void setInprogressPercentage(float inprogressPercentage) {
		this.inprogressPercentage = inprogressPercentage;
	}

	public Long getQa() {
		return qa;
	}

	public void setQa(Long qa) {
		this.qa = qa;
	}

	public float getQaPercentage() {
		return qaPercentage;
	}

	public void setQaPercentage(float qaPercentage) {
		this.qaPercentage = qaPercentage;
	}

	public Long getDone() {
		return done;
	}

	public void setDone(Long done) {
		this.done = done;
	}

	public float getDonePercentage() {
		return donePercentage;
	}

	public void setDonePercentage(float donePercentage) {
		this.donePercentage = donePercentage;
	}

	public Long getHold() {
		return hold;
	}

	public void setHold(Long hold) {
		this.hold = hold;
	}

	public Long getDrop() {
		return drop;
	}

	public void setDrop(Long drop) {
		this.drop = drop;
	}

	public float getCompletionPercentage() {
		return completionPercentage;
	}

	public void setCompletionPercentage(float completionPercentage) {
		this.completionPercentage = completionPercentage;
	}
	
	
}
