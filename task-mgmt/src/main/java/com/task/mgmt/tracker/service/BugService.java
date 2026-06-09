package com.task.mgmt.tracker.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.task.mgmt.tracker.constant.BugStatus;
import com.task.mgmt.tracker.constant.TaskDesignation;
import com.task.mgmt.tracker.constant.TaskStatus;
import com.task.mgmt.tracker.constant.WorkStatus;
import com.task.mgmt.tracker.entity.Bug;
import com.task.mgmt.tracker.entity.Member;
import com.task.mgmt.tracker.entity.Task;
import com.task.mgmt.tracker.entity.TaskAssignment;
import com.task.mgmt.tracker.entity.TaskStatusTrack;
import com.task.mgmt.tracker.exception.AppException;
import com.task.mgmt.tracker.exception.NotFoundException;
import com.task.mgmt.tracker.repository.TaskRepository;
import com.task.mgmt.tracker.request.payload.ActivityRequestDto;
import com.task.mgmt.tracker.request.payload.BugRequestDto;
import com.task.mgmt.tracker.request.payload.BugRequestUpdateDto;
import com.task.mgmt.tracker.response.payload.DetailedTaskResponseDto;

@Service
public class BugService {

	@Autowired
	TaskRepository taskRepo;

	@Autowired
	TaskService taskService;

	private static final Logger LOGGER = LoggerFactory.getLogger(BugService.class);

	public DetailedTaskResponseDto reportBugs(BugRequestDto dto, String memberId) {
		try {
			Task task = taskRepo.findById(dto.getTaskId()).orElseThrow(() -> {
				LOGGER.error("Task not found with ID: {}", dto.getTaskId());
				return new NotFoundException("Task not found");
			});

			TaskAssignment testerAssignment = task.getAssignments().stream()
					.filter(a -> a.getAssignedTo().getId().equals(memberId)
							&& TaskDesignation.TESTER.name().equalsIgnoreCase(a.getDesignation()))
					.findFirst().orElseThrow(() -> {
						LOGGER.error("Tester assignment not found for member: {}", memberId);
						return new AppException("You are not assigned as TESTER for this task");
					});

			Member actor = testerAssignment.getAssignedTo();

			if (!TaskStatus.TESTING.name().equalsIgnoreCase(task.getStatus())) {
				LOGGER.warn("Task is not in TESTING stage. Current status: {}", task.getStatus());
				throw new AppException("Bugs can only be reported when task is in TESTING stage");
			}

			if (testerAssignment.getWorkStatus() != WorkStatus.WIP) {
				LOGGER.warn("Tester has not started work (WIP). Status: {}", testerAssignment.getWorkStatus());
				throw new AppException("You must start your work (WIP) before reporting bugs");
			}

			for (BugRequestDto.BugItemDto incoming : dto.getBugs()) {
				if (incoming.getId() == null) {
					Bug bug = new Bug();
					bug.setTitle(incoming.getTitle());
					bug.setDescription(incoming.getDescription());
					bug.setSeverity(incoming.getSeverity());
					bug.setStatus(BugStatus.NOT_FIXED.name());
					bug.setReportedAt(LocalDateTime.now());
					bug.setCreatedBy(actor);
					task.getBugs().add(bug);
					LOGGER.info("New bug reported: {}", bug.getTitle());
				} else {
					Bug existing = task.getBugs().stream().filter(b -> b.getId().equals(incoming.getId())).findFirst()
							.orElseThrow(() -> {
								LOGGER.error("Bug not found with ID: {}", incoming.getId());
								return new NotFoundException("Bug not found: " + incoming.getId());
							});
					existing.setTitle(incoming.getTitle());
					existing.setDescription(incoming.getDescription());
					existing.setSeverity(incoming.getSeverity());
					existing.setReportedAt(LocalDateTime.now());
					LOGGER.info("Existing bug updated: {}", existing.getId());
				}
			}

			TaskStatusTrack track = new TaskStatusTrack();
			track.setTask(task);
			track.setMember(actor);
			track.setChangedStatus(task.getStatus());
			track.setChangedTime(LocalDateTime.now());
			track.setMessage("Tester created or updated bug(s) during TESTING");
			task.getTaskStatusTrack().add(track);

			task.setModifiedBy(actor);
			task.setModifiedDate(LocalDateTime.now());

			Task saved = taskRepo.save(task);
			LOGGER.info("Task saved after bug report/update: {}", task.getId());

			ActivityRequestDto requestDto = new ActivityRequestDto();
			requestDto.setStatus(task.getStatus());
			requestDto.setModifiedBy(testerAssignment.getAssignedTo().getId());
			requestDto.setModifiedDate(LocalDateTime.now());

			taskService.createActivity("added bugs", testerAssignment.getAssignedTo(), requestDto, saved);
			return taskService.convertTaskToDto(saved);

		} catch (RuntimeException e) {
			LOGGER.error("Upsert bug operation failed", e);
			throw e;
		}
	}

