package com.task.mgmt.tracker.request.payload;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BugRequestUpdateDto {

	@NotNull(message = "Bug ID is required")
	private Long id;

	@Pattern(regexp = "FIXED|NOT_FIXED|NOT_AN_ISSUE|VERIFIED", message = "Status must be one of: FIXED, NOT_FIXED, NOT_AN_ISSUE, VERIFIED")
	private String status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
