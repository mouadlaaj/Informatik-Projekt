package com.task.mgmt.tracker.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.task.mgmt.tracker.response.payload.NotificationPayloadDto;
import com.task.mgmt.tracker.service.NotificationService;
import com.task.mgmt.tracker.utils.AppUtils;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/notification")
@CrossOrigin("*")
@SecurityRequirement(name = "token")
public class NotificationController {
	
	@Autowired
	NotificationService notificationService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);
	
	@GetMapping
	public ResponseEntity<List<NotificationPayloadDto>> getAllNotifications() {
		String loggedUser = AppUtils.getLoggedInUser();
		LOGGER.info("Fetching all unreaded nofication By :{}", loggedUser);
		List<NotificationPayloadDto> Notifications = notificationService.getAllNotification(loggedUser);
		LOGGER.info("Successfully fetching all unreaded notification, size : {}", Notifications.size());
		return new ResponseEntity<List<NotificationPayloadDto>>(Notifications, HttpStatus.OK);
	}
	
	@PutMapping("/{id}/{status}")
	public ResponseEntity<List<NotificationPayloadDto>> updateViewStatus(@PathVariable("id") String id, @PathVariable("status") boolean status) {
		String loggedUser = AppUtils.getLoggedInUser();
		LOGGER.info("Notification status read or unread status changed By :{}", loggedUser);
		List<NotificationPayloadDto> Notifications = notificationService.updateViewStatus(loggedUser, id, status);
		LOGGER.info("Notification status read or unread status changed successfully By:{}, {}", loggedUser);
		return new ResponseEntity<List<NotificationPayloadDto>>(Notifications, HttpStatus.OK);
	}
}
