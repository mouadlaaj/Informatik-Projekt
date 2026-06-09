package com.task.mgmt.tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.task.mgmt.tracker.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {

	boolean existsByProjectName(String projectName);

	List<Project> findAllByStatusInOrderByCreatedDateDesc(List<String> status);

	Project findByIdAndStatus(String projectId, String status);

	Project findByIdAndTeamsIdIn(String projectId, List<String> teams);

	Project findByIdAndTagsNameIn(String projectId, List<String> tags);

	List<Project> findAllByCreatedById(String adminId);
	
	List<Project> findAllByTeamsMembersId(String adminId);
}
