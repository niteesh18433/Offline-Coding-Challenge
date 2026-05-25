package com.example.taskmanagement.mapper;

import com.example.taskmanagement.domain.model.Task;
import com.example.taskmanagement.dto.TaskRequest;
import com.example.taskmanagement.dto.TaskResponse;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task toEntity(TaskRequest dto) {
        if (dto == null) return null;
        return Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .dueDate(dto.getDueDate())
                .build();
    }

    public TaskResponse toDto(Task entity) {
        if (entity == null) return null;
        return TaskResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .dueDate(entity.getDueDate())
                .build();
    }
}


