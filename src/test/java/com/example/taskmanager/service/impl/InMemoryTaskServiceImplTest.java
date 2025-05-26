package com.example.taskmanager.service.impl;

import com.example.taskmanager.exceptions.NotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.impl.InMemoryTaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskServiceImplTest {

    private InMemoryTaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        taskService = new InMemoryTaskServiceImpl();
    }

    @Test
    void testCreateAndGetTasks() {
        Long userId = 1L;
        Task task = Task.builder()
                .taskText("Test task")
                .userId(userId)
                .dueDate(LocalDateTime.now().plusDays(1))
                .build();

        Task createdTask = taskService.createTaskForUser(userId, task);

        assertNotNull(createdTask.getTaskId());
        assertEquals("Test task", createdTask.getTaskText());
        assertEquals(userId, createdTask.getUserId());
        assertFalse(createdTask.getDeleted());
        assertFalse(createdTask.getComplete());

        List<Task> allTasks = taskService.getAllTasksByUserId(userId);
        assertEquals(1, allTasks.size());
        assertEquals("Test task", allTasks.get(0).getTaskText());
    }

    @Test
    void testGetPendingTasks() {
        Long userId = 2L;

        Task task1 = Task.builder()
                .taskText("Pending task")
                .userId(userId)
                .build();

        Task task2 = Task.builder()
                .taskText("Completed task")
                .userId(userId)
                .build();

        Task createdTask1 = taskService.createTaskForUser(userId, task1);
        Task createdTask2 = taskService.createTaskForUser(userId, task2);

        createdTask2.setComplete(true);

        List<Task> pendingTasks = taskService.getPendingTasksByUserId(userId);
        assertEquals(1, pendingTasks.size());
        assertEquals("Pending task", pendingTasks.get(0).getTaskText());
    }

    @Test
    void testSoftDeleteTask() {
        Long userId = 3L;

        Task task = Task.builder()
                .taskText("Task to delete")
                .userId(userId)
                .build();

        Task createdTask = taskService.createTaskForUser(userId, task);

        taskService.softDeleteTask(userId, createdTask.getTaskId());

        List<Task> allTasks = taskService.getAllTasksByUserId(userId);
        assertTrue(allTasks.isEmpty());
    }

    @Test
    void testSoftDeleteTaskThrowsWhenUserNotFound() {
        Long userId = 100L;
        Long taskId = 1L;

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            taskService.softDeleteTask(userId, taskId);
        });

        assertEquals("No tasks found for user with id " + userId, exception.getMessage());
    }

    @Test
    void testSoftDeleteTaskThrowsWhenTaskNotFound() {
        Long userId = 4L;

        Task task = Task.builder()
                .taskText("Existing task")
                .userId(userId)
                .build();

        taskService.createTaskForUser(userId, task);

        Long nonExistingTaskId = 999L;

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            taskService.softDeleteTask(userId, nonExistingTaskId);
        });

        assertEquals("Task with id " + nonExistingTaskId + " not found for user " + userId, exception.getMessage());
    }
}
