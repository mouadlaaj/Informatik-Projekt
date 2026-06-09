package com.task.mgmt.tracker.response.payload;
import java.time.LocalDateTime;
import java.util.List;

import com.task.mgmt.tracker.entity.Member;

import lombok.Data;
@Data
public class TeamResponseDto {

    private String teamName;
    private Member teamLeaderId;
    private String createdBy;
    private LocalDateTime createdAt;
    private List<MemberDto> teamMembers;
    
    private int totalMembers;

}
