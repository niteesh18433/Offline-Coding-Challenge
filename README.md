# Task Management API

A comprehensive REST API for task management built with Spring Boot, following Domain-Driven Design (DDD) architecture principles. This project demonstrates enterprise-level Java development practices with complete test coverage, clean code architecture, and professional API design.

---

## 📋 Project Overview

The Task Management API provides a complete solution for managing tasks with the following capabilities:

- **Create** tasks with title, description, and due date
- **Read** individual tasks or list all tasks with pagination
- **Update** task details and status
- **Delete** tasks from the system
- **Filter** tasks by status (PENDING, IN_PROGRESS, DONE)
- **Sort** tasks by due date
- **Paginate** results for efficient data retrieval
- **Validate** input data with comprehensive constraint checking

### Key Features
✅ Full CRUD operations  
✅ Task status management (PENDING, IN_PROGRESS, DONE)  
✅ Pagination and filtering support  
✅ Comprehensive validation  
✅ Global exception handling  
✅ RESTful API design  
✅ Complete test coverage (72 tests)  
✅ H2 in-memory database  

---

## 🛠️ Tech Stack

### Backend Framework
| Technology | Version | Purpose |
|-----------|---------|---------|
| **Java** | 17 | Core programming language |
| **Spring Boot** | 3.1.6 | REST API and application framework |
| **Spring Data JPA** | 3.1.6 | ORM and database access |
| **Spring Validation** | 3.1.6 | Bean validation |

### Database
| Technology | Purpose |
|-----------|---------|
| **H2 Database** | In-memory relational database |
| **JPA/Hibernate** | Object-relational mapping |

### Testing
| Technology | Purpose |
|-----------|---------|
| **JUnit 5** | Unit testing framework |
| **Mockito** | Mocking library |
| **Spring Test** | Spring integration testing |
| **AssertJ** | Fluent assertions |

### Build & Dependency Management
| Technology | Purpose |
|-----------|---------|
| **Maven** | Build automation and dependency management |
| **Lombok** | Reduce boilerplate code |

---

## 🏗️ Architecture

The project follows **Domain-Driven Design (DDD)** principles with a clean, layered architecture:

```
┌─────────────────────────────────────┐
│      REST API Layer (Controller)    │
│    - HTTP Request/Response Handling │
│    - Request Validation             │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│    Application Layer (Service)      │
│    - Business Logic                 │
│    - Transaction Management         │
│    - Orchestration                  │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│     Domain Layer (Model)            │
│    - Task Entity                    │
│    - TaskStatus Enum                │
│    - Business Rules                 │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│   Infrastructure Layer (Repository) │
│    - Data Access                    │
│    - Database Operations            │
└─────────────────────────────────────┘
```

### Layer Responsibilities

1. **Controller Layer** (`controller/`)
   - Handles HTTP requests and responses
   - Validates input data
   - Returns appropriate HTTP status codes

2. **Service Layer** (`service/`)
   - Implements business logic
   - Manages transactions
   - Orchestrates repository calls
   - Task creation, retrieval, updates

3. **Domain Layer** (`domain/model/`)
   - Defines core entities (Task)
   - Contains domain enums (TaskStatus)
   - Encapsulates business rules
   - Pre-persistence callbacks

4. **Data Access Layer** (`repository/`)
   - Extends JpaRepository
   - Provides database query methods
   - Status-based filtering

5. **DTO Layer** (`dto/`)
   - Task request and response objects
   - Separates API contracts from domain model
   - Input/output data transfer objects

6. **Exception Handling** (`exception/`)
   - Global exception handler
   - Custom exceptions (TaskNotFoundException)
   - Consistent error responses

7. **Mapper** (`mapper/`)
   - Converts between entities and DTOs
   - Maintains separation of concerns

---

## 🚀 How to Run

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Git (for cloning)

### Step 1: Clone the Repository
```bash
git clone https://github.com/yourusername/task-management-api.git
cd task-management-api
```

### Step 2: Build the Project
```bash
mvn clean install
```
This command will:
- Clean previous builds
- Compile source code
- Run all tests
- Package the application

### Step 3: Run the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8081`

### Step 4: Verify the Application
Check that the application is running:
```bash
curl http://localhost:8081/tasks
```

