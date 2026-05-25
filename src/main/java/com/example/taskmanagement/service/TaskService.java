package com.example.taskmanagement.service;

import com.example.taskmanagement.domain.model.TaskStatus;
import com.example.taskmanagement.dto.TaskRequest;
import com.example.taskmanagement.dto.TaskResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {

    TaskResponse createTask(TaskRequest request);

    TaskResponse getTaskById(String id);

    TaskResponse updateTask(String id, TaskRequest request);

    void deleteTask(String id);

    Page<TaskResponse> listTasks(TaskStatus status, Pageable pageable);

}

