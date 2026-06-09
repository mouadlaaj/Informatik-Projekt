package com.task.mgmt.tracker.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.task.mgmt.tracker.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String>{

	Notification findByReceiverIdAndId(String loggedUser, String id);

	List<Notification> findByReceiverIdAndViewStatusAndDateBetweenOrderByDateDesc(String memberId, boolean b,
			LocalDateTime twoDaysAgo, LocalDateTime currentDate);

	List<Notification> findByReceiverIdAndDateBetweenOrderByDateDesc(String memberId, 
			LocalDateTime twoDaysAgo, LocalDateTime currentDate);

}
