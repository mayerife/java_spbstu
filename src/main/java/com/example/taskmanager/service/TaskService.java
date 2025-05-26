package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;

import java.util.List;

public interface TaskService {

    List<Task> getAllTasksByUserId(Long userId);

    List<Task> getPendingTasksByUserId(Long userId);

    Task createTaskForUser(Long userId, Task task);

    void softDeleteTask(Long userId, Long taskId);
}