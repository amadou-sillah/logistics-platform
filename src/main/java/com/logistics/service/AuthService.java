package com.logistics.service;

import com.logistics.dto.AuthRequest;
import com.logistics.dto.AuthResponse;
import com.logistics.dto.UserDto;
import com.logistics.model.User;
import com.logistics.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;

    public AuthResponse authenticate(AuthRequest request) {
        try {
            log.info("Login attempt for email: {}", request.getEmail());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            log.info("Authentication successful for: {}", request.getEmail());
            String token = tokenProvider.generateToken(authentication);
            User user = userService.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found after authentication"));
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setName(user.getName());
            userDto.setEmail(user.getEmail());
            userDto.setRole(user.getRole().name());
            return new AuthResponse(token, userDto);
        } catch (AuthenticationException e) {
            log.error("Authentication failed for {}: {}", request.getEmail(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during login for {}: ", request.getEmail(), e);
            throw e;
        }
    }
}
