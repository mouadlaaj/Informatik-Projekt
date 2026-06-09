package com.task.mgmt.tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.task.mgmt.tracker.entity.Comments;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, String> {

	List<Comments> findAllByOrderByCreatedDateDesc();

//	List<Comments> findByTaskIdOrderByCreatedDateDesc(String taskId);

}
