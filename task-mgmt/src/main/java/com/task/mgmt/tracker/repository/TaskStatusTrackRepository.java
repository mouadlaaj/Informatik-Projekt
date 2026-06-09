package com.task.mgmt.tracker.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.task.mgmt.tracker.entity.TaskStatusTrack;

@Repository
public interface TaskStatusTrackRepository extends JpaRepository<TaskStatusTrack, String> {

	List<TaskStatusTrack> findByMemberIdAndChangedTimeBetween(String loggedUser, LocalDateTime startDateTime,
			LocalDateTime endDateTime);

	List<TaskStatusTrack> findByMemberIdOrTask_AssignedBy_IdAndChangedTimeBetween(String loggedUser, String loggedUser2,
			LocalDateTime startDateTime, LocalDateTime endDateTime);

//	List<TaskStatusTrack> findByTaskIdAndMemberIdOrderByChangedTimeAsc(String taskId, String memberId);
//
//	List<TaskStatusTrack> findAllByChangedStatusAndTaskId(String status, String taskId);
//
//	List<TaskStatusTrack> findAllByTaskId(String taskId);
//
//	List<TaskStatusTrack> findAllByTaskIdIn(List<String> collect);
//
//	List<TaskStatusTrack> findAllByChangedTimeOrderByChangedTimeDesc(LocalDateTime startDateTime);
//
//	List<TaskStatusTrack> findAllByChangedTimeBetweenOrderByChangedTimeDesc(LocalDateTime startDateTime,
//			LocalDateTime endDateTime);
//
//	@Query("SELECT t FROM TaskStatusTrack t "
//			+ "WHERE (t.task.assignedTo.id = :memberId OR t.task.qcAssignedTo.id = :memberId OR t.task.assignedBy.id = :memberId OR t.task.qcAssignedBy.id = :memberId) "
//			+ "AND t.changedTime BETWEEN :startDateTime AND :endDateTime " + "ORDER BY t.changedTime DESC")
//	List<TaskStatusTrack> findAllByTaskAssignedToOrByIdOrTaskQcAssignedByIdOrToAndChangedTimeBetweenOrderByChangedTimeDesc(
//			@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime,
//			@Param("memberId") String memberId);
//
//	@Query("SELECT t FROM TaskStatusTrack t "
//			+ "WHERE (t.task.assignedTo.id = :memberId OR t.task.qcAssignedTo.id = :memberId) "
//			+ "AND t.changedTime BETWEEN :startDateTime AND :endDateTime " + "ORDER BY t.changedTime DESC")
//	List<TaskStatusTrack> findByAssignedToOrQcAssignedToAndChangedTimeBetween(@Param("memberId") String memberId,
//			@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);
//
//	@Query("SELECT t FROM TaskStatusTrack t "
//			+ "WHERE (t.task.assignedTo.id = :memberId OR t.task.assignedBy.id = :memberId OR t.task.qcAssignedBy.id = :memberId OR t.task.qcAssignedTo.id = :memberId) "
//			+ "AND t.changedTime BETWEEN :startDateTime AND :endDateTime " + "ORDER BY t.changedTime DESC")
//	List<TaskStatusTrack> findByAssignedToOrAssignedByOrQcAssignedToOrQcAssignedByAndChangedTimeBetween(
//			@Param("memberId") String memberId, @Param("startDateTime") LocalDateTime startDateTime,
//			@Param("endDateTime") LocalDateTime endDateTime);
//
//	List<TaskStatusTrack> findAllByChangedTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
//
//	@Query("SELECT t FROM TaskStatusTrack t "
//			+ "WHERE (t.task.assignedTo.id IN :memberIds OR t.task.assignedBy.id IN :memberIds "
//			+ "OR t.task.qcAssignedTo.id IN :memberIds OR t.task.qcAssignedBy.id IN :memberIds) "
//			+ "AND t.changedTime BETWEEN :startDateTime AND :endDateTime " + "ORDER BY t.changedTime DESC")
//	List<TaskStatusTrack> findAllByChangedTimeBetweenAndTaskAssignedToIdInOrTaskAssignedByIdInOrTaskQcAssignedToIdInOrTaskQcAssignedByIdInOrderByChangedTimeDesc(
//			@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime,
//			@Param("memberIds") Set<String> memberIds);
//	
//	
//	@Query("SELECT t FROM TaskStatusTrack t "
//		    + "WHERE (t.task.assignedTo.id = :memberId OR t.task.qcAssignedTo.id = :memberId) "
//		    + "AND t.changedTime BETWEEN :startDateTime AND :endDateTime "
//		    + "AND t.task.status IN :statuses "
//		    + "ORDER BY t.changedTime DESC")
//		List<TaskStatusTrack> findByAssignedToOrQcAssignedToAndChangedTimeBetweenAndStatusIn(
//		    @Param("memberId") String memberId,
//		    @Param("startDateTime") LocalDateTime startDateTime, 
//		    @Param("endDateTime") LocalDateTime endDateTime,
//		    @Param("statuses") List<String> statuses);
//
//		@Query("SELECT t FROM TaskStatusTrack t "
//		    + "WHERE (t.task.assignedTo.id = :memberId OR t.task.assignedBy.id = :memberId OR t.task.qcAssignedBy.id = :memberId OR t.task.qcAssignedTo.id = :memberId) "
//		    + "AND t.changedTime BETWEEN :startDateTime AND :endDateTime "
//		    + "AND t.task.status IN :statuses "
//		    + "ORDER BY t.changedTime DESC")
//		List<TaskStatusTrack> findByAssignedToOrAssignedByOrQcAssignedToOrQcAssignedByAndChangedTimeBetweenAndStatusIn(
//		    @Param("memberId") String memberId, 
//		    @Param("startDateTime") LocalDateTime startDateTime,
//		    @Param("endDateTime") LocalDateTime endDateTime,
//		    @Param("statuses") List<String> statuses);
//
//		@Query("SELECT t FROM TaskStatusTrack t "
//		    + "WHERE t.changedTime BETWEEN :startDateTime AND :endDateTime "
//		    + "AND t.task.status IN :statuses "
//		    + "ORDER BY t.changedTime DESC")
//		List<TaskStatusTrack> findAllByChangedTimeBetweenAndStatusIn(
//		    @Param("startDateTime") LocalDateTime startDateTime, 
//		    @Param("endDateTime") LocalDateTime endDateTime,
//		    @Param("statuses") List<String> statuses);

}
