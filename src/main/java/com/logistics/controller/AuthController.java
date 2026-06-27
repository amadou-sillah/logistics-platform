package com.logistics.controller;

import com.logistics.dto.AuthRequest;
import com.logistics.dto.AuthResponse;
import com.logistics.model.Role;
import com.logistics.model.User;
import com.logistics.service.AuthService;
import com.logistics.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @GetMapping("/ping")
    public Map<String, String> ping() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "Auth controller is alive");
        return response;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        log.info("Login attempt for: {}", request.getEmail());
        return authService.authenticate(request);
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody AuthRequest request) {
        log.info("Registration attempt for email: {}", request.getEmail());
        try {
            if (userService.findByEmail(request.getEmail()).isPresent()) {
                log.warn("Registration failed: user with email {} already exists", request.getEmail());
                throw new ResponseStatusException(HttpStatus.CONFLICT, "User with this email already exists");
            }
            User user = userService.register(
                    request.getName() != null ? request.getName() : "User",
                    request.getEmail(),
                    request.getPassword(),
                    Role.CUSTOMER
            );
            log.info("User registered with ID: {}", user.getId());
            return authService.authenticate(request);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during registration for {}: ", request.getEmail(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Registration failed: " + e.getMessage());
        }
    }
}
