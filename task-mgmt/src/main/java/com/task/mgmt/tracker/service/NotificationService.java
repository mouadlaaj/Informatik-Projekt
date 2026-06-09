package com.task.mgmt.tracker.service;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.task.mgmt.tracker.constant.Status;
import com.task.mgmt.tracker.entity.Member;
import com.task.mgmt.tracker.entity.Notification;
import com.task.mgmt.tracker.exception.AppException;
import com.task.mgmt.tracker.repository.MemberRepository;
import com.task.mgmt.tracker.repository.NotificationRepository;
import com.task.mgmt.tracker.response.payload.MemberDto;
import com.task.mgmt.tracker.response.payload.NotificationPayloadDto;

@Component
public class NotificationService {
	
	@Autowired
	MemberRepository memberRepo;
	
	@Autowired
	NotificationRepository notificationRepo;
	
	@Autowired
    private SimpMessagingTemplate template;
	
	private static final String ALL_USER_TOPIC = "/all/users";
	
    public void sendNotification(String toMemberId, String fromMemberId, String topic, String message) throws IOException, SQLException{
    	Member toMember = memberRepo.findByIdAndStatus(toMemberId, Status.ACTIVE.name());
    	Member fromMember = memberRepo.findByIdAndStatus(fromMemberId, Status.ACTIVE.name());
    	NotificationPayloadDto dto = new NotificationPayloadDto();
    	
    	MemberDto toMemberDto = new MemberDto();
    	toMemberDto.setId(toMember.getId());
    	toMemberDto.setFirstName(toMember.getFirstName());
    	toMemberDto.setLastName(toMember.getLastName());
    	
    	MemberDto fromMemberDto = new MemberDto();
    	fromMemberDto.setId(fromMember.getId());
    	fromMemberDto.setFirstName(fromMember.getFirstName());
    	fromMemberDto.setLastName(fromMember.getLastName());
    	
    	dto.setFrom(fromMemberDto);
    	dto.setTo(toMemberDto);
    	dto.setMessage(message);
    	dto.setTime(LocalDateTime.now());
    	
    	JavaTimeModule module = new JavaTimeModule();
        LocalDateTimeDeserializer localDateTimeDeserializer = new
                LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        module.addDeserializer(LocalDateTime.class, localDateTimeDeserializer);
        ObjectMapper mapper = Jackson2ObjectMapperBuilder.json()
                .modules(module)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    	String data = mapper.writeValueAsString(dto);
    	
		template.convertAndSendToUser(toMemberId, topic, data);
		template.convertAndSend(ALL_USER_TOPIC,"send notification for all users");
		
		 Notification notification = new Notification();
		 notification.setSender(fromMember);
		 notification.setReceiver(toMember);
	     notification.setMessage(message);
	     notification.setDate(LocalDateTime.now());
		 notificationRepo.save(notification);
    }
    
    public void sendNotificationAll() {
    	template.convertAndSend(ALL_USER_TOPIC,"send notification for all users");
    }
    
    
    public List<NotificationPayloadDto> getAllNotification(String memberId) {
	    try {
	        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(3); 
	        LocalDateTime currentDate = LocalDateTime.now().plusDays(1); 
	        List<Notification> save = notificationRepo.findByReceiverIdAndDateBetweenOrderByDateDesc(memberId, twoDaysAgo, currentDate);
	        
	        List<NotificationPayloadDto> notificationDtos = save.stream().map(e -> {
	            NotificationPayloadDto notificationDto = new NotificationPayloadDto();
	            MemberDto fromMember = new MemberDto();
	            fromMember.setId(e.getSender().getId());
	            fromMember.setFirstName(e.getSender().getFirstName());
	            fromMember.setLastName(e.getSender().getLastName());
	            notificationDto.setFrom(fromMember);
	            
	            MemberDto toMember = new MemberDto();
	            toMember.setId(e.getReceiver().getId());
	            toMember.setFirstName(e.getReceiver().getFirstName()); 
	            toMember.setLastName(e.getReceiver().getLastName()); 
	            notificationDto.setId(e.getId());
	            notificationDto.setTo(toMember);
	            notificationDto.setViewStatus(e.isViewStatus());
	            notificationDto.setMessage(e.getMessage());
	            notificationDto.setTime(e.getDate());
	            
	            return notificationDto;
	        }).collect(Collectors.toList());
	        
	        return notificationDtos;
	        
	    } catch (Exception e) {
	        throw new AppException(e.getMessage());
	    }
	}

	public List<NotificationPayloadDto> updateViewStatus(String loggedUser, String id, boolean status) {
		Notification notify = notificationRepo.findByReceiverIdAndId(loggedUser, id);
		if(notify != null) {
			notify.setViewStatus(status);
			notificationRepo.save(notify);
			return getAllNotification(loggedUser);
		}
		return null;
	}
    
}
