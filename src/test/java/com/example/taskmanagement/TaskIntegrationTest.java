package com.example.taskmanagement;

import com.example.taskmanagement.domain.model.TaskStatus;
import com.example.taskmanagement.dto.TaskRequest;
import com.example.taskmanagement.dto.TaskResponse;
import com.example.taskmanagement.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Task Management API Integration Tests")
@ActiveProfiles("test")
@Transactional
class TaskIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    private LocalDate futureDate;

    @BeforeEach
    void setUp() {
        futureDate = LocalDate.now().plusDays(7);
        taskRepository.deleteAll();
    }

    // ==================== Complete API Flow Tests ====================

    @Test
    @DisplayName("Complete API flow: create -> get -> update -> delete")
    void completeApiFlow_success() throws Exception {
        // Step 1: Create a task
        TaskRequest createRequest = TaskRequest.builder()
                .title("Complete API test")
                .description("Test full API workflow")
                .status(TaskStatus.PENDING)
                .dueDate(futureDate)
                .build();

        MvcResult createResult = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Complete API test\",\"description\":\"Test full API workflow\",\"status\":\"PENDING\",\"dueDate\":\"" + futureDate + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title", is("Complete API test")))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        String taskId = extractTaskIdFromJson(responseBody);

        // Verify task was saved to database
        assertThat(taskRepository.findById(taskId)).isPresent();

        // Step 2: Get the created task
        mockMvc.perform(get("/tasks/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(taskId)))
                .andExpect(jsonPath("$.title", is("Complete API test")))
                .andExpect(jsonPath("$.description", is("Test full API workflow")))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.dueDate", is(futureDate.toString())));

        // Step 3: Update the task
        LocalDate updatedDueDate = futureDate.plusDays(5);
        MvcResult updateResult = mockMvc.perform(put("/tasks/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated API test\",\"description\":\"Updated workflow\",\"status\":\"IN_PROGRESS\",\"dueDate\":\"" + updatedDueDate + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(taskId)))
                .andExpect(jsonPath("$.title", is("Updated API test")))
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")))
                .andExpect(jsonPath("$.dueDate", is(updatedDueDate.toString())))
                .andReturn();

        // Verify update in database
        assertThat(taskRepository.findById(taskId))
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("title", "Updated API test")
                .hasFieldOrPropertyWithValue("status", TaskStatus.IN_PROGRESS);

        // Step 4: Delete the task
        mockMvc.perform(delete("/tasks/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verify task was deleted from database
        assertThat(taskRepository.findById(taskId)).isEmpty();
    }

    // ==================== Create Task Tests ====================

    @Test
    @DisplayName("Should create task successfully")
    void createTask_success() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New Task\",\"description\":\"Task description\",\"status\":\"PENDING\",\"dueDate\":\"" + futureDate + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title", is("New Task")))
                .andExpect(jsonPath("$.status", is("PENDING")));

        // Verify in database
        assertThat(taskRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should fail to create task with missing title")
    void createTask_missingTitle_fails() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Task description\",\"status\":\"PENDING\",\"dueDate\":\"" + futureDate + "\"}"))
                .andExpect(status().isBadRequest());

        // Verify nothing was saved
        assertThat(taskRepository.count()).isZero();
    }

    @Test
    @DisplayName("Should fail to create task with past dueDate")
    void createTask_pastDueDate_fails() throws Exception {
        // Arrange
        LocalDate pastDate = LocalDate.now().minusDays(1);

        // Act & Assert
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New Task\",\"description\":\"Task description\",\"dueDate\":\"" + pastDate + "\"}"))
                .andExpect(status().isBadRequest());

        // Verify nothing was saved
        assertThat(taskRepository.count()).isZero();
    }

    @Test
    @DisplayName("Should fail to create task with missing dueDate")
    void createTask_missingDueDate_fails() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New Task\",\"description\":\"Task description\",\"status\":\"PENDING\"}"))
                .andExpect(status().isBadRequest());

        // Verify nothing was saved
        assertThat(taskRepository.count()).isZero();
    }

    // ==================== Get Task Tests ====================

    @Test
    @DisplayName("Should retrieve existing task")
    void getTask_exists_success() throws Exception {
        // Arrange
        String taskId = createTaskViaRepository("Test Task", "Description", TaskStatus.PENDING);

        // Act & Assert
        mockMvc.perform(get("/tasks/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(taskId)))
                .andExpect(jsonPath("$.title", is("Test Task")))
                .andExpect(jsonPath("$.status", is("PENDING")));
    }

    @Test
    @DisplayName("Should fail to retrieve non-existent task")
    void getTask_notExists_fails() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/tasks/non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // ==================== Update Task Tests ====================

    @Test
    @DisplayName("Should update task successfully")
    void updateTask_success() throws Exception {
        // Arrange
        String taskId = createTaskViaRepository("Original Title", "Original Description", TaskStatus.PENDING);
        LocalDate newDueDate = futureDate.plusDays(3);

        // Act & Assert
        mockMvc.perform(put("/tasks/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated Title\",\"description\":\"Updated Description\",\"status\":\"IN_PROGRESS\",\"dueDate\":\"" + newDueDate + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(taskId)))
                .andExpect(jsonPath("$.title", is("Updated Title")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")));

        // Verify update in database
        assertThat(taskRepository.findById(taskId))
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("title", "Updated Title")
                .hasFieldOrPropertyWithValue("status", TaskStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("Should fail to update non-existent task")
    void updateTask_notExists_fails() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/tasks/non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated Title\",\"description\":\"Updated Description\",\"dueDate\":\"" + futureDate + "\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should fail to update task with invalid data")
    void updateTask_invalidData_fails() throws Exception {
        // Arrange
        String taskId = createTaskViaRepository("Original Title", "Original Description", TaskStatus.PENDING);

        // Act & Assert
        mockMvc.perform(put("/tasks/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"dueDate\":\"" + futureDate + "\"}"))
                .andExpect(status().isBadRequest());
    }

    // ==================== Delete Task Tests ====================

    @Test
    @DisplayName("Should delete task successfully")
    void deleteTask_success() throws Exception {
        // Arrange
        String taskId = createTaskViaRepository("Task to Delete", "Description", TaskStatus.PENDING);
        assertThat(taskRepository.existsById(taskId)).isTrue();

        // Act & Assert
        mockMvc.perform(delete("/tasks/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verify deletion in database
        assertThat(taskRepository.existsById(taskId)).isFalse();
    }

    @Test
    @DisplayName("Should fail to delete non-existent task")
    void deleteTask_notExists_fails() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/tasks/non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // ==================== List Tasks Tests ====================

    @Test
    @DisplayName("Should list all tasks with pagination")
    void listTasks_withoutFilter_success() throws Exception {
        // Arrange
        createTaskViaRepository("Task 1", "Description 1", TaskStatus.PENDING);
        createTaskViaRepository("Task 2", "Description 2", TaskStatus.IN_PROGRESS);
        createTaskViaRepository("Task 3", "Description 3", TaskStatus.DONE);

        // Act & Assert
        mockMvc.perform(get("/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$.totalElements", is(3)))
                .andExpect(jsonPath("$.content[*].id", hasItems(notNullValue())));
    }

    @Test
    @DisplayName("Should list tasks filtered by PENDING status")
    void listTasks_filterByPending_success() throws Exception {
        // Arrange
        createTaskViaRepository("Pending Task 1", "Description 1", TaskStatus.PENDING);
        createTaskViaRepository("Pending Task 2", "Description 2", TaskStatus.PENDING);
        createTaskViaRepository("Done Task", "Description 3", TaskStatus.DONE);

        // Act & Assert
        mockMvc.perform(get("/tasks")
                        .param("status", "PENDING")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].status", everyItem(is("PENDING"))));
    }

    @Test
    @DisplayName("Should list tasks filtered by IN_PROGRESS status")
    void listTasks_filterByInProgress_success() throws Exception {
        // Arrange
        createTaskViaRepository("In Progress Task", "Description 1", TaskStatus.IN_PROGRESS);
        createTaskViaRepository("Pending Task", "Description 2", TaskStatus.PENDING);

        // Act & Assert
        mockMvc.perform(get("/tasks")
                        .param("status", "IN_PROGRESS")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].status", is("IN_PROGRESS")));
    }

    @Test
    @DisplayName("Should list tasks filtered by DONE status")
    void listTasks_filterByDone_success() throws Exception {
        // Arrange
        createTaskViaRepository("Done Task", "Description 1", TaskStatus.DONE);
        createTaskViaRepository("In Progress Task", "Description 2", TaskStatus.IN_PROGRESS);

        // Act & Assert
        mockMvc.perform(get("/tasks")
                        .param("status", "DONE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].status", is("DONE")));
    }

    @Test
    @DisplayName("Should return empty list when no tasks exist")
    void listTasks_empty_success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));
    }

    @Test
    @DisplayName("Should respect pagination parameters")
    void listTasks_withPagination_success() throws Exception {
        // Arrange
        for (int i = 1; i <= 15; i++) {
            createTaskViaRepository("Task " + i, "Description " + i, TaskStatus.PENDING);
        }

        // Act & Assert
        mockMvc.perform(get("/tasks")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.totalElements", is(15)))
                .andExpect(jsonPath("$.totalPages", is(3)));
    }

    @Test
    @DisplayName("Should support sorting by dueDate")
    void listTasks_sortedByDueDate_success() throws Exception {
        // Arrange
        createTaskViaRepository("Task 1", "Description 1", TaskStatus.PENDING, futureDate.plusDays(10));
        createTaskViaRepository("Task 2", "Description 2", TaskStatus.PENDING, futureDate.plusDays(5));
        createTaskViaRepository("Task 3", "Description 3", TaskStatus.PENDING, futureDate);

        // Act & Assert
        mockMvc.perform(get("/tasks")
                        .param("sort", "dueDate,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].title", is("Task 3")))
                .andExpect(jsonPath("$.content[1].title", is("Task 2")))
                .andExpect(jsonPath("$.content[2].title", is("Task 1")));
    }

    // ==================== Helper Methods ====================

    private String createTaskViaRepository(String title, String description, TaskStatus status) {
        return createTaskViaRepository(title, description, status, futureDate);
    }

    private String createTaskViaRepository(String title, String description, TaskStatus status, LocalDate dueDate) {
        com.example.taskmanagement.domain.model.Task task = com.example.taskmanagement.domain.model.Task.builder()
                .title(title)
                .description(description)
                .status(status)
                .dueDate(dueDate)
                .build();
        task.prePersist(); // Generate UUID
        return taskRepository.save(task).getId();
    }

    private String extractTaskIdFromJson(String json) {
        // Simple JSON parsing to extract id
        int idIndex = json.indexOf("\"id\"");
        int colonIndex = json.indexOf(":", idIndex);
        int quoteStart = json.indexOf("\"", colonIndex);
        int quoteEnd = json.indexOf("\"", quoteStart + 1);
        return json.substring(quoteStart + 1, quoteEnd);
    }
}

