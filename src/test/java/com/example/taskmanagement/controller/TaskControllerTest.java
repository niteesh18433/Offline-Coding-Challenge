package com.example.taskmanagement.controller;

import com.example.taskmanagement.domain.model.TaskStatus;
import com.example.taskmanagement.dto.TaskRequest;
import com.example.taskmanagement.dto.TaskResponse;
import com.example.taskmanagement.exception.TaskNotFoundException;
import com.example.taskmanagement.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@DisplayName("TaskController Unit Tests")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskRequest validTaskRequest;
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

        taskResponse = TaskResponse.builder()
                .id("task-123")
                .title("Complete project")
                .description("Finish the task management API")
                .status(TaskStatus.PENDING)
                .dueDate(futureDate)
                .build();
    }

    // POST /tasks Tests

    @Test
    @DisplayName("POST /tasks should create task and return 201 Created")
    void createTask_validRequest_returns201() throws Exception {
        // Arrange
        when(taskService.createTask(any(TaskRequest.class))).thenReturn(taskResponse);

        // Act & Assert
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTaskRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", is("task-123")))
                .andExpect(jsonPath("$.title", is("Complete project")))
                .andExpect(jsonPath("$.status", is("PENDING")));

        verify(taskService, times(1)).createTask(any(TaskRequest.class));
    }

    @Test
    @DisplayName("POST /tasks should return 400 Bad Request for null title")
    void createTask_nullTitle_returns400() throws Exception {
        // Arrange
        TaskRequest invalidRequest = TaskRequest.builder()
                .title(null)
                .description("Description")
                .dueDate(futureDate)
                .build();

        // Act & Assert
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any());
    }

    @Test
    @DisplayName("POST /tasks should return 400 Bad Request for blank title")
    void createTask_blankTitle_returns400() throws Exception {
        // Arrange
        TaskRequest invalidRequest = TaskRequest.builder()
                .title("  ")
                .description("Description")
                .dueDate(futureDate)
                .build();

        // Act & Assert
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any());
    }

    @Test
    @DisplayName("POST /tasks should return 400 Bad Request for past dueDate")
    void createTask_pastDueDate_returns400() throws Exception {
        // Arrange
        TaskRequest invalidRequest = TaskRequest.builder()
                .title("Complete project")
                .description("Description")
                .dueDate(LocalDate.now().minusDays(1))
                .build();

        // Act & Assert
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any());
    }

    @Test
    @DisplayName("POST /tasks should return 400 Bad Request for null dueDate")
    void createTask_nullDueDate_returns400() throws Exception {
        // Arrange
        TaskRequest invalidRequest = TaskRequest.builder()
                .title("Complete project")
                .description("Description")
                .dueDate(null)
                .build();

        // Act & Assert
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any());
    }

    // GET /tasks/{id} Tests

    @Test
    @DisplayName("GET /tasks/{id} should return 200 Ok with task details")
    void getTaskById_existingTask_returns200() throws Exception {
        // Arrange
        when(taskService.getTaskById("task-123")).thenReturn(taskResponse);

        // Act & Assert
        mockMvc.perform(get("/tasks/task-123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("task-123")))
                .andExpect(jsonPath("$.title", is("Complete project")))
                .andExpect(jsonPath("$.description", is("Finish the task management API")))
                .andExpect(jsonPath("$.status", is("PENDING")));

        verify(taskService, times(1)).getTaskById("task-123");
    }

    @Test
    @DisplayName("GET /tasks/{id} should return 404 Not Found for non-existent task")
    void getTaskById_nonExistentTask_returns404() throws Exception {
        // Arrange
        when(taskService.getTaskById("non-existent"))
                .thenThrow(new TaskNotFoundException("Task not found with id: non-existent"));

        // Act & Assert
        mockMvc.perform(get("/tasks/non-existent")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).getTaskById("non-existent");
    }

    //  PUT /tasks/{id} Tests

    @Test
    @DisplayName("PUT /tasks/{id} should update task and return 200 Ok")
    void updateTask_validRequest_returns200() throws Exception {
        // Arrange
        TaskRequest updateRequest = TaskRequest.builder()
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

        when(taskService.updateTask(eq("task-123"), any(TaskRequest.class))).thenReturn(updatedResponse);

        // Act & Assert
        mockMvc.perform(put("/tasks/task-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("task-123")))
                .andExpect(jsonPath("$.title", is("Updated project")))
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")));

        verify(taskService, times(1)).updateTask(eq("task-123"), any(TaskRequest.class));
    }

    @Test
    @DisplayName("PUT /tasks/{id} should return 404 Not Found for non-existent task")
    void updateTask_nonExistentTask_returns404() throws Exception {
        // Arrange
        when(taskService.updateTask(eq("non-existent"), any(TaskRequest.class)))
                .thenThrow(new TaskNotFoundException("Task not found with id: non-existent"));

        // Act & Assert
        mockMvc.perform(put("/tasks/non-existent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTaskRequest)))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).updateTask(eq("non-existent"), any(TaskRequest.class));
    }

    @Test
    @DisplayName("PUT /tasks/{id} should return 400 Bad Request for invalid data")
    void updateTask_invalidRequest_returns400() throws Exception {
        // Arrange
        TaskRequest invalidRequest = TaskRequest.builder()
                .title("")
                .description("Description")
                .dueDate(futureDate)
                .build();

        // Act & Assert
        mockMvc.perform(put("/tasks/task-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).updateTask(any(), any());
    }

    // DELETE /tasks/{id} Tests

    @Test
    @DisplayName("DELETE /tasks/{id} should return 204 No Content")
    void deleteTask_existingTask_returns204() throws Exception {
        // Arrange
        doNothing().when(taskService).deleteTask("task-123");

        // Act & Assert
        mockMvc.perform(delete("/tasks/task-123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask("task-123");
    }

    @Test
    @DisplayName("DELETE /tasks/{id} should return 404 Not Found for non-existent task")
    void deleteTask_nonExistentTask_returns404() throws Exception {
        // Arrange
        doThrow(new TaskNotFoundException("Task not found with id: non-existent"))
                .when(taskService).deleteTask("non-existent");

        // Act & Assert
        mockMvc.perform(delete("/tasks/non-existent")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).deleteTask("non-existent");
    }

    //  GET /tasks (List) Tests

    @Test
    @DisplayName("GET /tasks should return 200 Ok with paginated task list")
    void listTasks_withoutFilter_returns200() throws Exception {
        // Arrange
        Page<TaskResponse> taskPage = new PageImpl<>(Arrays.asList(taskResponse));
        when(taskService.listTasks(isNull(), any())).thenReturn(taskPage);

        // Act & Assert
        mockMvc.perform(get("/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is("task-123")))
                .andExpect(jsonPath("$.content[0].title", is("Complete project")))
                .andExpect(jsonPath("$.totalElements", is(1)));

        verify(taskService, times(1)).listTasks(isNull(), any());
    }

    @Test
    @DisplayName("GET /tasks should filter by status when provided")
    void listTasks_withStatusFilter_returns200() throws Exception {
        // Arrange
        Page<TaskResponse> taskPage = new PageImpl<>(Arrays.asList(taskResponse));
        when(taskService.listTasks(eq(TaskStatus.PENDING), any())).thenReturn(taskPage);

        // Act & Assert
        mockMvc.perform(get("/tasks")
                        .param("status", "PENDING")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].status", is("PENDING")));

        verify(taskService, times(1)).listTasks(eq(TaskStatus.PENDING), any());
    }

    @Test
    @DisplayName("GET /tasks should return empty list when no tasks exist")
    void listTasks_emptyList_returns200() throws Exception {

        Page<TaskResponse> emptyPage = new PageImpl<>(Arrays.asList());
        when(taskService.listTasks(isNull(), any())).thenReturn(emptyPage);


        mockMvc.perform(get("/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));

        verify(taskService, times(1)).listTasks(isNull(), any());
    }

    @Test
    @DisplayName("GET /tasks should support pagination")
    void listTasks_withPagination_returns200() throws Exception {
        Page<TaskResponse> taskPage = new PageImpl<>(Arrays.asList(taskResponse), PageRequest.of(0, 10), 1);
        when(taskService.listTasks(isNull(), any())).thenReturn(taskPage);

        mockMvc.perform(get("/tasks")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements", is(1)));

        verify(taskService, times(1)).listTasks(isNull(), any());
    }

    @Test
    @DisplayName("GET /tasks should support sorting")
    void listTasks_withSorting_returns200() throws Exception {
        Page<TaskResponse> taskPage = new PageImpl<>(Arrays.asList(taskResponse));
        when(taskService.listTasks(isNull(), any())).thenReturn(taskPage);
        mockMvc.perform(get("/tasks")
                        .param("sort", "dueDate,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(taskService, times(1)).listTasks(isNull(), any());
    }
}
