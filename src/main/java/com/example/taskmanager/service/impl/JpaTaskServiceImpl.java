package com.example.taskmanager.service.impl;

import com.example.taskmanager.exceptions.NotFoundException;
import com.example.taskmanager.messaging.TaskCreatedEvent;
import com.example.taskmanager.messaging.TaskEventPublisher;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JpaTaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskEventPublisher taskEventPublisher;

    @Override
    @Cacheable(value = "tasks", key = "#userId")
    public List<Task> getAllTasksByUserId(Long userId) {
        return taskRepository.findByUserIdAndDeletedFalse(userId);
    }

    @Override
    @Cacheable(value = "tasks", key = "'pending_' + #userId")
    public List<Task> getPendingTasksByUserId(Long userId) {
        return taskRepository.findByUserIdAndCompleteFalseAndDeletedFalse(userId);
    }

    @Override
    @CacheEvict(value = "tasks", key = "#userId")
    public Task createTaskForUser(Long userId, Task task) {
        task.setUserId(userId);
        Task savedTask = taskRepository.save(task);

        TaskCreatedEvent event = new TaskCreatedEvent(savedTask.getTaskId(), savedTask.getUserId(), savedTask.getTaskText());
        taskEventPublisher.publishTaskCreatedEvent(event);
        return savedTask;
    }

    @Override
    @CacheEvict(value = "tasks", key = "#userId")
    public void softDeleteTask(Long userId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .filter(t -> t.getUserId().equals(userId))
                .orElseThrow(() -> new NotFoundException("Task not found"));

        task.setDeleted(true);
        taskRepository.save(task);
    }

    @Override
    @Cacheable(value = "tasks", key = "'status_' + #status")
    public List<Task> findByStatus(TaskStatus status) {
        return taskRepository.findByStatusAndDeletedFalseOrderByDueDateAsc(status);
    }

    @Override
    @CacheEvict(value = "tasks", allEntries = true)
    public Task updateTask(Long taskId, Task updatedTask) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found"));

        if (Boolean.TRUE.equals(task.getDeleted())) {
            throw new IllegalStateException("Cannot update a deleted task");
        }
        task.setStatus(updatedTask.getStatus());
        task.setComplete(updatedTask.getComplete());
        task.setTaskText(updatedTask.getTaskText());
        task.setDueDate(updatedTask.getDueDate());
        return taskRepository.save(task);
    }
}
