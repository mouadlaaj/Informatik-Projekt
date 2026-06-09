package com.task.mgmt.tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.task.mgmt.tracker.entity.Comments;
import com.task.mgmt.tracker.entity.Task;
import com.task.mgmt.tracker.entity.TaskAssignment;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {

	Task findByIdAndIsActive(String taskId, boolean b);

	List<Comments> findAllCommentsByIdOrderByCreatedDateDesc(String taskId);

	Task findByIdAndIsActiveAndCreatedById(String taskId, boolean b, String memberId);

	@Query("SELECT ta FROM TaskAssignment ta " + "WHERE ta.task.status = 'DONE' AND ta.assignedTo IS NOT NULL")
	List<TaskAssignment> findAllDoneAssignments();

	@Query("SELECT t FROM TaskAssignment t WHERE t.assignedBy.id = :memberId OR t.assignedTo.id = :memberId")
	List<TaskAssignment> findAllByAssignedByOrAssignedTo(@Param("memberId") String memberId);

	// Task findByParentTaskIdAndSubTasksId(String taskId, String subTaskId);

//	Task findByParentTaskId(String mainTaskId);
//
//	Task findByParentTaskIdAndSubTasksId(String mainTaskId, String subTaskId);
//
//	Task findByIdAndSubTasksId(String mainTaskId, String subTaskId);
//
//	Task findByIdAndIsActive(String taskId, boolean isActive);
//
//	List<Task> findAllByAssignedToId(String memberId);
//
//	List<Comments> findAllCommentsByIdOrderByCreatedDateDesc(String taskId);
//
//	List<Task> findAllByAssignedToIdAndAssignedDateBetween(String userId, LocalDateTime startDateTime,
//			LocalDateTime endDateTime);
//
//	List<Task> findAllByAssignedDateBetweenOrQcStartDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime,
//			LocalDateTime qcStartDateTime, LocalDateTime qcEndDateTime);
//
//	List<Task> findAllByStartDateBetweenOrQcStartDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime,
//			LocalDateTime qcStartDateTime, LocalDateTime qcEndDateTime);
//
//	List<Task> findAllByStartDateBetweenAndAssignedToIdOrQcStartDateBetweenAndQcAssignedToId(
//			LocalDateTime startDateTime, LocalDateTime endDateTime, String assignedToId, LocalDateTime qcStartDateTime,
//			LocalDateTime qcEndDateTime, String qcAssignedToId);
//
//	List<Task> findAllByAssignedToIdAndActualCompletedDateBetween(String memberId, LocalDateTime startDateTime,
//			LocalDateTime endDateTime);
//
//	List<Task> findByAssignedToIdAndActualStartDateIsNotNullAndStatusNotInAndActualStartDateBetween(String assignedToId,
//			List<String> excludedStatuses, LocalDateTime startDateTime, LocalDateTime endDateTime);
//
//	List<Task> findAllByProjectTeamsInAndAssignedDateBetween(List<Team> teams, LocalDateTime startDateTime,
//			LocalDateTime endDateTime);
//
//	Page<Task> findAllByProjectTeamsMembersIdAndStatusOrderByModifiedDateDesc(String memberId, String status,
//			PageRequest of);
//
//	Page<Task> findAllByStatusOrderByModifiedDateDesc(String status, PageRequest of);
//
//	List<Task> findAllByAssignedToIdAndAssignedDateBetweenOrQcAssignedToIdAndQcStartDateBetween(String loggedUser,
//			LocalDateTime startDateTime, LocalDateTime endDateTime, String qcLoggedUser, LocalDateTime qcStartDate,
//			LocalDateTime qcEndDate);
//
//	List<Task> findAllByAssignedToIdOrQcAssignedToIdAndAssignedDateBetweenOrQcStartDateBetween(String id, String id2,
//			LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDateTime startDateTime2,
//			LocalDateTime endDateTime2);
//
//	List<Task> findAllByProjectTeamsIdAndAssignedDateBetweenOrQcStartDateBetween(String teamId, LocalDateTime startDate,
//			LocalDateTime endDateTime, LocalDateTime qcStartDate, LocalDateTime qcEndDateTime);
//
//	List<Task> findAllByQcAssignedToIdAndQcStartDateBetween(String loggedUser, LocalDateTime startDateTime,
//			LocalDateTime endDateTime);
//
//	List<Task> findAllByAssignedDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
//
//	List<Task> findAllByQcStartDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
//
//	List<Task> findAllByProjectTeamsInAndAssignedDateBetweenOrQcStartDateBetween(List<Team> teams,
//			LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDateTime qcStartDateTime,
//			LocalDateTime qcEndDateTime);
//
//	@Query("SELECT t FROM Task t " + "WHERE (t.assignedTo.id = :memberId AND t.status = 'INPROGRESS') "
//			+ "OR (t.qcAssignedTo.id = :memberId AND t.status = 'QA') ")
//	List<Task> findTasksForMember(@Param("memberId") String memberId);
//
//	@Query("SELECT t FROM Task t " + "WHERE (t.assignedTo.id = :memberId OR t.qcAssignedTo.id = :memberId) "
//			+ "AND EXISTS (SELECT ts FROM t.taskStatusTrack ts WHERE ts.changedStatus = :status)")
//	List<Task> findTasksByMemberIdAndStatus(@Param("memberId") String memberId, @Param("status") String status);
//
//	@Query("SELECT DISTINCT t FROM Task t " + "JOIN t.assignedTo a " + "JOIN t.qcAssignedTo q "
//			+ "JOIN Member m ON m.id = :memberId " + "WHERE t.status = 'DONE' "
//			+ "AND FUNCTION('DATEDIFF', CURRENT_TIMESTAMP, t.qcActualCompletedDate) >= :elapsedDays " + "AND ("
//			+ "    (m.role = 'ROLE_SUPER_ADMIN' AND "
//			+ "        ((a.role = 'ROLE_ADMIN' AND a.id NOT IN (SELECT r.member.id FROM TaskRating r WHERE r.task.id = t.id)) OR "
//			+ "         (q.role = 'ROLE_ADMIN' AND q.id NOT IN (SELECT r.member.id FROM TaskRating r WHERE r.task.id = t.id)))) "
//			+ "    OR " + "    (m.role = 'ROLE_ADMIN' AND "
//			+ "        (t.assignedBy.id = :memberId OR t.qcAssignedBy.id = :memberId) AND "
//			+ "        ((a.id <> :memberId AND a.id NOT IN (SELECT r.member.id FROM TaskRating r WHERE r.task.id = t.id)) OR "
//			+ "         (q.id <> :memberId AND q.id NOT IN (SELECT r.member.id FROM TaskRating r WHERE r.task.id = t.id)))) "
//			+ ")")
//	List<Task> findTasks(@Param("memberId") String memberId, @Param("elapsedDays") int elapsedDays);

}
