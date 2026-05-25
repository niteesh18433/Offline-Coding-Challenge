# Task Management API - Test Execution Guide

## Quick Reference

### Test Files Location
```
src/
├── test/
│   ├── java/
│   │   └── com/example/taskmanagement/
│   │       ├── service/
│   │       │   └── TaskServiceTest.java           (15 tests)
│   │       ├── controller/
│   │       │   └── TaskControllerTest.java        (27 tests)
│   │       └── TaskIntegrationTest.java           (15 tests)
│   └── resources/
│       └── application-test.properties
```

---

## Running Tests

### Using Maven

#### Run all tests
```bash
mvn test
```

#### Run single test class
```bash
mvn test -Dtest=TaskServiceTest
mvn test -Dtest=TaskControllerTest
mvn test -Dtest=TaskIntegrationTest
```

#### Run specific test method
```bash
mvn test -Dtest=TaskServiceTest#createTask_success
```

#### Run tests with detailed output
```bash
mvn test -X
```

#### Generate test report
```bash
mvn surefire-report:report
```

### Using IDE (IntelliJ, Eclipse, VS Code)

#### Run All Tests
- Right-click on test folder → "Run Tests" or "Run with Coverage"

#### Run Single Test Class
- Right-click on test file → "Run" or "Run with Coverage"

#### Run Single Test Method
- Right-click on test method → "Run"

---

## Test Structure (AAA Pattern)

All tests follow the **Arrange-Act-Assert** (AAA) pattern:

```java
@Test
@DisplayName("Should successfully create a task")
void createTask_success() {
    // Arrange - Set up test data
    TaskRequest request = TaskRequest.builder()
        .title("Test Title")
        .dueDate(LocalDate.now().plusDays(7))
        .build();
    
    when(taskRepository.save(any())).thenReturn(testEntity);
    when(taskMapper.toDto(testEntity)).thenReturn(testResponse);
    
    // Act - Execute the operation
    TaskResponse result = taskService.createTask(request);
    
    // Assert - Verify the results
    assertThat(result).isNotNull();
    assertThat(result.getTitle()).isEqualTo("Test Title");
    
    verify(taskRepository, times(1)).save(any());
}
```

---

## Test Types Explanation

### 1. Unit Tests (TaskServiceTest)
- **Type**: Service layer unit tests
- **Mocking**: Repository and Mapper layers
- **Focus**: Business logic validation
- **Speed**: Fast (no database)
- **Isolation**: Service is tested in isolation

**Example**:
```java
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock private TaskRepository taskRepository;
    @InjectMocks private TaskServiceImpl taskService;
    
    @Test
    void createTask_success() { ... }
}
```

### 2. Controller Tests (TaskControllerTest)
- **Type**: Controller/Web layer tests
- **Testing**: REST endpoints, HTTP status codes, response format
- **Mocking**: Service layer
- **Tool**: MockMvc for HTTP testing
- **Speed**: Medium (no database)

**Example**:
```java
@WebMvcTest(TaskController.class)
class TaskControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private TaskService taskService;
    
    @Test
    void createTask_validRequest_returns201() throws Exception {
        mockMvc.perform(post("/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(...))
            .andExpect(status().isCreated());
    }
}
```

### 3. Integration Tests (TaskIntegrationTest)
- **Type**: End-to-end integration tests
- **Testing**: Complete API flow with real database
- **Database**: H2 in-memory for fast execution
- **Scope**: All layers integrated
- **Speed**: Medium (database I/O)

**Example**:
```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private TaskRepository taskRepository;
    
    @Test
    void completeApiFlow_success() throws Exception {
        // Create, get, update, delete
    }
}
```

---

## Test Scenarios Covered

### CRUD Operations
| Operation | Unit Test | Controller Test | Integration Test |
|-----------|-----------|-----------------|------------------|
| Create (success) | ✅ | ✅ | ✅ |
| Create (invalid) | ✅ | ✅ | ✅ |
| Read (found) | ✅ | ✅ | ✅ |
| Read (not found) | ✅ | ✅ | ✅ |
| Update (success) | ✅ | ✅ | ✅ |
| Update (invalid) | ✅ | ✅ | ✅ |
| Delete (success) | ✅ | ✅ | ✅ |
| Delete (not found) | ✅ | ✅ | ✅ |
| List (all) | ✅ | ✅ | ✅ |
| Filter (by status) | ✅ | ✅ | ✅ |
| Pagination | ✅ | ✅ | ✅ |

### Validation Scenarios
- Null/blank title
- Past due dates
- Missing required fields
- Invalid request format

---

## Understanding Test Failures

### Service Test Failure
```
TaskServiceTest.createTask_success FAILED
AssertionError: expected null but was "task-123"
```
**Cause**: Mock configuration issue  
**Fix**: Check `when()` statement in test setup

### Controller Test Failure
```
TaskControllerTest.createTask_validRequest_returns201 FAILED
Expected status 201 but was 400
```
**Cause**: Validation error  
**Fix**: Verify request validation or check GlobalExceptionHandler

### Integration Test Failure
```
TaskIntegrationTest.createTask_success FAILED
Actual database count is 0
```
**Cause**: Transaction not committed  
**Fix**: Check @Transactional and database configuration

---

## Test Configuration

### application-test.properties
Located at: `src/test/resources/application-test.properties`

```properties
# H2 in-memory database (recreated for each test)
spring.datasource.url=jdbc:h2:mem:testdb

# Hibernate DDL auto
spring.jpa.hibernate.ddl-auto=create-drop

# Disable SQL logging for cleaner output
spring.jpa.show-sql=false
```

