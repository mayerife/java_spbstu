package com.example.taskmanager.service.impl;

import com.example.taskmanager.model.Notification;
import com.example.taskmanager.service.NotificationService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@Profile("in-memory")
public class InMemoryNotificationService implements NotificationService {

    private final Map<Long, List<Notification>> notificationStore = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<Notification> getAllNotificationsByUserId(Long userId) {
        return notificationStore.getOrDefault(userId, Collections.emptyList())
                .stream()
                .filter(n -> !n.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> getPendingNotificationsByUserId(Long userId) {
        return notificationStore.getOrDefault(userId, Collections.emptyList())
                .stream()
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
        if (notifications != null) {
            notifications.stream()
                    .filter(n -> n.getNotificationId().equals(notificationId))
                    .findFirst()
                    .ifPresent(n -> n.setRead(true));
        }
    }

    @Override
    public void softDeleteNotification(Long userId, Long notificationId) {
        List<Notification> notifications = notificationStore.get(userId);
        if (notifications != null) {
            notifications.stream()
                    .filter(n -> n.getNotificationId().equals(notificationId))
                    .findFirst()
                    .ifPresent(n -> n.setDeleted(true));
        }
    }
}
