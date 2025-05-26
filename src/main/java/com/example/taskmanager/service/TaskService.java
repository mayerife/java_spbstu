package com.example.taskmanager.service;

import org.springframework.stereotype.Service;
import com.example.taskmanager.model.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final Map<Long, Task> tasks = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong();

    public List<Task> getAllTasksForUser(Long userId) {
        return tasks.values().stream()
                .filter(t -> !t.isDeleted() && t.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Task> getPendingTasksForUser(Long userId) {
        return tasks.values().stream()
                .filter(t -> !t.isDeleted() && !t.isCompleted() && t.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public Task addTask(Task task) {
        long id = idGenerator.incrementAndGet();
        task.setId(id);
        task.setCreationDate(LocalDateTime.now());
        tasks.put(id, task);
        return task;
    }

    public boolean markTaskDeleted(Long taskId) {
        Task task = tasks.get(taskId);
        if (task != null) {
            task.setDeleted(true);
            return true;
        }
        return false;
    }
}