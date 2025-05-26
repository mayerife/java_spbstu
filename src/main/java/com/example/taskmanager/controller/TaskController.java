package com.example.taskmanager.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // GET: Все задачи пользователя
    @GetMapping("/{userId}")
    public List<Task> getAllTasks(@PathVariable Long userId) {
        return taskService.getAllTasksForUser(userId);
    }

    // GET: Только незавершённые задачи
    @GetMapping("/pending/{userId}")
    public List<Task> getPendingTasks(@PathVariable Long userId) {
        return taskService.getPendingTasksForUser(userId);
    }

    // POST: Добавить задачу
    @PostMapping
    public ResponseEntity<Task> addTask(@RequestBody Task task) {
        Task created = taskService.addTask(task);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // DELETE (soft): Пометить как удалённую
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        boolean deleted = taskService.markTaskDeleted(taskId);
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
