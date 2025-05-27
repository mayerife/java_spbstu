package com.example.taskmanager.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static com.example.taskmanager.config.RabbitMQConfig.*;

@Component
@RequiredArgsConstructor
public class TaskEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishTaskCreatedEvent(TaskCreatedEvent event) {
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, event);
    }
}