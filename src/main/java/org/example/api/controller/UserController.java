package org.example.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.api.dto.LoginRequest;
import org.example.api.dto.UserDTO;
import org.example.api.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public UserDTO login(@Valid @RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }
}