You should receive a response with an empty task list (200 OK).

### Alternative: Run JAR File
```bash
# Build JAR
mvn clean package -DskipTests

# Run JAR
java -jar target/task-management-api-0.0.1-SNAPSHOT.jar
```

---

## 💾 H2 Console

H2 database console is available for development and debugging.

### Access H2 Console
- **URL**: http://localhost:8081/h2-console
- **Driver**: `org.h2.Driver`
- **JDBC URL**: `jdbc:h2:mem:taskdb`
- **Username**: `sa`
- **Password**: (leave empty)

### H2 Console Features
✅ View database schema  
✅ Execute SQL queries  
✅ Inspect table data  
✅ Test database operations  

### Example SQL Queries
```sql
-- View all tasks
SELECT * FROM tasks;

-- Count tasks by status
SELECT status, COUNT(*) as count FROM tasks GROUP BY status;

-- Find tasks due within 7 days
SELECT * FROM tasks WHERE due_date <= DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY);

-- Find tasks with 'API' in title
SELECT * FROM tasks WHERE title LIKE '%API%';
```

---

## 📡 API Endpoints

### Base URL
```
http://localhost:8081/tasks
```

### Endpoints Overview

| Method | Endpoint | Description | Status Code |
|--------|----------|-------------|-------------|
| **POST** | `/tasks` | Create a new task | 201 Created |
| **GET** | `/tasks/{id}` | Get task by ID | 200 OK |
| **PUT** | `/tasks/{id}` | Update task | 200 OK |
| **DELETE** | `/tasks/{id}` | Delete task | 204 No Content |
| **GET** | `/tasks` | List all tasks | 200 OK |

### 1. Create Task
**POST** `/tasks`

Create a new task with title, description, status, and due date.

```
POST /tasks HTTP/1.1
Host: localhost:8081
Content-Type: application/json
```

**Response**:
- **201 Created** - Task created successfully
- **400 Bad Request** - Invalid input data
- **Location Header** - URL of created resource

### 2. Get Task by ID
**GET** `/tasks/{id}`

Retrieve a specific task by its ID.

```
GET /tasks/550e8400-e29b-41d4-a716-446655440000 HTTP/1.1
Host: localhost:8081
```

**Response**:
- **200 OK** - Task found
- **404 Not Found** - Task does not exist

### 3. Update Task
**PUT** `/tasks/{id}`

Update an existing task's details.

```
PUT /tasks/550e8400-e29b-41d4-a716-446655440000 HTTP/1.1
Host: localhost:8081
Content-Type: application/json
```

**Response**:
- **200 OK** - Task updated successfully
- **400 Bad Request** - Invalid input data
- **404 Not Found** - Task does not exist

### 4. Delete Task
**DELETE** `/tasks/{id}`

Delete a task permanently.

```
DELETE /tasks/550e8400-e29b-41d4-a716-446655440000 HTTP/1.1
Host: localhost:8081
```

**Response**:
- **204 No Content** - Task deleted successfully
- **404 Not Found** - Task does not exist

### 5. List Tasks
**GET** `/tasks`

Retrieve all tasks with optional filtering, pagination, and sorting.

```
GET /tasks?status=PENDING&page=0&size=10&sort=dueDate,asc HTTP/1.1
Host: localhost:8081
```

**Query Parameters**:
- `status` (optional) - Filter by status: `PENDING`, `IN_PROGRESS`, `DONE`
- `page` (optional) - Page number (0-indexed, default: 0)
- `size` (optional) - Page size (default: 10)
- `sort` (optional) - Sort field and direction (e.g., `dueDate,asc`)

**Response**:
- **200 OK** - List retrieved successfully

---

## 📨 Sample Requests

### 1. Create a New Task
```bash
curl -X POST http://localhost:8081/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Complete API documentation",
    "description": "Write comprehensive API documentation",
    "status": "PENDING",
    "dueDate": "2026-06-15"
  }'
```

