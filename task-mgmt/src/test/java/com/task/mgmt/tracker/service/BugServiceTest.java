package com.task.mgmt.tracker.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.task.mgmt.tracker.constant.BugStatus;
import com.task.mgmt.tracker.constant.TaskDesignation;
import com.task.mgmt.tracker.constant.TaskStatus;
import com.task.mgmt.tracker.constant.WorkStatus;
import com.task.mgmt.tracker.entity.Bug;
import com.task.mgmt.tracker.entity.Member;
import com.task.mgmt.tracker.entity.Task;
import com.task.mgmt.tracker.entity.TaskAssignment;
import com.task.mgmt.tracker.exception.AppException;
import com.task.mgmt.tracker.repository.TaskRepository;
import com.task.mgmt.tracker.request.payload.BugRequestDto;
import com.task.mgmt.tracker.request.payload.BugRequestUpdateDto;
import com.task.mgmt.tracker.response.payload.DetailedTaskResponseDto;

public class BugServiceTest {

    @Mock
    private TaskRepository taskRepo;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private BugService bugService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void reportBugs_Positive_NewBug() {
        BugRequestDto dto = new BugRequestDto();
        dto.setTaskId("task123");

        BugRequestDto.BugItemDto bugItem = new BugRequestDto.BugItemDto();
        bugItem.setTitle("Bug 1");
        bugItem.setDescription("Bug description");
        bugItem.setSeverity("HIGH");
        dto.setBugs(List.of(bugItem));

        Task task = new Task();
        task.setId("task123");
        task.setStatus(TaskStatus.TESTING.name());
        task.setBugs(new ArrayList<>()); 
        Member tester = new Member();
        tester.setId("member1");

        TaskAssignment assignment = new TaskAssignment();
        assignment.setAssignedTo(tester);
        assignment.setDesignation(TaskDesignation.TESTER.name());
        assignment.setWorkStatus(WorkStatus.WIP);

        task.setAssignments(List.of(assignment));

        when(taskRepo.findById("task123")).thenReturn(Optional.of(task));
        when(taskRepo.save(any())).thenReturn(task);
        when(taskService.convertTaskToDto(task)).thenReturn(new DetailedTaskResponseDto());

        DetailedTaskResponseDto response = bugService.reportBugs(dto, "member1");
        assertNotNull(response);
    }

    @Test
    void reportBugs_Negative_NotTester() {
        Task task = new Task();
        task.setAssignments(List.of());

        when(taskRepo.findById("task123")).thenReturn(Optional.of(task));

        BugRequestDto dto = new BugRequestDto();
        dto.setTaskId("task123");
        dto.setBugs(List.of());

        AppException ex = assertThrows(AppException.class, () -> bugService.reportBugs(dto, "invalid"));
        assertEquals("You are not assigned as TESTER for this task", ex.getMessage());
    }


    @Test
    void updateBugStatus_Positive() {
        Bug bug = new Bug();
        bug.setId(1L);

        BugRequestUpdateDto updateDto = new BugRequestUpdateDto();
        updateDto.setId(1L);
        updateDto.setStatus(BugStatus.FIXED.name());

        Member member = new Member();
        member.setId("member1");

        TaskAssignment assignment = new TaskAssignment();
        assignment.setAssignedTo(member);
        assignment.setDesignation(TaskDesignation.DEVELOPER.name());
        assignment.setWorkStatus(WorkStatus.WIP);

        Task task = new Task();
        task.setBugs(List.of(bug));
        task.setAssignments(List.of(assignment));

        when(taskRepo.findById("task1")).thenReturn(Optional.of(task));
        when(taskRepo.save(task)).thenReturn(task);

        assertDoesNotThrow(() -> bugService.updateBugStatus("task1", List.of(updateDto), "member1"));
    }

    @Test
    void updateBugStatus_Negative_BugNotFound() {
        Task task = new Task();
        task.setBugs(List.of());

        when(taskRepo.findById("task1")).thenReturn(Optional.of(task));

        BugRequestUpdateDto dto = new BugRequestUpdateDto();
        dto.setId(99L);
        dto.setStatus("FIXED");

        AppException ex = assertThrows(AppException.class,
                () -> bugService.updateBugStatus("task1", List.of(dto), "member1"));
        assertTrue(ex.getMessage().contains("No bugs found"));
    }


    @Test
    void deleteBug_Positive() {
        Bug bug = new Bug();
        bug.setId(1L);

        Member member = new Member();
        member.setId("member1");

        TaskAssignment assignment = new TaskAssignment();
        assignment.setAssignedTo(member);
        assignment.setDesignation(TaskDesignation.TESTER.name());

        Task task = new Task();
        task.setBugs(new ArrayList<>(List.of(bug)));
        task.setAssignments(List.of(assignment));

        when(taskRepo.findById("task123")).thenReturn(Optional.of(task));
        when(taskRepo.save(any())).thenReturn(task);

        assertDoesNotThrow(() -> bugService.deleteBug("task123", "member1", 1L));
    }


    @Test
    void deleteBug_Negative_NotTester() {
        Task task = new Task();
        task.setAssignments(List.of());

        when(taskRepo.findById("task123")).thenReturn(Optional.of(task));

        AppException ex = assertThrows(AppException.class,
                () -> bugService.deleteBug("task123", "member1", 1L));

        assertEquals("You are not assigned as TESTER for this task", ex.getMessage());
    }
}