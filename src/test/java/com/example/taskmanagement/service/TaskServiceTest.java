package com.example.taskmanagement.service;

import com.example.taskmanagement.domain.model.Task;
import com.example.taskmanagement.domain.model.TaskStatus;
import com.example.taskmanagement.dto.TaskRequest;
import com.example.taskmanagement.dto.TaskResponse;
import com.example.taskmanagement.exception.TaskNotFoundException;
import com.example.taskmanagement.mapper.TaskMapper;
import com.example.taskmanagement.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService Unit Tests")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private TaskRequest validTaskRequest;
    private Task taskEntity;
    private TaskResponse taskResponse;
    private LocalDate futureDate;

    @BeforeEach
    void setUp() {
        futureDate = LocalDate.now().plusDays(7);

        validTaskRequest = TaskRequest.builder()
                .title("Complete project")
                .description("Finish the task management API")
                .status(TaskStatus.PENDING)
                .dueDate(futureDate)
                .build();

        taskEntity = Task.builder()
                .id("task-123")
                .title("Complete project")
                .description("Finish the task management API")
                .status(TaskStatus.PENDING)
                .dueDate(futureDate)
                .build();

        taskResponse = TaskResponse.builder()
                .id("task-123")
                .title("Complete project")
                .description("Finish the task management API")
                .status(TaskStatus.PENDING)
                .dueDate(futureDate)
                .build();
    }

    @Test
    @DisplayName("Should successfully create a task")
    void createTask_success() {
        // Arrange
        when(taskMapper.toEntity(validTaskRequest)).thenReturn(taskEntity);
        when(taskRepository.save(any(Task.class))).thenReturn(taskEntity);
        when(taskMapper.toDto(taskEntity)).thenReturn(taskResponse);

        // Act
        TaskResponse result = taskService.createTask(validTaskRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("task-123");
        assertThat(result.getTitle()).isEqualTo("Complete project");
        assertThat(result.getStatus()).isEqualTo(TaskStatus.PENDING);
        assertThat(result.getDueDate()).isEqualTo(futureDate);

        verify(taskMapper, times(1)).toEntity(validTaskRequest);
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(taskMapper, times(1)).toDto(taskEntity);
    }

    @Test
    @DisplayName("Should throw exception when creating task with null request")
    void createTask_nullRequest_throwsException() {
        // Act & Assert
        assertThatThrownBy(() -> taskService.createTask(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Request must not be null");

        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when creating task with blank title")
    void createTask_blankTitle_throwsException() {
        // Arrange
        TaskRequest invalidRequest = TaskRequest.builder()
                .title("  ")
                .description("Description")
                .dueDate(futureDate)
                .build();

        // Act & Assert
        assertThatThrownBy(() -> taskService.createTask(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("title must not be blank");

        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when creating task with null dueDate")
    void createTask_nullDueDate_throwsException() {
        // Arrange
        TaskRequest invalidRequest = TaskRequest.builder()
                .title("Complete project")
                .description("Description")
                .dueDate(null)
                .build();

        // Act & Assert
        assertThatThrownBy(() -> taskService.createTask(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("dueDate must be provided");

        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when creating task with past dueDate")
    void createTask_pastDueDate_throwsException() {
        // Arrange
        TaskRequest invalidRequest = TaskRequest.builder()
                .title("Complete project")
                .description("Description")
                .dueDate(LocalDate.now().minusDays(1))
                .build();

        // Act & Assert
        assertThatThrownBy(() -> taskService.createTask(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("dueDate must be a future date");

        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should successfully retrieve task by id")
    void getTaskById_success() {
        // Arrange
        when(taskRepository.findById("task-123")).thenReturn(Optional.of(taskEntity));
        when(taskMapper.toDto(taskEntity)).thenReturn(taskResponse);

        // Act
        TaskResponse result = taskService.getTaskById("task-123");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("task-123");
        assertThat(result.getTitle()).isEqualTo("Complete project");

        verify(taskRepository, times(1)).findById("task-123");
        verify(taskMapper, times(1)).toDto(taskEntity);
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException when task not found")
    void getTaskById_notFound_throwsException() {
        // Arrange
        when(taskRepository.findById("non-existent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> taskService.getTaskById("non-existent"))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task not found with id: non-existent");

        verify(taskRepository, times(1)).findById("non-existent");
        verify(taskMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Should successfully update a task")
    void updateTask_success() {
        // Arrange
        TaskRequest updateRequest = TaskRequest.builder()
                .title("Updated project")
                .description("Updated description")
                .status(TaskStatus.IN_PROGRESS)
                .dueDate(futureDate.plusDays(5))
                .build();

        Task updatedEntity = Task.builder()
                .id("task-123")
                .title("Updated project")
                .description("Updated description")
                .status(TaskStatus.IN_PROGRESS)
                .dueDate(futureDate.plusDays(5))
                .build();

        TaskResponse updatedResponse = TaskResponse.builder()
                .id("task-123")
                .title("Updated project")
                .description("Updated description")
                .status(TaskStatus.IN_PROGRESS)
                .dueDate(futureDate.plusDays(5))
                .build();

        when(taskRepository.findById("task-123")).thenReturn(Optional.of(taskEntity));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedEntity);
        when(taskMapper.toDto(updatedEntity)).thenReturn(updatedResponse);

        // Act
        TaskResponse result = taskService.updateTask("task-123", updateRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("task-123");
        assertThat(result.getTitle()).isEqualTo("Updated project");
        assertThat(result.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(result.getDueDate()).isEqualTo(futureDate.plusDays(5));

        verify(taskRepository, times(1)).findById("task-123");
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(taskMapper, times(1)).toDto(updatedEntity);
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException when updating non-existent task")
    void updateTask_notFound_throwsException() {
        // Arrange
        when(taskRepository.findById("non-existent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> taskService.updateTask("non-existent", validTaskRequest))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task not found with id: non-existent");

        verify(taskRepository, times(1)).findById("non-existent");
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when updating task with null request")
    void updateTask_nullRequest_throwsException() {
        // Act & Assert
        assertThatThrownBy(() -> taskService.updateTask("task-123", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Request must not be null");

        verify(taskRepository, never()).findById("task-123");
    }

    @Test
    @DisplayName("Should throw exception when updating task with blank title")
    void updateTask_blankTitle_throwsException() {
        // Arrange
        TaskRequest invalidRequest = TaskRequest.builder()
                .title("")
                .description("Description")
                .dueDate(futureDate)
                .build();

        // Act & Assert
        assertThatThrownBy(() -> taskService.updateTask("task-123", invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("title must not be blank");

        verify(taskRepository, never()).findById("task-123");
    }

    @Test
    @DisplayName("Should successfully delete a task")
    void deleteTask_success() {
        // Arrange
        when(taskRepository.existsById("task-123")).thenReturn(true);

        // Act
        taskService.deleteTask("task-123");

        // Assert
        verify(taskRepository, times(1)).existsById("task-123");
        verify(taskRepository, times(1)).deleteById("task-123");
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException when deleting non-existent task")
    void deleteTask_notFound_throwsException() {
        // Arrange
        when(taskRepository.existsById("non-existent")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> taskService.deleteTask("non-existent"))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task not found with id: non-existent");

        verify(taskRepository, times(1)).existsById("non-existent");
        verify(taskRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should successfully list all tasks with pagination")
    void listTasks_success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(taskEntity));
        Page<TaskResponse> responsePage = new PageImpl<>(Arrays.asList(taskResponse));

        when(taskRepository.findAll(pageable)).thenReturn(taskPage);
        when(taskMapper.toDto(taskEntity)).thenReturn(taskResponse);

        // Act
        Page<TaskResponse> result = taskService.listTasks(null, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo("task-123");
        assertThat(result.getTotalElements()).isEqualTo(1);

        verify(taskRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should successfully list tasks filtered by status")
    void listTasks_withStatus_success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(taskEntity));

        when(taskRepository.findByStatus(TaskStatus.PENDING, pageable)).thenReturn(taskPage);
        when(taskMapper.toDto(taskEntity)).thenReturn(taskResponse);

        // Act
        Page<TaskResponse> result = taskService.listTasks(TaskStatus.PENDING, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(TaskStatus.PENDING);

        verify(taskRepository, times(1)).findByStatus(TaskStatus.PENDING, pageable);
    }

    @Test
    @DisplayName("Should return empty page when no tasks found")
    void listTasks_empty_returnsEmptyPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> emptyPage = new PageImpl<>(Arrays.asList());

        when(taskRepository.findAll(pageable)).thenReturn(emptyPage);

        // Act
        Page<TaskResponse> result = taskService.listTasks(null, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();

        verify(taskRepository, times(1)).findAll(pageable);
    }
}
