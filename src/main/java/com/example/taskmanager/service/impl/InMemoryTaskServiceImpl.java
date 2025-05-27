package com.example.taskmanager.service.impl;

import com.example.taskmanager.exceptions.NotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
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
                .filter(task -> !task.getDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getPendingTasksByUserId(Long userId) {
        return tasksByUser.getOrDefault(userId, Collections.emptyList())
                .stream()
                .filter(task -> !task.getDeleted() && !task.getComplete())
                .collect(Collectors.toList());
    }

    @Override
    public Task createTaskForUser(Long userId, Task task) {
        task.setTaskId(idGenerator.getAndIncrement());
        task.setUserId(userId);
        task.setCreationDate(LocalDateTime.now());
        task.setDeleted(false);
        task.setComplete(false);

        tasksByUser.computeIfAbsent(userId, k -> new ArrayList<>()).add(task);

        return task;
    }

    @Override
    public void softDeleteTask(Long userId, Long taskId) {
        List<Task> userTasks = tasksByUser.get(userId);
        if (userTasks == null) {
            throw new NotFoundException("No tasks found for user with id " + userId);
        }

        Task task = userTasks.stream()
                .filter(t -> t.getTaskId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        "Task with id " + taskId + " not found for user " + userId));

        task.setDeleted(true);
    }


    @Override
    public List<Task> findByStatus(TaskStatus status) {
        return tasksByUser.values().stream()
                .flatMap(Collection::stream)
                .filter(task -> !task.getDeleted() && task.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public Task updateTask(Long taskId, Task updatedTask) {
        for (List<Task> userTasks : tasksByUser.values()) {
            for (int i = 0; i < userTasks.size(); i++) {
                Task task = userTasks.get(i);
                if (task.getTaskId().equals(taskId)) {
                    task.setStatus(updatedTask.getStatus());
                    task.setComplete(updatedTask.getComplete());
                    task.setTaskText(updatedTask.getTaskText());
                    task.setDueDate(updatedTask.getDueDate());
                    task.setDeleted(updatedTask.getDeleted());
                    return task;
                }
            }
        }
        throw new NotFoundException("Task with id " + taskId + " not found");
    }
}
