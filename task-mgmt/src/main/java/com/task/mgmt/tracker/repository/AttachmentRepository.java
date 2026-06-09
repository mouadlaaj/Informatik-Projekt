package com.task.mgmt.tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.task.mgmt.tracker.entity.Attachment;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, String>  {

}
