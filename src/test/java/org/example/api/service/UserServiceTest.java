package org.example.api.service;

import org.example.api.entity.User;
import org.example.api.exception.ResourceNotFoundException;
import org.example.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private User user;
    private String username;
    private String password;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        username = "testuser";
        password = "password";

        user = new User();
        user.setId(userId);
        user.setUsername(username);
        user.setPassword(password);
        user.setRole("USER");
    }

    @Nested
    @DisplayName("Get User Tests")
    class GetUserTests {
        @Test
        void getUserById_ShouldReturnUser_WhenExists() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            User result = userService.getUserById(userId);

            assertNotNull(result);
            assertEquals(userId, result.getId());
            assertEquals(username, result.getUsername());
            verify(userRepository).findById(userId);
        }

        @Test
        void getUserById_ShouldThrowException_WhenNotFound() {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(userId));
            verify(userRepository).findById(userId);
        }
    }

    @Nested
    @DisplayName("Validate Credentials Tests")
    class ValidateCredentialsTests {
        @Test
        void validateCredentials_ShouldReturnUser_WhenValid() {
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

            User result = userService.validateCredentials(username, password);

            assertNotNull(result);
            assertEquals(userId, result.getId());
            assertEquals(username, result.getUsername());
            verify(userRepository).findByUsername(username);
        }

        @Test
        void validateCredentials_ShouldThrowException_WhenUserNotFound() {
            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> userService.validateCredentials(username, password));
            verify(userRepository).findByUsername(username);
        }

        @Test
        void validateCredentials_ShouldThrowException_WhenPasswordInvalid() {
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

            assertThrows(ResourceNotFoundException.class,
                    () -> userService.validateCredentials(username, "wrongpassword"));
            verify(userRepository).findByUsername(username);
        }
    }
}