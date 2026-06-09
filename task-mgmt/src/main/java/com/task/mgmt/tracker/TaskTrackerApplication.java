package com.task.mgmt.tracker;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.task.mgmt.tracker.constant.RoleType;
import com.task.mgmt.tracker.constant.Status;
import com.task.mgmt.tracker.entity.Member;
import com.task.mgmt.tracker.exception.AppException;
import com.task.mgmt.tracker.repository.MemberRepository;
import com.task.mgmt.tracker.service.KeycloakService;

@SpringBootApplication(
		  exclude = {
		    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
		    org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
		  }
		)

public class TaskTrackerApplication implements CommandLineRunner {

	@Value("${app.default.member.password}")
	private String defaultPassword;

	@Autowired
	MemberRepository memberRepo;

	@Autowired
	KeycloakService keycloakService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public static void main(String[] args) {
		SpringApplication.run(TaskTrackerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			Member john = memberRepo.findById("tsk_001").orElse(null);
			if (john == null) {
				PasswordEncoder passwordEncoder = passwordEncoder();
				Member member = new Member();
				member.setId("tsk_001");
				member.setFirstName("John");
				member.setLastName("Doe");
				member.setPhoneNumber("9876543210");
				member.setPassword(passwordEncoder.encode(defaultPassword));
				member.setEmailId("john.doe@example.com");
				member.setRole(RoleType.MANAGER.name());
				member.setDesignation(RoleType.MANAGER.name());
				member.setAddress("123 Main Street, New York, NY 10001");
				member.setDob(LocalDate.of(1990, 7, 15));
				member.setGender("Male");
				member.setJoiningDate(LocalDate.of(2021, 5, 10));
				member.setYearsOfExperience(6.0f);
				member.setStatus(Status.ACTIVE.name());
				member.setCreatedDate(LocalDateTime.now());
				member.setCreatedBy(null);
				member.setModifiedDate(LocalDateTime.now());
				member.setModifiedBy(null);

				keycloakService.createUser(member.getId(), member.getEmailId(), member.getFirstName(),
						member.getLastName(), defaultPassword, member.getRole());

				memberRepo.save(member);
			} else {
				System.out.println("Member 'tsk_001' already exists.");
			}

		} catch (Exception e) {
			throw new AppException("Error for admin creation : " + e.getMessage());
		}

	}

}