**Response** (201 Created):
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "title": "Complete API documentation",
  "description": "Write comprehensive API documentation",
  "status": "PENDING",
  "dueDate": "2026-06-15"
}
```

### 2. Get All Tasks
```bash
curl http://localhost:8081/tasks
```

**Response** (200 OK):
```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "title": "Complete API documentation",
      "description": "Write comprehensive API documentation",
      "status": "PENDING",
      "dueDate": "2026-06-15"
    },
    {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "title": "Deploy to production",
      "description": "Deploy the API to production environment",
      "status": "IN_PROGRESS",
      "dueDate": "2026-06-10"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 2,
    "totalPages": 1
  }
}
```

### 3. Get Task by ID
```bash
curl http://localhost:8081/tasks/550e8400-e29b-41d4-a716-446655440000
```

**Response** (200 OK):
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "title": "Complete API documentation",
  "description": "Write comprehensive API documentation",
  "status": "PENDING",
  "dueDate": "2026-06-15"
}
```

### 4. Update Task
```bash
curl -X PUT http://localhost:8081/tasks/550e8400-e29b-41d4-a716-446655440000 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Complete API documentation",
    "description": "Write comprehensive API documentation with examples",
    "status": "IN_PROGRESS",
    "dueDate": "2026-06-20"
  }'
```

**Response** (200 OK):
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "title": "Complete API documentation",
  "description": "Write comprehensive API documentation with examples",
  "status": "IN_PROGRESS",
  "dueDate": "2026-06-20"
}
```

### 5. Delete Task
```bash
curl -X DELETE http://localhost:8081/tasks/550e8400-e29b-41d4-a716-446655440000
```

**Response** (204 No Content - No body)

### 6. Filter Tasks by Status
```bash
# Get all PENDING tasks
curl http://localhost:8081/tasks?status=PENDING

# Get all IN_PROGRESS tasks
curl http://localhost:8081/tasks?status=IN_PROGRESS

# Get all DONE tasks
curl http://localhost:8081/tasks?status=DONE
```

### 7. Pagination
```bash
# Get first page (10 items)
curl http://localhost:8081/tasks?page=0&size=10

# Get second page (5 items per page)
curl http://localhost:8081/tasks?page=1&size=5
```

### 8. Sorting
```bash
# Sort by due date ascending
curl http://localhost:8081/tasks?sort=dueDate,asc

# Sort by due date descending
curl http://localhost:8081/tasks?sort=dueDate,desc
```

### 9. Combined Filtering, Pagination, and Sorting
```bash
curl "http://localhost:8081/tasks?status=PENDING&page=0&size=5&sort=dueDate,asc"
```

---

## 🧪 Testing

The project includes comprehensive tests with 72 test methods covering all layers:

- **Unit Tests** (16 tests) - Service layer with mocked dependencies
- **Controller Tests** (27 tests) - REST endpoints with MockMvc
- **Integration Tests** (30 tests) - Complete API flow with H2 database

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
# Unit tests
mvn test -Dtest=TaskServiceTest

# Controller tests
mvn test -Dtest=TaskControllerTest

# Integration tests
mvn test -Dtest=TaskIntegrationTest
```

### Run Specific Test Method
```bash
mvn test -Dtest=TaskServiceTest#createTask_success
```

### Generate Test Report
```bash
mvn surefire-report:report
```

Report will be in: `target/site/surefire-report.html`

### Test Coverage
```
├─ Unit Tests:           16 tests (Service layer)
├─ Controller Tests:     27 tests (API endpoints)
├─ Integration Tests:    30 tests (Complete flow)
└─ Total:               72 tests (100% core functionality)
```

For detailed test documentation, see:
- `TEST_SUMMARY.md` - Complete test descriptions
- `TEST_EXECUTION_GUIDE.md` - How to run and extend tests
- `VERIFICATION_CHECKLIST.md` - Quality verification

---

## 📁 Folder Structure

