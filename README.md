# TaskManager — Multi-layered Spring Boot Task & Notification Manager
By Sofia Kravchenko, gr. 5130203/20102

---

## About the Project

TaskManager is a fully functional REST API application for managing tasks with notifications, built using Spring Boot.  
The project demonstrates best OOP practices, interface-based architecture, profile-based storage selection, caching, message broker integration, and asynchronous scheduling.

---

## Features

- **Modular design:** interfaces + multiple implementations for data storage (In-Memory, PostgreSQL)
- **Spring Profiles:** switch between storage implementations with `@Profile`
- **REST API:** controllers for tasks, users, and notifications with full CRUD logic and soft deletion
- **Unit tests:** written using JUnit 5 and Mockito
- **Database:** initially H2 in-memory, then PostgreSQL with Flyway migrations
- **Caching:** Redis integration for performance improvement
- **Messaging:** RabbitMQ integration — publishing events on task creation and async notification processing
- **Scheduling & Async:** periodic checking for overdue tasks using `@Scheduled` and `@Async`
- **Docker:** containerization and orchestration with Docker Compose

---

## Project Structure & Branches

Each development step was implemented on a separate branch and Pull Request for clear progress tracking:

| Step | Description                         | Branch   |
|-------|----------------------------------|----------|
| 1     | REST API with In-Memory Storage  | `step-1` |
| 2     | Unit Testing                     | `step-2` |
| 3     | Add H2 & JPA                    | `step-3` |
| 4     | Docker Support                  | `step-4` |
| 5     | PostgreSQL + Flyway Migrations  | `step-5` |
| 6     | Caching with Redis              | `step-6` |
| 7     | Messaging with RabbitMQ         | `step-7` |
| 8     | Scheduling & Async Tasks        | `step-8` |

---

## How to Run


### Locally (Steps 1–3)

1. Clone the repository and checkout the desired branch.
2. Configure `application.yml` for local H2/PostgreSQL.
3. Run the application via IDE or:
   ```bash
   ./gradlew bootRun
4. Access the API at http://localhost:8080


### With Docker Compose (Steps 4+)

1. Install **Docker** and **Docker Compose**
2. Run the services:
   ```bash
   docker-compose up --build
   ```
3. Available at:
    - API: `http://localhost:8080`
    - PostgreSQL: `localhost:5432`
    - RabbitMQ UI: `http://localhost:15672` (default user: guest / guest)

---

## API Endpoints

### TaskController
- `GET /tasks` — fetch all tasks for current user
- `GET /tasks/pending` — fetch only pending tasks
- `POST /tasks` — create new task
- `DELETE /tasks/{id}` — soft-delete task

### UserController
- `GET /users/login` — login (returns user info)
- `POST /users` — register user

### NotificationController
- `GET /notifications` — all notifications
- `GET /notifications/pending` — only unread notifications

---

