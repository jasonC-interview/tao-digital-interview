package org.example.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.api.dto.*;
import org.example.api.exception.ResourceNotFoundException;
import org.example.api.service.UserFacadeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserFacadeService userFacadeService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID userId;
    private UserDTO userDTO;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userDTO = new UserDTO(
                userId,
                "user",
                "USER",
                List.of()
        );
        loginRequest = new LoginRequest("user", "password");
    }

    @Nested
    @DisplayName("POST /user/login endpoint")
    class LoginTests {
        @Test
        void login_ShouldReturnUser_WhenValidCredentials() throws Exception {
            when(userFacadeService.login(loginRequest)).thenReturn(userDTO);

            mockMvc.perform(post("/user/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(userId.toString()))
                    .andExpect(jsonPath("$.username").value("user"))
                    .andExpect(jsonPath("$.role").value("USER"));

            verify(userFacadeService).login(loginRequest);
        }

        @Test
        void login_ShouldReturn404_WhenInvalidCredentials() throws Exception {
            when(userFacadeService.login(loginRequest))
                    .thenThrow(new ResourceNotFoundException("Invalid username or password"));

            mockMvc.perform(post("/user/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isNotFound());

            verify(userFacadeService).login(loginRequest);
        }

        @Test
        void login_ShouldReturn400_WhenInvalidRequest() throws Exception {
            LoginRequest invalidRequest = new LoginRequest(null, null);

            mockMvc.perform(post("/user/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(userFacadeService, never()).login(any());
        }
    }

    @Nested
    @DisplayName("GET /user/{id} endpoint")
    class GetUserTests {
        @Test
        void getUserById_ShouldReturnUser_WhenExists() throws Exception {
            when(userFacadeService.getUserById(userId)).thenReturn(userDTO);

            mockMvc.perform(get("/user/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(userId.toString()))
                    .andExpect(jsonPath("$.username").value("user"))
                    .andExpect(jsonPath("$.role").value("USER"));

            verify(userFacadeService).getUserById(userId);
        }

        @Test
        void getUserById_ShouldReturn404_WhenNotFound() throws Exception {
            when(userFacadeService.getUserById(userId))
                    .thenThrow(new ResourceNotFoundException("User not found"));

            mockMvc.perform(get("/user/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(userFacadeService).getUserById(userId);
        }

        @Test
        void getUserById_ShouldReturn400_WhenInvalidId() throws Exception {
            mockMvc.perform(get("/user/invalid-uuid")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(userFacadeService, never()).getUserById(any());
        }
    }
}