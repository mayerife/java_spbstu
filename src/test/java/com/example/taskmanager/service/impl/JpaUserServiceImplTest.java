package com.example.taskmanager.service.impl;

import com.example.taskmanager.exceptions.DuplicateResourceException;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaUserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JpaUserServiceImpl userService;

    @Test
    void registerUser_shouldSaveAndReturnUser_whenUsernameIsUnique() {
        User user = User.builder()
                .username("newuser")
                .password("password")
                .build();

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        User savedUser = userService.registerUser(user);

        assertThat(savedUser).isEqualTo(user);
        verify(userRepository).save(user);
    }

    @Test
    void registerUser_shouldThrowException_whenUsernameAlreadyExists() {
        User user = User.builder()
                .username("existinguser")
                .password("pass")
                .build();

        when(userRepository.findByUsername("existinguser"))
                .thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.registerUser(user))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Username already exists");

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_shouldReturnUser_whenCredentialsMatch() {
        User user = User.builder()
                .username("john")
                .password("secret")
                .build();

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        Optional<User> result = userService.login("john", "secret");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(user);
    }

    @Test
    void login_shouldReturnEmpty_whenPasswordDoesNotMatch() {
        User user = User.builder()
                .username("john")
                .password("correct")
                .build();

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        Optional<User> result = userService.login("john", "wrong");

        assertThat(result).isEmpty();
    }

    @Test
    void findById_shouldReturnUser_whenUserExists() {
        User user = User.builder()
                .userId(1L)
                .username("test")
                .password("pass")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> found = userService.findById(1L);

        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(user);
    }

    @Test
    void findById_shouldReturnEmpty_whenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> found = userService.findById(999L);

        assertThat(found).isEmpty();
    }
}
