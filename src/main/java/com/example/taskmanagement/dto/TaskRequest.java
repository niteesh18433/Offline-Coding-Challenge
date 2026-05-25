package com.example.taskmanagement.dto;

import com.example.taskmanagement.domain.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequest {

    @NotBlank(message = "title must not be blank")
    private String title;

    private String description;

    private TaskStatus status;

    @Future(message = "dueDate must be in future")
    @NotNull(message = "dueDate is required")
    private LocalDate dueDate;

}

