package com.example.taskmanagement.dto;

import com.example.taskmanagement.domain.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponseDto {
    private String id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDate dueDate;
}

