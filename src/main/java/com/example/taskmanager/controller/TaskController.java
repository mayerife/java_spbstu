package com.example.taskmanager.controller;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
class TaskRequest {
    private String taskText;
    private LocalDateTime dueDate;
}

@RestController
@RequestMapping("/api/v1/users/{userId}/tasks")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<Task>> getAllUserTasks(@PathVariable Long userId) {
        List<Task> tasks = taskService.getAllTasksByUserId(userId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Task>> getPendingUserTasks(@PathVariable Long userId) {
        List<Task> tasks = taskService.getPendingTasksByUserId(userId);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping
    public ResponseEntity<Task> createTaskForUser(@PathVariable Long userId, @RequestBody TaskRequest taskRequest) {
        if (taskRequest.getTaskText() == null || taskRequest.getTaskText().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Task taskToCreate = Task.builder()
                .taskText(taskRequest.getTaskText())
                .dueDate(taskRequest.getDueDate())
                .userId(userId)
                .build();

        Task createdTask = taskService.createTaskForUser(userId, taskToCreate);

        URI location = URI.create(String.format("/api/v1/users/%d/tasks/%d", userId, createdTask.getTaskId()));

        return ResponseEntity.created(location).body(createdTask);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> softDeleteTask(@PathVariable Long userId, @PathVariable Long taskId) {
        taskService.softDeleteTask(userId, taskId);
        return ResponseEntity.noContent().build();
    }
}
