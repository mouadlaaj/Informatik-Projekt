package com.task.mgmt.tracker.response.payload;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FirstPageTaskResponseDto {

	private long todoTaskCount;

	private long designTaskCount;

	private long developmentTaskCount;

	private long testTaskCount;

	private long doneTaskCount;

	private long blockerTaskCount;

	Map<String, List<TaskResponseDto>> tasks;

	public long getTodoTaskCount() {
		return todoTaskCount;
	}

	public void setTodoTaskCount(long todoTaskCount) {
		this.todoTaskCount = todoTaskCount;
	}

	public long getDesignTaskCount() {
		return designTaskCount;
	}

	public void setDesignTaskCount(long designTaskCount) {
		this.designTaskCount = designTaskCount;
	}

	public long getDevelopmentTaskCount() {
		return developmentTaskCount;
	}

	public void setDevelopmentTaskCount(long developmentTaskCount) {
		this.developmentTaskCount = developmentTaskCount;
	}

	public long getTestTaskCount() {
		return testTaskCount;
	}

	public void setTestTaskCount(long testTaskCount) {
		this.testTaskCount = testTaskCount;
	}

	public long getBlockerTaskCount() {
		return blockerTaskCount;
	}

	public void setBlockerTaskCount(long blockerTaskCount) {
		this.blockerTaskCount = blockerTaskCount;
	}

	public long getDoneTaskCount() {
		return doneTaskCount;
	}

	public void setDoneTaskCount(long doneTaskCount) {
		this.doneTaskCount = doneTaskCount;
	}

	public Map<String, List<TaskResponseDto>> getTasks() {
		return tasks;
	}

	public void setTasks(Map<String, List<TaskResponseDto>> tasks) {
		this.tasks = tasks;
	}

}
