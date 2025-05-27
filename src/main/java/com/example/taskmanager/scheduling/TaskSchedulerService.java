package com.example.taskmanager.scheduling;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.service.NotificationService;
import com.example.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskSchedulerService {

    private final TaskService taskService;
    private final NotificationService notificationService;

    @Scheduled(fixedRate = 60_000)
    public void checkOverdueTasks() {
        log.info("Checking for overdue tasks...");
        List<Task> overdueTasks = findOverdueTasks();

        for (Task task : overdueTasks) {
            processOverdueTask(task);
        }
    }

    public List<Task> findOverdueTasks() {
        return taskService.findByStatus(TaskStatus.TODO).stream()
                .filter(task -> !task.getComplete() && !task.getDeleted())
                .filter(task -> task.getDueDate() != null && task.getDueDate().isBefore(LocalDateTime.now()))
                .toList();
    }

    @Async
    public void processOverdueTask(Task task) {
        log.info("⚠ Processing overdue task id: {}", task.getTaskId());

        task.setStatus(TaskStatus.OVERDUE);
        taskService.updateTask(task.getTaskId(), task);

        String msg = "Task '" + task.getTaskText() + "' is overdue!";
        notificationService.createNotificationForUser(task.getUserId(), msg);
    }
}
