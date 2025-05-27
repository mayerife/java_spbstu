package com.example.taskmanager.scheduling;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.service.NotificationService;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.scheduling.TaskSchedulerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TaskSchedulerServiceTest {

    @Mock
    private TaskService taskService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TaskSchedulerService taskSchedulerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void checkOverdueTasks_shouldProcessOverdueTasks() {
        Task overdueTask = new Task();
        overdueTask.setTaskId(1L);
        overdueTask.setUserId(10L);
        overdueTask.setTaskText("Просроченная задача");
        overdueTask.setComplete(false);
        overdueTask.setDeleted(false);
        overdueTask.setDueDate(LocalDateTime.now().minusDays(1));
        overdueTask.setStatus(TaskStatus.TODO);

        when(taskService.findByStatus(TaskStatus.TODO)).thenReturn(List.of(overdueTask));
        when(taskService.updateTask(anyLong(), any(Task.class))).thenAnswer(invocation -> invocation.getArgument(1));

        taskSchedulerService.checkOverdueTasks();

        verify(taskService).updateTask(eq(1L), argThat(task -> task.getStatus() == TaskStatus.OVERDUE));

        verify(notificationService).createNotificationForUser(eq(10L), contains("overdue"));
    }

    @Test
    void findOverdueTasks_shouldReturnOnlyOverdue() {
        Task task1 = new Task();
        task1.setComplete(false);
        task1.setDeleted(false);
        task1.setDueDate(LocalDateTime.now().minusMinutes(10));
        task1.setStatus(TaskStatus.TODO);

        Task task2 = new Task();
        task2.setComplete(true);
        task2.setDeleted(false);
        task2.setDueDate(LocalDateTime.now().minusMinutes(10));
        task2.setStatus(TaskStatus.TODO);

        when(taskService.findByStatus(TaskStatus.TODO)).thenReturn(List.of(task1, task2));

        List<Task> overdueTasks = taskSchedulerService.findOverdueTasks();

        assert overdueTasks.size() == 1;
        assert overdueTasks.get(0) == task1;
    }
}

