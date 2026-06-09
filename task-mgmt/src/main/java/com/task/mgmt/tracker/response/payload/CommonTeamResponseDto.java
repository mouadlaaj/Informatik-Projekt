package com.task.mgmt.tracker.response.payload;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonTeamResponseDto {
	
	    private String id;
	    
	    private String teamName;
	    
	    private CommonMemberResponseDto teamLeadId;

	    private String status;

	    private LocalDateTime createdDate;

	    private CommonMemberResponseDto createdBy;

	    private LocalDateTime modifiedDate;

	    private CommonMemberResponseDto modifiedBy;
	    
	    private Set<CommonMemberResponseDto> members = new HashSet<>();

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getTeamName() {
			return teamName;
		}

		public void setTeamName(String teamName) {
			this.teamName = teamName;
		}

		public CommonMemberResponseDto getTeamLeadId() {
			return teamLeadId;
		}

		public void setTeamLeadId(CommonMemberResponseDto teamLeadId) {
			this.teamLeadId = teamLeadId;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public LocalDateTime getCreatedDate() {
			return createdDate;
		}

		public void setCreatedDate(LocalDateTime createdDate) {
			this.createdDate = createdDate;
		}

		public CommonMemberResponseDto getCreatedBy() {
			return createdBy;
		}

		public void setCreatedBy(CommonMemberResponseDto createdBy) {
			this.createdBy = createdBy;
		}

		public LocalDateTime getModifiedDate() {
			return modifiedDate;
		}

		public void setModifiedDate(LocalDateTime modifiedDate) {
			this.modifiedDate = modifiedDate;
		}

		public CommonMemberResponseDto getModifiedBy() {
			return modifiedBy;
		}

		public void setModifiedBy(CommonMemberResponseDto modifiedBy) {
			this.modifiedBy = modifiedBy;
		}

		public Set<CommonMemberResponseDto> getMembers() {
			return members;
		}

		public void setMembers(Set<CommonMemberResponseDto> members) {
			this.members = members;
		}
	

}
