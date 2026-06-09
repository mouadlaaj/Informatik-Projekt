package com.task.mgmt.tracker.request.payload;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetTeamMembersRequestDto {
   
	@NotEmpty(message = "Project Id  are required")
	private String projectId;
	
	@NotEmpty(message = "Team  are required")
	@Size(min = 1, message = "At least one team  must be assigned")
	private List<String> teams ;
}