### Test Profile Activation
```java
@SpringBootTest
@ActiveProfiles("test")  // Uses application-test.properties
class TaskIntegrationTest { }
```

---

## Mock Objects in Tests

### Mockito Annotations
```java
@Mock               // Create a mock object
@InjectMocks       // Inject mocks into service
@MockBean          // Spring mock bean for integration tests
```

### Common Mocking Patterns

**Setup return value**:
```java
when(taskRepository.findById("123"))
    .thenReturn(Optional.of(taskEntity));
```

**Setup exception**:
```java
when(taskRepository.findById("invalid"))
    .thenThrow(new TaskNotFoundException("Not found"));
```

**Verify invocation**:
```java
verify(taskRepository, times(1)).save(any(Task.class));
```

**Verify no invocation**:
```java
verify(taskService, never()).deleteTask(any());
```

---

## Writing New Tests (Template)

### Service Test Template
```java
@ExtendWith(MockitoExtension.class)
@DisplayName("Describe what you're testing")
class NewServiceTest {
    
    @Mock
    private DependencyA dependencyA;
    
    @InjectMocks
    private ServiceUnderTest serviceUnderTest;
    
    @Test
    @DisplayName("Should describe expected behavior")
    void testMethod_condition_expectedResult() {
        // Arrange
        
        // Act
        
        // Assert
    }
}
```

### Controller Test Template
```java
@WebMvcTest(ControllerUnderTest.class)
@DisplayName("Describe API endpoint testing")
class NewControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ServiceDependency serviceDependency;
    
    @Test
    @DisplayName("Should return expected status and response")
    void testEndpoint_scenario_expectedStatus() throws Exception {
        // Arrange
        when(serviceDependency.method()).thenReturn(...);
        
        // Act & Assert
        mockMvc.perform(post("/endpoint")
                .contentType(MediaType.APPLICATION_JSON)
                .content(...))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.field").exists());
    }
}
```

### Integration Test Template
```java
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Describe complete workflow testing")
class NewIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private Repository repository;
    
    @Test
    @DisplayName("Should complete full workflow")
    void testCompleteFlow_scenario_success() throws Exception {
        // Arrange
        
        // Act - perform HTTP request
        mockMvc.perform(post("/endpoint")
                .contentType(MediaType.APPLICATION_JSON)
                .content(...))
            .andExpect(status().isCreated());
        
        // Assert - verify database state
        assertThat(repository.count()).isEqualTo(1);
    }
}
```

---

## Assertion Examples

### AssertJ Assertions
```java
// Basic assertions
assertThat(value).isNotNull();
assertThat(value).isEqualTo(expected);
assertThat(value).isBetween(1, 10);

// String assertions
assertThat(title).startsWith("Test");
assertThat(title).contains("API");

// Collection assertions
assertThat(list).hasSize(5);
assertThat(list).contains("item1", "item2");
assertThat(list).isEmpty();

// Object assertions
assertThat(response)
    .hasFieldOrPropertyWithValue("status", "PENDING")
    .extracting("title")
    .isEqualTo("Test");
```

### MockMvc Response Assertions
```java
.andExpect(status().isOk())
.andExpect(status().isCreated())
.andExpect(status().isNoContent())
.andExpect(status().isBadRequest())
.andExpect(status().isNotFound())

.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id").exists())
.andExpect(jsonPath("$.title").value("Test"))
.andExpect(jsonPath("$.status").value("PENDING"))

.andExpect(header().exists("Location"))
.andExpect(header().string("Content-Type", containsString("json")))
```

---

## Debugging Tests

### Enable Verbose Output
```java
@Test
void debugTest() {
    System.out.println("Debug message: " + value);
    // Test code...
}
```

### Add Breakpoints in IDE
1. Click on line number in test file
2. Debug test (right-click → Debug)
3. Step through execution

### View HTTP Requests/Responses
```java
mockMvc.perform(post("/tasks")...)
    .andDo(print())  // Print full request/response
    .andExpect(...);
```

### Check Database State
```java
List<Task> allTasks = repository.findAll();
System.out.println("Count: " + allTasks.size());
allTasks.forEach(task -> System.out.println(task));
```

---

## Test Performance Tuning

### Key Statistics
```
Unit Tests (TaskServiceTest):    ~5-10ms per test  (Total: ~100ms)
Controller Tests (TaskControllerTest): ~20-50ms per test  (Total: ~500ms)
Integration Tests (TaskIntegrationTest): ~50-200ms per test (Total: ~3s)

Total estimated run time: ~4 seconds
```

### Optimization Tips
1. Use `@WebMvcTest` instead of `@SpringBootTest` for controller tests
2. Mock external dependencies in unit tests
3. Use in-memory H2 for integration tests
4. Minimize database fixtures
5. Disable unnecessary logging in tests

---

## Continuous Integration

### GitHub Actions Example
```yaml
name: Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: '17'
      - run: mvn clean test
```

### GitLab CI Example
```yaml
test:
  stage: test
  script:
    - mvn clean test
  coverage: '/Coverage: \d+\.\d+%/'
```

---

## Additional Resources

- [JUnit 5 Documentation](https://junit.org/junit5/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core)
- [AssertJ Documentation](https://assertj.github.io/assertj-core.html)
- [Spring Test Documentation](https://spring.io/guides/gs/testing-web/)
- [H2 Database Documentation](https://www.h2database.com/)

---

## Support

For issues or questions about tests:
1. Check test class JavaDoc comments
2. Review TEST_SUMMARY.md for detailed descriptions
3. Check test method @DisplayName for expected behavior
4. Review test code comments in Arrange/Act/Assert sections

