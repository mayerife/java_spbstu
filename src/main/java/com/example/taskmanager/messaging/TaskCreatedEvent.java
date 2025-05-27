package com.example.taskmanager.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreatedEvent implements Serializable {
    private Long taskId;
    private Long userId;
    private String taskText;
}