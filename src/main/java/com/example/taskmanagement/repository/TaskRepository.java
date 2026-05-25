package com.example.taskmanagement.repository;

import com.example.taskmanagement.domain.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
	// Find tasks by status with pagination
	org.springframework.data.domain.Page<com.example.taskmanagement.domain.model.Task> findByStatus(com.example.taskmanagement.domain.model.TaskStatus status, org.springframework.data.domain.Pageable pageable);

}


