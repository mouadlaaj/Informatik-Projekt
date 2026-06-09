package com.task.mgmt.tracker.request.payload;

import com.task.mgmt.tracker.response.payload.CommonMemberResponseDto;
import com.task.mgmt.tracker.response.payload.TaskStatusReportCountDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberTaskReportDto {

	private CommonMemberResponseDto member;
    private TaskStatusReportCountDto taskCounts;
	public CommonMemberResponseDto getMember() {
		return member;
	}
	public void setMember(CommonMemberResponseDto member) {
		this.member = member;
	}
	public TaskStatusReportCountDto getTaskCounts() {
		return taskCounts;
	}
	public void setTaskCounts(TaskStatusReportCountDto taskCounts) {
		this.taskCounts = taskCounts;
	}
    
    
    
}
