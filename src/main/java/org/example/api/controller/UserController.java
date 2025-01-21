package org.example.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.api.dto.LoginRequest;
import org.example.api.dto.UserDTO;
import org.example.api.service.UserFacadeService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserFacadeService userFacadeService;

    @PostMapping("/login")
    public UserDTO login(@Valid @RequestBody LoginRequest loginRequest) {
        return userFacadeService.login(loginRequest);
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable UUID id) {
        return userFacadeService.getUserById(id);
    }
}
