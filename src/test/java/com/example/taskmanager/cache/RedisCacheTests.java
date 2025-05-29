package com.example.taskmanager.cache;

import com.example.taskmanager.model.Notification;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.NotificationRepository;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.NotificationService;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class RedisCacheTests {

    @Autowired private TaskService taskService;
    @Autowired private UserService userService;
    @Autowired private NotificationService notificationService;

    @Autowired private CacheManager cacheManager;
    @Autowired private UserRepository userRepository;
    @Autowired private TaskRepository taskRepository;
    @Autowired private NotificationRepository notificationRepository;

    private Long testUserId;
    private Long testTaskId;
    private Long testNotificationId;

    @BeforeEach
    void setUp() {
        cacheManager.getCache("tasks").clear();
        cacheManager.getCache("users").clear();
        cacheManager.getCache("notifications").clear();

        notificationRepository.deleteAll();
        taskRepository.deleteAll();
        userRepository.deleteAll();

        // Создание пользователя
        User testUser = User.builder()
                .username("testuser")
                .password("pass123")
                .build();
        testUser = userRepository.save(testUser);
        testUserId = testUser.getUserId();

        // Создание задачи
        Task testTask = Task.builder()
                .taskText("Test task text")
                .userId(testUserId)
                .build();
        testTask = taskRepository.save(testTask);
        testTaskId = testTask.getTaskId();

        // Создание уведомления
        Notification notification = Notification.builder()
                .userId(testUserId)
                .message("Test notification")
                .build();
        notification = notificationRepository.save(notification);
        testNotificationId = notification.getNotificationId();
    }

    @Test
    void testTaskCachingByUserId() {
        List<Task> firstCall = taskService.getAllTasksByUserId(testUserId);
        List<Task> secondCall = taskService.getAllTasksByUserId(testUserId);

        assertEquals(firstCall, secondCall);
        assertNotNull(cacheManager.getCache("tasks").get(testUserId));
    }

    @Test
    void testPendingTaskCaching() {
        List<Task> first = taskService.getPendingTasksByUserId(testUserId);
        List<Task> second = taskService.getPendingTasksByUserId(testUserId);

        assertEquals(first, second);
        assertNotNull(cacheManager.getCache("tasks").get("pending_" + testUserId));
    }

    @Test
    void testTaskCacheEvictionOnCreate() {
        taskService.getAllTasksByUserId(testUserId);
        assertNotNull(cacheManager.getCache("tasks").get(testUserId));

        taskService.createTaskForUser(testUserId, Task.builder().taskText("New Task").build());
        assertNull(cacheManager.getCache("tasks").get(testUserId));
    }

    @Test
    void testTaskCacheEvictionOnDelete() {
        taskService.getAllTasksByUserId(testUserId);
        assertNotNull(cacheManager.getCache("tasks").get(testUserId));

        taskService.softDeleteTask(testUserId, testTaskId);
        assertNull(cacheManager.getCache("tasks").get(testUserId));
    }

    @Test
    void testUserCaching() {
        Optional<User> first = userService.findById(testUserId);
        Optional<User> second = userService.findById(testUserId);

        assertTrue(first.isPresent());
        assertEquals(first, second);
        assertNotNull(cacheManager.getCache("users").get(testUserId));
    }

    @Test
    void testNotificationCachingByUserId() {
        List<Notification> n1 = notificationService.getAllNotificationsByUserId(testUserId);
        List<Notification> n2 = notificationService.getAllNotificationsByUserId(testUserId);

        assertEquals(n1, n2);
        assertNotNull(cacheManager.getCache("notifications").get(testUserId));
    }

    @Test
    void testPendingNotificationCaching() {
        List<Notification> first = notificationService.getPendingNotificationsByUserId(testUserId);
        List<Notification> second = notificationService.getPendingNotificationsByUserId(testUserId);

        assertEquals(first, second);
        assertNotNull(cacheManager.getCache("notifications").get("pending_" + testUserId));
    }

    @Test
    void testNotificationCacheEvictionOnCreate() {
        notificationService.getAllNotificationsByUserId(testUserId);
        assertNotNull(cacheManager.getCache("notifications").get(testUserId));

        notificationService.createNotificationForUser(testUserId, "Another message");
        assertNull(cacheManager.getCache("notifications").get(testUserId));
    }

    @Test
    void testNotificationCacheEvictionOnMarkAsRead() {
        notificationService.getAllNotificationsByUserId(testUserId);
        assertNotNull(cacheManager.getCache("notifications").get(testUserId));

        notificationService.markNotificationAsRead(testUserId, testNotificationId);
        assertNull(cacheManager.getCache("notifications").get(testUserId));
    }

    @Test
    void testNotificationCacheEvictionOnDelete() {
        notificationService.getAllNotificationsByUserId(testUserId);
        assertNotNull(cacheManager.getCache("notifications").get(testUserId));

        notificationService.softDeleteNotification(testUserId, testNotificationId);
        assertNull(cacheManager.getCache("notifications").get(testUserId));
    }
}

