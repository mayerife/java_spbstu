package com.example.taskmanager.controller;

import com.example.taskmanager.controller.UserController;
import com.example.taskmanager.model.User;
import com.example.taskmanager.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;

@WebMvcTest(UserController.class)
@Import(UserControllerTest.Config.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @TestConfiguration
    static class Config {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }

    @Test
    void testLogin_Successful() throws Exception {
        User user = User.builder()
                .userId(1L)
                .username("testuser")
                .password("password")
                .build();

        Mockito.when(userService.login("testuser", "password")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/v1/users/login")
                        .param("username", "testuser")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void testLogin_Unauthorized() throws Exception {
        Mockito.when(userService.login("invalid", "wrong")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/users/login")
                        .param("username", "invalid")
                        .param("password", "wrong"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRegisterUser_Successful() throws Exception {
        User createdUser = User.builder()
                .userId(1L)
                .username("newuser")
                .password("newpass")
                .build();

        Mockito.when(userService.registerUser(any(User.class))).thenReturn(createdUser);

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "newuser",
                                  "password": "newpass"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/users/1"))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.username").value("newuser"));
    }

    @Test
    void testRegisterUser_MissingFields_BadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "",
                                  "password": "pass"
                                }
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "user",
                                  "password": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterUser_Conflict() throws Exception {
        Mockito.when(userService.registerUser(any(User.class)))
                .thenThrow(new IllegalArgumentException("Duplicate"));

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "duplicateuser",
                                  "password": "123"
                                }
                                """))
                .andExpect(status().isConflict());
    }
}
