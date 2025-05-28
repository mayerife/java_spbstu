package com.example.taskmanager.service.impl;

import com.example.taskmanager.exceptions.NotFoundException;
import com.example.taskmanager.model.Notification;
import com.example.taskmanager.service.impl.InMemoryNotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryNotificationServiceImplTest {

    private InMemoryNotificationServiceImpl notificationService;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        notificationService = new InMemoryNotificationServiceImpl();
    }

    @Test
    void testCreateNotificationForUser() {
        Notification notification = notificationService.createNotificationForUser(userId, "Test message");

        assertNotNull(notification.getNotificationId());
        assertEquals(userId, notification.getUserId());
        assertEquals("Test message", notification.getMessage());
        assertFalse(notification.isRead());
        assertFalse(notification.isDeleted());
    }

    @Test
    void testGetAllNotificationsByUserId() {
        notificationService.createNotificationForUser(userId, "Message 1");
        notificationService.createNotificationForUser(userId, "Message 2");

        List<Notification> notifications = notificationService.getAllNotificationsByUserId(userId);

        assertEquals(2, notifications.size());
    }

    @Test
    void testGetAllNotificationsByUserId_ExcludesDeleted() {
        Notification n1 = notificationService.createNotificationForUser(userId, "Message 1");
        Notification n2 = notificationService.createNotificationForUser(userId, "Message 2");

        notificationService.softDeleteNotification(userId, n1.getNotificationId());

        List<Notification> notifications = notificationService.getAllNotificationsByUserId(userId);

        assertEquals(1, notifications.size());
        assertEquals(n2.getNotificationId(), notifications.get(0).getNotificationId());
    }

    @Test
    void testGetAllNotificationsByUserId_NoNotifications_Throws() {
        assertThrows(NotFoundException.class, () -> {
            notificationService.getAllNotificationsByUserId(userId);
        });
    }

    @Test
    void testGetPendingNotificationsByUserId() {
        Notification n1 = notificationService.createNotificationForUser(userId, "Message 1");
        Notification n2 = notificationService.createNotificationForUser(userId, "Message 2");

        notificationService.markNotificationAsRead(userId, n1.getNotificationId());

        List<Notification> pending = notificationService.getPendingNotificationsByUserId(userId);

        assertEquals(1, pending.size());
        assertEquals(n2.getNotificationId(), pending.get(0).getNotificationId());
    }

    @Test
    void testGetPendingNotificationsByUserId_NoNotifications_Throws() {
        assertThrows(NotFoundException.class, () -> {
            notificationService.getPendingNotificationsByUserId(userId);
        });
    }

    @Test
    void testMarkNotificationAsRead() {
        Notification notification = notificationService.createNotificationForUser(userId, "Mark as read");

        notificationService.markNotificationAsRead(userId, notification.getNotificationId());

        List<Notification> pending = notificationService.getPendingNotificationsByUserId(userId);
        assertEquals(0, pending.size());
    }

    @Test
    void testMarkNotificationAsRead_NotFound_Throws() {
        assertThrows(NotFoundException.class, () -> {
            notificationService.markNotificationAsRead(userId, 999L);
        });
    }

    @Test
    void testSoftDeleteNotification() {
        Notification notification = notificationService.createNotificationForUser(userId, "Delete me");

        notificationService.softDeleteNotification(userId, notification.getNotificationId());

        List<Notification> all = notificationService.getAllNotificationsByUserId(userId);
        assertEquals(0, all.size());
    }

    @Test
    void testSoftDeleteNotification_NotFound_Throws() {
        assertThrows(NotFoundException.class, () -> {
            notificationService.softDeleteNotification(userId, 123L);
        });
    }
}