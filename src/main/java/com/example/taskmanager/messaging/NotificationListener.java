package com.example.taskmanager.messaging;

import com.example.taskmanager.config.RabbitMQConfig;
import com.example.taskmanager.model.Notification;
import com.example.taskmanager.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.example.taskmanager.config.RabbitMQConfig.QUEUE;

@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationRepository notificationRepository;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void handleTaskCreatedEvent(TaskCreatedEvent event) {
        Notification notification = Notification.builder()
                .userId(event.getUserId())
                .message("New task: " + event.getTaskText())
                .creationDate(LocalDateTime.now())
                .read(false)
                .deleted(false)
                .build();

        notificationRepository.save(notification);
    }
}