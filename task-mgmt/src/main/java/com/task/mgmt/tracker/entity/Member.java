package com.task.mgmt.tracker.entity;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "members")
public class Member {

    @Id
    @Column(length = 100)
    private String id;

    @Column(name = "first_name",length = 15)
    private String firstName; 

    @Column(name = "last_name",length = 15)
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;
    
    @JsonIgnore
    @Column(name = "password")
    private String password;

    @Column(name = "email_id")
    private String emailId;

    @Column(name = "role")
    private String role;

    @Column(name = "designation",length = 30)
    private String designation;

    @Column(name = "status")
    private String status;

    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @Column(name = "joining_date")
    private LocalDate joiningDate;
    
    @Column(name = "relieving_date")
    private LocalDateTime relievingDate;
    
    @Column(name = "years_of_experience")
    private float yearsOfExperience;
    
    @Column(name = "dob")
    private LocalDate dob;
    
    @Column(name = "gender")
    private String gender;
    
    @Column(name = "address", length = 3000)
    private String address;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "created_by")
    private Member createdBy;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "modified_by")
    private Member modifiedBy;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDate getJoiningDate() {
		return joiningDate;
	}

	public void setJoiningDate(LocalDate joiningDate) {
		this.joiningDate = joiningDate;
	}

	public LocalDateTime getRelievingDate() {
		return relievingDate;
	}

	public void setRelievingDate(LocalDateTime relievingDate) {
		this.relievingDate = relievingDate;
	}

	public float getYearsOfExperience() {
		return yearsOfExperience;
	}

	public void setYearsOfExperience(float yearsOfExperience) {
		this.yearsOfExperience = yearsOfExperience;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Member getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Member createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(LocalDateTime modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Member getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Member modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
    
//    @OneToMany(mappedBy = "trainer")
//    private List<TrainingEvent> trainings = new ArrayList<>();
    
    
    

}