```
task-management-api/
│
├── 📄 pom.xml                           Maven configuration
├── 📄 README.md                         This file
├── 📄 TEST_SUMMARY.md                   Test documentation
├── 📄 TEST_EXECUTION_GUIDE.md           Test execution guide
├── 📄 VERIFICATION_CHECKLIST.md         Verification checklist
├── 📄 VISUAL_GUIDE.txt                  Visual test guide
│
├── 📁 src/
│   ├── main/
│   │   ├── java/com/example/taskmanagement/
│   │   │   ├── TaskManagementApplication.java    Main application class
│   │   │   │
│   │   │   ├── 📁 controller/
│   │   │   │   └── TaskController.java           REST endpoints
│   │   │   │
│   │   │   ├── 📁 service/
│   │   │   │   ├── TaskService.java              Service interface
│   │   │   │   └── TaskServiceImpl.java           Service implementation
│   │   │   │
│   │   │   ├── 📁 domain/
│   │   │   │   └── model/
│   │   │   │       ├── Task.java                 Entity model
│   │   │   │       └── TaskStatus.java           Status enum
│   │   │   │
│   │   │   ├── 📁 repository/
│   │   │   │   └── TaskRepository.java           Data access interface
│   │   │   │
│   │   │   ├── 📁 dto/
│   │   │   │   ├── TaskRequest.java              Create/Update request
│   │   │   │   └── TaskResponse.java             API response
│   │   │   │
│   │   │   ├── 📁 mapper/
│   │   │   │   └── TaskMapper.java               Entity-DTO mapping
│   │   │   │
│   │   │   └── 📁 exception/
│   │   │       ├── GlobalExceptionHandler.java   Global error handling
│   │   │       └── TaskNotFoundException.java    Custom exception
│   │   │
│   │   └── resources/
│   │       └── application.properties            Application configuration
│   │
│   └── test/
│       ├── java/com/example/taskmanagement/
│       │   ├── 📁 service/
│       │   │   └── TaskServiceTest.java          Unit tests (16 tests)
│       │   ├── 📁 controller/
│       │   │   └── TaskControllerTest.java       Controller tests (27 tests)
│       │   └── TaskIntegrationTest.java          Integration tests (30 tests)
│       │
│       └── resources/
│           └── application-test.properties       Test configuration
│
└── 📁 target/                           Build output
    └── task-management-api-0.0.1-SNAPSHOT.jar  Executable JAR
```

### Key Files Explained

| File | Purpose |
|------|---------|
| `TaskManagementApplication.java` | Spring Boot entry point |
| `TaskController.java` | REST API endpoints |
| `TaskServiceImpl.java` | Business logic implementation |
| `Task.java` | JPA entity model |
| `TaskRepository.java` | Spring Data repository |
| `TaskRequest/Response.java` | Data transfer objects |
| `TaskMapper.java` | Convert between entities and DTOs |
| `GlobalExceptionHandler.java` | Centralized error handling |

---

## 📝 Assumptions

The following assumptions were made during the development of this API:

### Data Model
1. **Task ID**: Automatically generated as UUID on creation
2. **Title**: Required field, non-empty string (max 255 characters)
3. **Description**: Optional field for additional task details
4. **Status**: Enum with three values: PENDING, IN_PROGRESS, DONE
5. **Due Date**: Required field, must be a future date (cannot be today or past)
6. **Default Status**: New tasks default to PENDING status if not specified

### API Behavior
1. **RESTful Design**: API follows REST conventions for HTTP methods
2. **Pagination**: List endpoint returns paginated results (default 10 per page)
3. **Sorting**: Default sort order is by due date ascending
4. **Filtering**: Status filter is optional; if not provided, all tasks are returned
5. **Timestamps**: Tasks do not have explicit creation/modification timestamps

### Error Handling
1. **Invalid Dates**: Past dates or today are rejected with 400 Bad Request
2. **Missing Fields**: Required fields (title, due date) return 400 Bad Request
3. **Not Found**: Non-existent tasks return 404 Not Found
4. **Validation Errors**: Return 400 with field-level error details
5. **Server Errors**: Unexpected errors return 500 Internal Server Error

### Database
1. **H2 In-Memory**: Database is recreated on each application startup
2. **Schema Management**: Hibernate auto-creates schema from entities
3. **Transactions**: Service layer methods are transactional
4. **Data Persistence**: Data is not persisted between application restarts

### Security
1. **No Authentication**: Open API with no authentication/authorization
2. **No Rate Limiting**: No API rate limiting implemented
3. **No CORS**: CORS not configured; can be added if needed
4. **Input Validation**: Only basic validation, no advanced security checks

### Performance
1. **In-Memory Database**: Suitable for development/testing, not production
2. **No Caching**: No caching layer implemented
3. **No Query Optimization**: Complex queries not used
4. **Pagination Limit**: Maximum page size not enforced

