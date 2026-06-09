package com.task.mgmt.tracker.service;

import java.time.LocalDateTime;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.mgmt.tracker.constant.Status;
import com.task.mgmt.tracker.constant.TaskStatus;
import com.task.mgmt.tracker.entity.Attachment;
import com.task.mgmt.tracker.entity.Member;
import com.task.mgmt.tracker.entity.Task;
import com.task.mgmt.tracker.exception.AppException;
import com.task.mgmt.tracker.exception.NotFoundException;
import com.task.mgmt.tracker.repository.AttachmentRepository;
import com.task.mgmt.tracker.repository.MemberRepository;
import com.task.mgmt.tracker.repository.TaskRepository;
import com.task.mgmt.tracker.request.payload.ActivityRequestDto;
import com.task.mgmt.tracker.request.payload.CreateAttachmentRequestDto;

import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.results.Result;

@Service
public class AttachmentService {

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	TaskRepository taskRepo;

	@Autowired
	MemberRepository memberRepo;

	@Autowired
	AttachmentRepository attachmentRepo;
	
	@Autowired
	TaskService taskService;

	@Value("${file.upload.public-key}")
	private String publicKey;

	@Value("${file.upload.private-key}")
	private String privateKey;

	@Value("${file.upload.url}")
	private String url;

	private static final Logger LOGGER = LoggerFactory.getLogger(AttachmentService.class);

	public Attachment createAttachment(String requestAttachment,String memberId, MultipartFile file) {
		try {

			CreateAttachmentRequestDto requestDto = objectMapper.readValue(requestAttachment,
					CreateAttachmentRequestDto.class);

			if (memberId == null) {
				throw new NotFoundException("Attachment uploaded by member id is required");
			}

			if (requestDto.getTaskId() == null) {
				throw new NotFoundException("Task id is required");
			}

			Member member = memberRepo.findByIdAndStatus(memberId, Status.ACTIVE.name());

			if (member == null) {
				LOGGER.error("Exception occured while create Attachment,Attachment uploaded by member id not Found :{}",memberId);
				throw new NotFoundException("Attachment uploaded by member id not Found");
			}

			Task task = taskRepo.findById(requestDto.getTaskId())
					.orElseThrow(() -> new NotFoundException("Task id not found"));

			if (task != null && task.getStatus().equals(TaskStatus.DONE.name())) {
				LOGGER.error("Exception occured while create Attachment,Task id not found :{}",requestDto.getTaskId());
				throw new NotFoundException("Task id not found");
			}

			Attachment attachment = new Attachment();
			attachment.setStatus(Status.ACTIVE.name());
			attachment.setUploadedBy(member);
			attachment.setTask(task);
			attachment.setProject(task.getProject());

			String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
			ImageKit imageKit = ImageKit.getInstance();
			Configuration config = new Configuration(publicKey, privateKey, url);
			imageKit.setConfig(config);
			byte[] fileBytes = file.getBytes();
			String base64 = Base64.getEncoder().encodeToString(fileBytes); 
			FileCreateRequest fileCreateRequest = new FileCreateRequest(base64, fileName);

			Result result = imageKit.upload(fileCreateRequest); 

			attachment.setUploadedDate(LocalDateTime.now());
			attachment.setUrl(result.getUrl());
			
			Attachment save = attachmentRepo.save(attachment);
			
			ActivityRequestDto activityRequestDto = new ActivityRequestDto();
			activityRequestDto.setProjectId(save.getProject().getId());
			activityRequestDto.setCreatedBy(save.getUploadedBy().getId());
			activityRequestDto.setCreatedDate(save.getUploadedDate());
			activityRequestDto.setAttachments(save.getUrl());
			
			taskService.createActivity("attached "+save.getUrl()+" to this task",save.getUploadedBy(),activityRequestDto,save.getTask());
			
			return save;
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception occured while create Attachment : {}", e.getMessage());
			throw new AppException("Exception occured while create Attachment : " + e.getMessage());
		}
	}

	public void deleteAttachment(String attachmentId, String memberId) {
		try {
			Member member = memberRepo.findByIdAndStatus(memberId, Status.ACTIVE.name());
			if (member == null) {
				LOGGER.error("Exception occured while delete Attachment,Attachment delete member id not Found :{}",memberId);
				throw new NotFoundException("Attachment delete member id not Found");
			}
			Attachment attachment = attachmentRepo.findById(attachmentId).orElseThrow(() -> {
				LOGGER.error("Exception occured while delete Attachment,Attachment id not found: {}", attachmentId);
				return new NotFoundException("Attachment id not found: " + attachmentId);
			});		
			ActivityRequestDto activityRequestDto = new ActivityRequestDto();
			activityRequestDto.setProjectId(attachment.getProject().getId());
			activityRequestDto.setModifiedBy(member.getId());
			activityRequestDto.setModifiedDate(LocalDateTime.now());
			activityRequestDto.setAttachments(attachment.getUrl());
			taskService.createActivity("attachment deleted ",member,activityRequestDto,attachment.getTask());
			attachmentRepo.deleteById(attachmentId);
			
		} catch (Exception e) {
			LOGGER.error("Exception occured while delete Attachment : {}", e.getMessage());
			throw new AppException("Exception occured while delete Attachment : " + e.getMessage());
		}
	}
}