	public void updateBugStatus(String taskId, List<BugRequestUpdateDto> updates, String memberId) {
		try {
			Task task = taskRepo.findById(taskId).orElseThrow(() -> {
				LOGGER.error("Task not found with ID: {}", taskId);
				return new NotFoundException("Task not found");
			});

			if (task.getBugs() == null || task.getBugs().isEmpty()) {
				LOGGER.warn("No bugs found for task ID: {}", taskId);
				throw new AppException("No bugs found for this task");
			}

			Optional<TaskAssignment> assignmentOpt = task.getAssignments().stream()
					.filter(a -> a.getAssignedTo().getId().equals(memberId)
							&& (TaskDesignation.DEVELOPER.name().equalsIgnoreCase(a.getDesignation())
									|| TaskDesignation.TESTER.name().equalsIgnoreCase(a.getDesignation())))
					.findFirst();

			if (assignmentOpt.isEmpty()) {
				LOGGER.error("Member {} is not assigned as DEVELOPER or TESTER", memberId);
				throw new AppException("Only a DEVELOPER or TESTER can update bug status");
			}

			if (assignmentOpt.get().getWorkStatus() != WorkStatus.WIP) {
				LOGGER.warn("Member {} has not started WIP", memberId);
				throw new AppException("You must start your work (WIP) before reporting bugs");
			}

			Member actor = assignmentOpt.get().getAssignedTo();

			for (BugRequestUpdateDto update : updates) {
				boolean found = false;
				for (Bug bug : task.getBugs()) {
					if (bug.getId().equals(update.getId())) {
						bug.setStatus(update.getStatus());
						bug.setReportedAt(LocalDateTime.now());
						found = true;
						LOGGER.info("Updated status for bug {} to {}", bug.getId(), bug.getStatus());

						TaskStatusTrack track = new TaskStatusTrack();
						track.setTask(task);
						track.setMember(actor);
						track.setChangedTime(LocalDateTime.now());
						track.setMessage("Bug " + bug.getId() + " status updated to " + bug.getStatus());
						task.getTaskStatusTrack().add(track);

						break;
					}
				}
				if (!found) {
					LOGGER.error("Bug with ID {} not found in task {}", update.getId(), taskId);
					throw new NotFoundException("Bug with ID " + update.getId() + " not found in this task");
				}
			}

			task.setModifiedBy(actor);
			task.setModifiedDate(LocalDateTime.now());

			Task saved = taskRepo.save(task);
			LOGGER.info("Bug status updated and task saved: {}", taskId);

			ActivityRequestDto requestDto = new ActivityRequestDto();
			requestDto.setStatus(task.getStatus());
			requestDto.setModifiedBy(actor.getId());
			requestDto.setModifiedDate(LocalDateTime.now());

			taskService.createActivity("updated bugs status", actor, requestDto, saved);

		} catch (RuntimeException e) {
			LOGGER.error("Bug update failed", e);
			throw e;
		}
	}

	public void deleteBug(String taskId, String memberId, Long bugId) {
		try {
			Task task = taskRepo.findById(taskId).orElseThrow(() -> {
				LOGGER.error("Task not found with ID: {}", taskId);
				return new NotFoundException("Task not found");
			});

			TaskAssignment taskAssignment = task.getAssignments().stream()
					.filter(a -> a.getAssignedTo().getId().equals(memberId)
							&& TaskDesignation.TESTER.name().equalsIgnoreCase(a.getDesignation()))
					.findFirst().orElseThrow(() -> {
						LOGGER.error("Tester assignment not found for member: {}", memberId);
						return new AppException("You are not assigned as TESTER for this task");
					});

			Optional<Bug> bugOpt = task.getBugs().stream().filter(bug -> bug.getId().equals(bugId)).findFirst();

			if (bugOpt.isEmpty()) {
				LOGGER.warn("Bug not found in task. Bug ID: {}, Task ID: {}", bugId, taskId);
				throw new NotFoundException("Bug not found in this task");
			}

			task.getBugs().remove(bugOpt.get());
			task.setModifiedDate(LocalDateTime.now());
			Task saved = taskRepo.save(task);

			LOGGER.info("Bug successfully deleted: {}", bugId);

			ActivityRequestDto requestDto = new ActivityRequestDto();
			requestDto.setStatus(task.getStatus());
			requestDto.setModifiedBy(taskAssignment.getAssignedTo().getId());
			requestDto.setModifiedDate(LocalDateTime.now());

			taskService.createActivity("deleted bugs", taskAssignment.getAssignedTo(), requestDto, saved);

		} catch (Exception e) {
			LOGGER.error("Unexpected error while deleting bug", e);
			throw new AppException(e.getMessage());
		}
	}
}