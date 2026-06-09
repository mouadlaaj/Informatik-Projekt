package com.task.mgmt.tracker.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.task.mgmt.tracker.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, String>{
	
	Tag findByName(String tagName);

	List<Tag> findAllByNameIn(Set<String> newTagNames);
}