---

## 🚀 Future Improvements

### Phase 1: Enhanced Features
- [ ] **User Authentication**: JWT-based authentication
- [ ] **Authorization**: Role-based access control (RBAC)
- [ ] **Task Categories**: Organize tasks by category/project
- [ ] **Task Priority**: Add priority levels (LOW, MEDIUM, HIGH)
- [ ] **Task Assignments**: Assign tasks to users
- [ ] **Timestamps**: Track created_at and updated_at
- [ ] **Task History**: Audit trail for task changes
- [ ] **Comments**: Add comments to tasks for collaboration

### Phase 2: Advanced Features
- [ ] **Subtasks**: Create dependent subtasks
- [ ] **Recurring Tasks**: Support recurring task patterns
- [ ] **Reminders**: Email/notification reminders for due dates
- [ ] **Task Attachments**: Upload and manage task attachments
- [ ] **Tags**: Add flexible tagging system
- [ ] **Search**: Full-text search across tasks
- [ ] **Reporting**: Generate task completion reports
- [ ] **Analytics**: Task metrics and dashboard

### Phase 3: Production Readiness
- [ ] **Database**: Migrate to PostgreSQL/MySQL for production
- [ ] **Caching**: Implement Redis for performance
- [ ] **API Documentation**: Add Swagger/OpenAPI documentation
- [ ] **Logging**: Structured logging with ELK stack
- [ ] **Monitoring**: Application monitoring and alerting
- [ ] **Error Tracking**: Sentry or similar error tracking
- [ ] **CI/CD**: GitHub Actions or GitLab CI/CD pipeline
- [ ] **Docker**: Containerization with Docker

### Phase 4: DevOps & Deployment
- [ ] **Kubernetes**: Deployment on Kubernetes clusters
- [ ] **Load Balancing**: Scale horizontally with load balancer
- [ ] **API Gateway**: Kong or AWS API Gateway
- [ ] **Database Replication**: Primary-replica setup
- [ ] **Backup/Recovery**: Automated backup and disaster recovery
- [ ] **Performance Testing**: Load testing and optimization
- [ ] **Security Scanning**: Dependency and code scanning
- [ ] **Documentation**: Postman collections, API guides

### Phase 5: Frontend Integration
- [ ] **React Frontend**: Build React UI for web
- [ ] **Mobile App**: Mobile app using React Native/Flutter
- [ ] **WebSocket**: Real-time notifications
- [ ] **GraphQL**: Alternative GraphQL endpoint
- [ ] **Webhooks**: Event-driven webhooks

### Technical Debt Reduction
- [ ] [ ] Add integration with external monitoring tools
- [ ] [ ] Enhance test coverage to >90%
- [ ] [ ] Code quality improvements (SonarQube)
- [ ] [ ] Performance optimization
- [ ] [ ] Documentation improvements
- [ ] [ ] Security audit

---

## 📞 Support & Contributing

### Issues & Bug Reports
If you encounter any issues or bugs, please:
1. Check existing issues on GitHub
2. Provide detailed reproduction steps
3. Include error messages and logs
4. Specify your environment (Java version, OS, etc.)

### Contributing
Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Write tests for new features
4. Submit a pull request

### Questions?
- Review the test documentation: `TEST_SUMMARY.md`
- Check test execution guide: `TEST_EXECUTION_GUIDE.md`
- Review API endpoints in this README

---

## 📄 License

This project is provided as-is for educational and development purposes.

---

## Authors

- ARIGELA NITEESH KUMAR 
- Created: May 2026

---

## Changelog

### Version 0.0.1 (May 2026)
- ✅ Initial project setup with Spring Boot 3
- ✅ Domain-Driven Design architecture
- ✅ Full CRUD API endpoints
- ✅ 72 comprehensive tests
- ✅ H2 in-memory database
- ✅ Input validation and error handling
- ✅ Complete documentation

---

### Quick Reference

```bash
# Build project
mvn clean install

# Run application
mvn spring-boot:run

# Run tests
mvn test

# Access H2 Console
# -> http://localhost:8081/h2-console

# Example API call
curl http://localhost:8081/tasks
```

**Happy coding!!**
