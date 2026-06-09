package com.task.mgmt.tracker.response.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportResponseDto {
	private int tasksInTodo;

	private float tasksInTodoPercentage;

	private int tasksInDesign;

	private float tasksInDesignPercentage;

	private int tasksInDevelopment;

	private float tasksInDevelopmentPercentage;

	private int tasksInTest;

	private float tasksInTestPercentage;

	private int tasksInDone;

	private float tasksInDonePercentage;

	public int getTasksInTodo() {
		return tasksInTodo;
	}

	public void setTasksInTodo(int tasksInTodo) {
		this.tasksInTodo = tasksInTodo;
	}

	public float getTasksInTodoPercentage() {
		return tasksInTodoPercentage;
	}

	public void setTasksInTodoPercentage(float tasksInTodoPercentage) {
		this.tasksInTodoPercentage = tasksInTodoPercentage;
	}

	public int getTasksInDesign() {
		return tasksInDesign;
	}

	public void setTasksInDesign(int tasksInDesign) {
		this.tasksInDesign = tasksInDesign;
	}

	public float getTasksInDesignPercentage() {
		return tasksInDesignPercentage;
	}

	public void setTasksInDesignPercentage(float tasksInDesignPercentage) {
		this.tasksInDesignPercentage = tasksInDesignPercentage;
	}

	public int getTasksInDevelopment() {
		return tasksInDevelopment;
	}

	public void setTasksInDevelopment(int tasksInDevelopment) {
		this.tasksInDevelopment = tasksInDevelopment;
	}

	public float getTasksInDevelopmentPercentage() {
		return tasksInDevelopmentPercentage;
	}

	public void setTasksInDevelopmentPercentage(float tasksInDevelopmentPercentage) {
		this.tasksInDevelopmentPercentage = tasksInDevelopmentPercentage;
	}

	public int getTasksInTest() {
		return tasksInTest;
	}

	public void setTasksInTest(int tasksInTest) {
		this.tasksInTest = tasksInTest;
	}

	public float getTasksInTestPercentage() {
		return tasksInTestPercentage;
	}

	public void setTasksInTestPercentage(float tasksInTestPercentage) {
		this.tasksInTestPercentage = tasksInTestPercentage;
	}

	public int getTasksInDone() {
		return tasksInDone;
	}

	public void setTasksInDone(int tasksInDone) {
		this.tasksInDone = tasksInDone;
	}

	public float getTasksInDonePercentage() {
		return tasksInDonePercentage;
	}

	public void setTasksInDonePercentage(float tasksInDonePercentage) {
		this.tasksInDonePercentage = tasksInDonePercentage;
	}

}
