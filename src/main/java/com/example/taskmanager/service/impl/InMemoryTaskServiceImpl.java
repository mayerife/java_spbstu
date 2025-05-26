package com.example.taskmanager.service.impl;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@Profile("in-memory")
public class InMemoryTaskServiceImpl implements TaskService {

    // taskId generator
    private final AtomicLong idGenerator = new AtomicLong(1);

    // Храним задачи в Map: userId -> List<Task>
    private final Map<Long, List<Task>> tasksByUser = new HashMap<>();

    @Override
    public List<Task> getAllTasksByUserId(Long userId) {
        return tasksByUser.getOrDefault(userId, Collections.emptyList())
                .stream()
                .filter(task -> !task.getIsDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getPendingTasksByUserId(Long userId) {
        return tasksByUser.getOrDefault(userId, Collections.emptyList())
                .stream()
                .filter(task -> !task.getIsDeleted() && !task.getIsComplete())
                .collect(Collectors.toList());
    }

    @Override
    public Task createTaskForUser(Long userId, Task task) {
        task.setTaskId(idGenerator.getAndIncrement());
        task.setUserId(userId);
        task.setCreationDate(LocalDateTime.now());
        task.setIsDeleted(false);
        task.setIsComplete(false);

        tasksByUser.computeIfAbsent(userId, k -> new ArrayList<>()).add(task);

        return task;
    }

    @Override
    public void softDeleteTask(Long userId, Long taskId) {
        List<Task> userTasks = tasksByUser.get(userId);
        if (userTasks != null) {
            userTasks.stream()
                    .filter(task -> task.getTaskId().equals(taskId))
                    .findFirst()
                    .ifPresent(task -> task.setIsDeleted(true));
        }
    }
}
