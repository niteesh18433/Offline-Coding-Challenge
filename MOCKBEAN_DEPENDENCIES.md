# MockBean Dependencies & Configuration

## Overview

MockBean is a Spring Boot testing annotation that allows you to add mocks to the Spring application context. This document explains all the dependencies required for MockBean to work properly in your test suite.

---

## What is @MockBean?

The `@MockBean` annotation is used in Spring Boot tests to replace a Spring bean with a mock object in the application context. This is particularly useful in:

- **@WebMvcTest** - Testing controllers in isolation with mocked services
- **@SpringBootTest** - Testing with a full application context
- **@DataJpaTest** - Testing repositories in isolation

In your TaskControllerTest, `@MockBean` is used to:
```java
@MockBean
private TaskService taskService;  // Mocked service
```

---

## Dependencies Required for MockBean

### 1. Spring Boot Test Starter
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```
**Purpose**: Meta-dependency that includes all common testing libraries
**Includes**: JUnit 5, Mockito, AssertJ, Spring Test, Hamcrest

### 2. JUnit 5 (Jupiter)
```xml
<!-- API for writing tests -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <scope>test</scope>
</dependency>

<!-- Test engine/runtime -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <scope>test</scope>
</dependency>
```
**Purpose**: Testing framework
**Used for**: `@Test`, `@BeforeEach`, `@DisplayName` annotations

### 3. Mockito Core
```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>
```
**Purpose**: Core mocking framework
**Used for**: `when()`, `verify()`, `thenReturn()` in tests

### 4. Mockito JUnit Jupiter
```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```
**Purpose**: Integration between Mockito and JUnit 5
**Used for**: `@ExtendWith(MockitoExtension.class)` annotation

### 5. Spring Test
```xml
<!-- Spring Boot Test Support -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Core Spring Test Framework -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-test</artifactId>
    <scope>test</scope>
</dependency>
```
**Purpose**: Spring testing infrastructure
**Used for**: 
- `@WebMvcTest` (slice test for controllers)
- `@MockBean` (replace Spring beans)
- `MockMvc` (test HTTP requests)
- `@SpringBootTest` (full context test)

### 6. AssertJ
```xml
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <scope>test</scope>
</dependency>
```
**Purpose**: Fluent assertions library
**Used for**: Clean assertion syntax in tests
**Example**:
```java
assertThat(result).isNotNull().hasFieldOrPropertyWithValue("status", PENDING);
```

### 7. Hamcrest
```xml
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>hamcrest</artifactId>
    <scope>test</scope>
</dependency>
```
**Purpose**: Matcher library for assertions
**Used for**: MockMvc response assertions
**Example**:
```java
.andExpect(jsonPath("$.content", hasSize(1)))
```

---

## How MockBean Works with Dependencies

### Dependency Flow

```
@WebMvcTest (Spring Test)
         ↓
    Loads Application Context (Spring Boot Test)
         ↓
  Creates Test Slice (Controller Layer)
         ↓
  @MockBean annotation detected (Spring Test)
         ↓
  Creates Mock using Mockito (Mockito)
         ↓
  Registers in Application Context
         ↓
  Available for @Autowired injection
         ↓
  Configure with when/verify (Mockito)
```

### Example: How MockBean is Used

```java
@WebMvcTest(TaskController.class)           // Spring Test: Slice test
class TaskControllerTest {
    
    @Autowired
    private MockMvc mockMvc;                 // Spring Test: HTTP testing
    
    @MockBean
    private TaskService taskService;         // Spring Test: Mock bean
    
    @Test
    @DisplayName("Test")                     // JUnit 5: Test annotation
    void testMethod() throws Exception {
        // Configure mock with Mockito
        when(taskService.getTask("123"))     // Mockito: Setup expectation
            .thenReturn(taskResponse);       // Mockito: Return value
        
        // Test HTTP request with MockMvc
        mockMvc.perform(get("/tasks/123"))   // Spring Test: Build HTTP request
            .andExpect(status().isOk())      // Hamcrest: Assert status
            .andExpect(jsonPath("$.id", is("123")));  // Hamcrest: Assert JSON
        
        // Verify mock was called
        verify(taskService).getTask("123");  // Mockito: Verify interaction
    }
}
```

### Dependency Roles

| Dependency | Role | Annotations |
|-----------|------|-------------|
| JUnit 5 | Test execution framework | `@Test`, `@BeforeEach`, `@DisplayName` |
| Mockito | Mock object creation | `when()`, `verify()`, `thenReturn()` |
| Spring Test | Test infrastructure | `@WebMvcTest`, `@MockBean`, `MockMvc` |
| Spring Boot Test | Spring Boot test support | Application context loading |
| AssertJ | Fluent assertions | `assertThat()` |
| Hamcrest | Matcher expressions | `hasSize()`, `is()` |

---

## Complete Dependency List for pom.xml

```xml
<!-- Test (JUnit 5 + Mockito + Spring Test) -->
<!-- Spring Boot Test Starter (includes JUnit 5, Mockito, AssertJ, Spring Test) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- JUnit 5 (Jupiter) -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <scope>test</scope>
</dependency>

