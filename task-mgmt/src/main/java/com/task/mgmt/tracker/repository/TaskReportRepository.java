package com.task.mgmt.tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.task.mgmt.tracker.entity.TaskReport;

@Repository
public interface TaskReportRepository extends JpaRepository<TaskReport, Long> {


	List<TaskReport> findAllByTaskIdIn(List<String> listOfTaskIds);



}
