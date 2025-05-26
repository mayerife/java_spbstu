package com.example.taskmanager.controller;

import com.example.taskmanager.controller.TaskController;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;

@WebMvcTest(TaskController.class)
@Import(TaskControllerTest.Config.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskService taskService;

    @TestConfiguration
    static class Config {
        @Bean
        public TaskService taskService() {
            return Mockito.mock(TaskService.class);
        }
    }

    @Test
    void testGetAllUserTasks() throws Exception {
        Task task = Task.builder()
                .taskId(1L)
                .userId(42L)
                .taskText("Test Task")
                .dueDate(LocalDateTime.now().plusDays(1))
                .build();

        Mockito.when(taskService.getAllTasksByUserId(42L)).thenReturn(List.of(task));

        mockMvc.perform(get("/api/v1/users/42/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].taskId").value(1L))
                .andExpect(jsonPath("$[0].taskText").value("Test Task"));
    }

    @Test
    void testGetPendingUserTasks() throws Exception {
        Task task = Task.builder()
                .taskId(2L)
                .userId(42L)
                .taskText("Pending Task")
                .complete(false)
                .build();

        Mockito.when(taskService.getPendingTasksByUserId(42L)).thenReturn(List.of(task));

        mockMvc.perform(get("/api/v1/users/42/tasks/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].taskId").value(2L))
                .andExpect(jsonPath("$[0].taskText").value("Pending Task"));
    }

    @Test
    void testCreateTask_Success() throws Exception {
        Task createdTask = Task.builder()
                .taskId(10L)
                .userId(42L)
                .taskText("New Task")
                .dueDate(LocalDateTime.of(2025, 12, 31, 10, 0))
                .build();

        Mockito.when(taskService.createTaskForUser(eq(42L), any(Task.class)))
                .thenReturn(createdTask);

        mockMvc.perform(post("/api/v1/users/42/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "taskText": "New Task",
                                  "dueDate": "2025-12-31T10:00:00"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/users/42/tasks/10"))
                .andExpect(jsonPath("$.taskId").value(10L))
                .andExpect(jsonPath("$.taskText").value("New Task"));
    }

    @Test
    void testCreateTask_BadRequest_EmptyText() throws Exception {
        mockMvc.perform(post("/api/v1/users/42/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "taskText": "   ",
                                  "dueDate": "2025-12-31T10:00:00"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSoftDeleteTask() throws Exception {
        mockMvc.perform(delete("/api/v1/users/42/tasks/123"))
                .andExpect(status().isNoContent());

        Mockito.verify(taskService).softDeleteTask(42L, 123L);
    }
}
