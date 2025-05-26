package com.example.taskmanager.service.impl;

import com.example.taskmanager.exceptions.NotFoundException;
import com.example.taskmanager.model.Notification;
import com.example.taskmanager.service.NotificationService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@Profile("in-memory")
public class InMemoryNotificationServiceImpl implements NotificationService {

    private final Map<Long, List<Notification>> notificationStore = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<Notification> getAllNotificationsByUserId(Long userId) {
        List<Notification> notifications = notificationStore.get(userId);
        if (notifications == null) {
            throw new NotFoundException("User with id " + userId + " has no notifications.");
        }
        return notifications.stream()
                .filter(n -> !n.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> getPendingNotificationsByUserId(Long userId) {
        List<Notification> notifications = notificationStore.get(userId);
        if (notifications == null) {
            throw new NotFoundException("User with id " + userId + " has no notifications.");
        }
        return notifications.stream()
                .filter(n -> !n.isDeleted() && !n.isRead())
                .collect(Collectors.toList());
    }

    @Override
    public Notification createNotificationForUser(Long userId, String message) {
        Notification notification = Notification.builder()
                .notificationId(idGenerator.getAndIncrement())
                .userId(userId)
                .message(message)
                .build();

        notificationStore.computeIfAbsent(userId, k -> new ArrayList<>()).add(notification);
        return notification;
    }

    @Override
    public void markNotificationAsRead(Long userId, Long notificationId) {
        List<Notification> notifications = notificationStore.get(userId);
        if (notifications == null) {
            throw new NotFoundException("User with id " + userId + " has no notifications.");
        }

        Notification notification = notifications.stream()
                .filter(n -> n.getNotificationId().equals(notificationId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        "Notification with id " + notificationId + " not found for user " + userId));

        notification.setRead(true);
    }

    @Override
    public void softDeleteNotification(Long userId, Long notificationId) {
        List<Notification> notifications = notificationStore.get(userId);
        if (notifications == null) {
            throw new NotFoundException("User with id " + userId + " has no notifications.");
        }

        Notification notification = notifications.stream()
                .filter(n -> n.getNotificationId().equals(notificationId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        "Notification with id " + notificationId + " not found for user " + userId));

        notification.setDeleted(true);
    }
}
