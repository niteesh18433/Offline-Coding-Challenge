package com.example.taskmanagement.service;

import com.example.taskmanagement.domain.model.Task;
import com.example.taskmanagement.domain.model.TaskStatus;
import com.example.taskmanagement.dto.TaskRequest;
import com.example.taskmanagement.dto.TaskResponse;
// using fully-qualified exception references below to avoid compilation ordering issues
import com.example.taskmanagement.mapper.TaskMapper;
import com.example.taskmanagement.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public TaskResponse createTask(TaskRequest request) {
        validateRequestForCreate(request);
        Task entity = taskMapper.toEntity(request);
        Task saved = taskRepository.save(entity);
        return taskMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new com.example.taskmanagement.exception.TaskNotFoundException("Task not found with id: " + id));
        return taskMapper.toDto(task);
    }

    @Override
    public TaskResponse updateTask(String id, TaskRequest request) {
        validateRequestForUpdate(request);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new com.example.taskmanagement.exception.TaskNotFoundException("Task not found with id: " + id));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getStatus() != null) task.setStatus(request.getStatus());
        task.setDueDate(request.getDueDate());

        Task updated = taskRepository.save(task);
        return taskMapper.toDto(updated);
    }

    @Override
    public void deleteTask(String id) {
        boolean exists = taskRepository.existsById(id);
        if (!exists) throw new com.example.taskmanagement.exception.TaskNotFoundException("Task not found with id: " + id);
        taskRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> listTasks(TaskStatus status, Pageable pageable) {
        Page<Task> page;
        if (status != null) {
            page = taskRepository.findByStatus(status, pageable);
        } else {
            page = taskRepository.findAll(pageable);
        }
        return page.map(taskMapper::toDto);
    }

    private void validateRequestForCreate(TaskRequest request) {
        if (request == null) throw new IllegalArgumentException("Request must not be null");
        if (request.getTitle() == null || request.getTitle().isBlank())
            throw new IllegalArgumentException("title must not be blank");
        if (request.getDueDate() == null)
            throw new IllegalArgumentException("dueDate must be provided");
        if (!request.getDueDate().isAfter(LocalDate.now()))
            throw new IllegalArgumentException("dueDate must be a future date");
    }

    private void validateRequestForUpdate(TaskRequest request) {
        if (request == null) throw new IllegalArgumentException("Request must not be null");
        if (request.getTitle() == null || request.getTitle().isBlank())
            throw new IllegalArgumentException("title must not be blank");
        if (request.getDueDate() == null)
            throw new IllegalArgumentException("dueDate must be provided");
        if (!request.getDueDate().isAfter(LocalDate.now()))
            throw new IllegalArgumentException("dueDate must be a future date");
    }
}



