package com.example.taskmanager.service.impl;

import com.example.taskmanager.exceptions.NotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
// @Profile("jpa")
@RequiredArgsConstructor
public class JpaTaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    public List<Task> getAllTasksByUserId(Long userId) {
        return taskRepository.findByUserIdAndDeletedFalse(userId);
    }

    @Override
    public List<Task> getPendingTasksByUserId(Long userId) {
        return taskRepository.findByUserIdAndCompleteFalseAndDeletedFalse(userId);
    }

    @Override
    public Task createTaskForUser(Long userId, Task task) {
        task.setUserId(userId);
        return taskRepository.save(task);
    }

    @Override
    public void softDeleteTask(Long userId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .filter(t -> t.getUserId().equals(userId))
                .orElseThrow(() -> new NotFoundException("Task not found"));

        task.setDeleted(true);
        taskRepository.save(task);
    }
}
