package com.example.taskmanager.repository;

import com.example.taskmanager.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdAndDeletedFalse(Long userId);
    List<Notification> findByUserIdAndDeletedFalseAndReadFalse(Long userId);
}

