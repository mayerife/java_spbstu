package com.example.taskmanager.service.impl;

import com.example.taskmanager.model.User;
import com.example.taskmanager.service.impl.InMemoryUserServiceImpl;
import com.example.taskmanager.exceptions.DuplicateResourceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserServiceImplTest {

    private InMemoryUserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new InMemoryUserServiceImpl();
    }

    @Test
    void testRegisterUser_Success() {
        User user = User.builder()
                .username("john")
                .password("password123")
                .build();

        User registered = userService.registerUser(user);

        assertNotNull(registered.getUserId());
        assertEquals("john", registered.getUsername());
        assertEquals("password123", registered.getPassword());
    }

    @Test
    void testRegisterUser_DuplicateUsername_ThrowsException() {
        User user1 = User.builder()
                .username("alice")
                .password("pass1")
                .build();

        userService.registerUser(user1);

        User user2 = User.builder()
                .username("alice")  // same username
                .password("pass2")
                .build();

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            userService.registerUser(user2);
        });

        assertTrue(exception.getMessage().contains("alice"));
    }

    @Test
    void testLogin_Success() {
        User user = User.builder()
                .username("bob")
                .password("mypassword")
                .build();

        userService.registerUser(user);

        Optional<User> loggedIn = userService.login("bob", "mypassword");
        assertTrue(loggedIn.isPresent());
        assertEquals("bob", loggedIn.get().getUsername());
    }

    @Test
    void testLogin_Failure_WrongPassword() {
        User user = User.builder()
                .username("carol")
                .password("secret")
                .build();

        userService.registerUser(user);

        Optional<User> loggedIn = userService.login("carol", "wrongpass");
        assertFalse(loggedIn.isPresent());
    }

    @Test
    void testLogin_Failure_UserNotFound() {
        Optional<User> loggedIn = userService.login("nonexistent", "nopass");
        assertFalse(loggedIn.isPresent());
    }

    @Test
    void testFindById_Success() {
        User user = User.builder()
                .username("dave")
                .password("pass")
                .build();

        User registered = userService.registerUser(user);
        Long userId = registered.getUserId();

        Optional<User> found = userService.findById(userId);
        assertTrue(found.isPresent());
        assertEquals("dave", found.get().getUsername());
    }

    @Test
    void testFindById_NotFound() {
        Optional<User> found = userService.findById(999L);
        assertFalse(found.isPresent());
    }
}
