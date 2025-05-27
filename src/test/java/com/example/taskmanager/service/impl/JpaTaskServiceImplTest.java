package com.example.taskmanager.service.impl;

import com.example.taskmanager.exceptions.NotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaTaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private JpaTaskServiceImpl taskService;

    @Test
    void getAllTasksByUserId_shouldReturnTasks() {
        Task task = Task.builder()
                .taskId(1L)
                .taskText("Test Task")
                .userId(1L)
                .build();

        when(taskRepository.findByUserIdAndDeletedFalse(1L)).thenReturn(List.of(task));

        List<Task> tasks = taskService.getAllTasksByUserId(1L);

        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getTaskText()).isEqualTo("Test Task");
    }

    @Test
    void getPendingTasksByUserId_shouldReturnOnlyPendingTasks() {
        Task task = Task.builder()
                .taskId(1L)
                .taskText("Incomplete task")
                .userId(1L)
                .complete(false)
                .build();

        when(taskRepository.findByUserIdAndCompleteFalseAndDeletedFalse(1L))
                .thenReturn(List.of(task));

        List<Task> tasks = taskService.getPendingTasksByUserId(1L);

        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getComplete()).isFalse();
    }

    @Test
    void createTaskForUser_shouldSetUserIdAndSave() {
        Task input = Task.builder()
                .taskText("New Task")
                .build();

        Task saved = Task.builder()
                .taskId(1L)
                .taskText("New Task")
                .userId(1L)
                .build();

        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        Task result = taskService.createTaskForUser(1L, input);

        assertThat(result.getUserId()).isEqualTo(1L);
        verify(taskRepository).save(input);
    }

    @Test
    void softDeleteTask_shouldMarkTaskAsDeleted() {
        Task task = Task.builder()
                .taskId(1L)
                .userId(1L)
                .taskText("To delete")
                .deleted(false)
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.softDeleteTask(1L, 1L);

        assertThat(task.getDeleted()).isTrue();
        verify(taskRepository).save(task);
    }

    @Test
    void softDeleteTask_shouldThrowIfNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.softDeleteTask(1L, 99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Task not found");
    }

    @Test
    void softDeleteTask_shouldThrowIfUserIdMismatch() {
        Task task = Task.builder()
                .taskId(1L)
                .userId(2L) // not matching
                .taskText("Wrong user")
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.softDeleteTask(1L, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Task not found");
    }
}
