//package com.wdm.tracker.service;
//
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//import java.util.Optional;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.wdm.tracker.constant.Status;
//import com.wdm.tracker.constant.TaskStatus;
//import com.wdm.tracker.entity.Attachment;
//import com.wdm.tracker.entity.Member;
//import com.wdm.tracker.entity.Project;
//import com.wdm.tracker.entity.Task;
//import com.wdm.tracker.exception.NotFoundException;
//import com.wdm.tracker.repository.AttachmentRepository;
//import com.wdm.tracker.repository.MemberRepository;
//import com.wdm.tracker.repository.TaskRepository;
//import com.wdm.tracker.request.payload.CreateAttachmentRequestDto;
//
//import io.imagekit.sdk.ImageKit;
//import io.imagekit.sdk.models.results.Result;
//
//class AttachmentServiceTest {
//
//    @Mock
//    private TaskRepository taskRepo;
//    @Mock
//    private MemberRepository memberRepo;
//    @Mock
//    private AttachmentRepository attachmentRepo;
//    @Mock
//    private ObjectMapper objectMapper;
//    @Mock
//    private TaskService taskService;
//    @InjectMocks
//    private AttachmentService attachmentService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void createAttachment_Positive_ShouldUploadAndReturnAttachment() throws Exception {
//        String memberId = "member123";
//        String taskId = "task123";
//
//        // Prepare request DTO JSON
//        CreateAttachmentRequestDto dto = new CreateAttachmentRequestDto();
//        dto.setTaskId(taskId);
//        String jsonDto = new ObjectMapper().writeValueAsString(dto);
//
//        Member member = new Member();
//        member.setId(memberId);
//
//        Task task = new Task();
//        task.setId(taskId);
//        task.setProject(new Project());
//        task.setStatus(TaskStatus.TODO.name());
//
//        MultipartFile mockFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "dummy content".getBytes());
//
//        Attachment savedAttachment = new Attachment();
//        savedAttachment.setId("att123");
//        savedAttachment.setUploadedBy(member);
//        savedAttachment.setTask(task);
//        savedAttachment.setProject(task.getProject());
//        savedAttachment.setUrl("http://imagekit.io/uploaded/test.jpg");
//
//        // Mock dependencies
//        when(memberRepo.findByIdAndStatus(memberId, Status.ACTIVE.name())).thenReturn(member);
//        when(taskRepo.findById(taskId)).thenReturn(Optional.of(task));
//        when(attachmentRepo.save(any(Attachment.class))).thenReturn(savedAttachment);
//
//        // Mock ImageKit upload result
//        Result mockResult = mock(Result.class);
//        when(mockResult.getUrl()).thenReturn("http://imagekit.io/uploaded/test.jpg");
//
//        ImageKit imageKit = mock(ImageKit.class);
//        ImageKit.getInstance();
//        when(imageKit.upload(any())).thenReturn(mockResult);
//
//        // Run
//        Attachment result = attachmentService.createAttachment(jsonDto, memberId, mockFile);
//        assertNotNull(result);
//        assertEquals("http://imagekit.io/uploaded/test.jpg", result.getUrl());
//    }
//
//
//    @Test
//    void createAttachment_Negative_MemberNotFound() throws Exception {
//        CreateAttachmentRequestDto dto = new CreateAttachmentRequestDto();
//        dto.setTaskId("task123");
//
//        when(objectMapper.readValue(any(String.class), eq(CreateAttachmentRequestDto.class))).thenReturn(dto);
//        when(memberRepo.findByIdAndStatus("member1", Status.ACTIVE.name())).thenReturn(null);
//
//        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "data".getBytes());
//
//        NotFoundException ex = assertThrows(NotFoundException.class,
//                () -> attachmentService.createAttachment("{}", "member1", file));
//        assertEquals("Attachment uploaded by member id not Found", ex.getMessage());
//    }
//
//    @Test
//    void deleteAttachment_Positive() {
//        Member member = new Member();
//        member.setId("member1");
//
//        Project project = new Project();
//        project.setId("proj1");
//
//        Attachment attachment = new Attachment();
//        attachment.setId("attachment1");
//        attachment.setProject(project);
//        attachment.setTask(new Task());
//        attachment.setUrl("mock-url");
//
//        when(memberRepo.findByIdAndStatus("member1", Status.ACTIVE.name())).thenReturn(member);
//        when(attachmentRepo.findById("attachment1")).thenReturn(Optional.of(attachment));
//
//        assertDoesNotThrow(() -> attachmentService.deleteAttachment("attachment1", "member1"));
//    }
//
//    @Test
//    void deleteAttachment_Negative_AttachmentNotFound() {
//        Member member = new Member();
//        member.setId("member1");
//
//        when(memberRepo.findByIdAndStatus("member1", Status.ACTIVE.name())).thenReturn(member);
//        when(attachmentRepo.findById("attachment1")).thenReturn(Optional.empty());
//
//        NotFoundException ex = assertThrows(NotFoundException.class,
//                () -> attachmentService.deleteAttachment("attachment1", "member1"));
//        assertTrue(ex.getMessage().contains("Attachment id not found"));
//    }
//}