<!-- Mockito (for mocking) -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>

<!-- AssertJ (for fluent assertions) -->
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <scope>test</scope>
</dependency>

<!-- Hamcrest (for matchers in MockMvc) -->
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>hamcrest</artifactId>
    <scope>test</scope>
</dependency>

<!-- Spring Test (for @WebMvcTest, MockMvc, @MockBean) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-test</artifactId>
    <scope>test</scope>
</dependency>
```

---

## Maven Build Command

After adding all dependencies, update your project:

```bash
# Download all dependencies
mvn clean install

# Run tests with dependencies
mvn test
```

---

## Verifying Dependencies

Check that all dependencies are downloaded:

```bash
# List all dependencies
mvn dependency:tree

# Filter test dependencies
mvn dependency:tree | grep test
```

### Expected Output
```
[INFO] +- org.springframework.boot:spring-boot-starter-test:jar:3.1.6:test
[INFO] |  +- org.junit.jupiter:junit-jupiter:jar:5.9.2:test
[INFO] |  +- org.mockito:mockito-core:jar:5.2.0:test
[INFO] |  +- org.mockito:mockito-junit-jupiter:jar:5.2.0:test
[INFO] |  +- org.assertj:assertj-core:jar:3.24.1:test
[INFO] |  +- org.hamcrest:hamcrest:jar:2.2:test
[INFO] |  +- org.springframework:spring-test:jar:6.0.11:test
```

---

## Common Issues & Solutions

### Issue 1: MockBean Not Found
**Error**: `Cannot resolve symbol 'MockBean'`

**Solution**: Ensure `spring-boot-test` dependency is present:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-test</artifactId>
    <scope>test</scope>
</dependency>
```

### Issue 2: When/Then Not Working
**Error**: `Cannot resolve symbol 'when'` or `'thenReturn'`

**Solution**: Ensure Mockito dependencies are present:
```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>
```

### Issue 3: @Test Annotation Not Found
**Error**: `Cannot resolve symbol 'Test'`

**Solution**: Ensure JUnit 5 API is present:
```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <scope>test</scope>
</dependency>
```

### Issue 4: Tests Don't Run
**Error**: `No tests found` or tests don't execute

**Solution**: Ensure JUnit 5 Engine is present:
```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <scope>test</scope>
</dependency>
```

### Issue 5: MockMvc Not Available
**Error**: `Cannot resolve symbol 'MockMvc'`

**Solution**: Ensure Spring Test is present:
```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-test</artifactId>
    <scope>test</scope>
</dependency>
```

---

## Best Practices

### 1. Always Use @MockBean with @WebMvcTest or @SpringBootTest
```java
@WebMvcTest(TaskController.class)
class TaskControllerTest {
    
    @MockBean                              // ✅ Use @MockBean
    private TaskService taskService;       // with @WebMvcTest
    
    // NOT @Mock                           // ❌ Don't use @Mock
    // with @WebMvcTest
}
```

### 2. Use Specific Dependencies
Don't rely solely on spring-boot-starter-test:
```xml
✅ Explicit dependencies make it clear what's needed
❌ Implicit transitive dependencies are unclear
```

### 3. Keep Test Scope
Always use `<scope>test</scope>`:
```xml
✅ Keeps test libraries out of production JAR
❌ Including in default scope adds overhead
```

### 4. Version Management
Let Spring Boot manage versions:
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>3.1.6</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

---

## Summary

| Component | Library | Purpose |
|-----------|---------|---------|
| Test Framework | JUnit 5 | Test execution |
| Mocking | Mockito | Create mock objects |
| Spring Testing | Spring Test | Test infrastructure |
| Web Testing | MockMvc | HTTP request testing |
| Assertions | AssertJ | Fluent assertions |
| Matchers | Hamcrest | Response matching |

**All dependencies are now in your pom.xml and ready to use! 🎉**

Run your tests:
```bash
mvn test
```

Or run specific test:
```bash
mvn test -Dtest=TaskControllerTest
```

---

## Reference

- [Spring Boot Testing Documentation](https://spring.io/guides/gs/testing-web/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [AssertJ Documentation](https://assertj.github.io/assertj-core.html)

