#!/bin/bash
set -e

echo "📁 Generating full backend source code for PostgreSQL..."

# DTOs
mkdir -p src/main/java/com/logistics/dto
cat > src/main/java/com/logistics/dto/AuthRequest.java << 'DTO'
package com.logistics.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank @Email
    private String email;
    @NotBlank
    private String password;
    private String name; // for registration
}
DTO

cat > src/main/java/com/logistics/dto/AuthResponse.java << 'DTO'
package com.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private UserDto user;
}
DTO

cat > src/main/java/com/logistics/dto/UserDto.java << 'DTO'
package com.logistics.dto;

import lombok.Data;

@Data
public class UserDto {
    private String id;
    private String name;
    private String email;
    private String role;
}
DTO

cat > src/main/java/com/logistics/dto/ShipmentDto.java << 'DTO'
package com.logistics.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ShipmentDto {
    private String id;
    private String trackingNumber;
    private String customerId;
    private String origin;
    private String destination;
    private String status;
    private LocalDateTime eta;
    private Double amount;
    private String priority;
}
DTO

cat > src/main/java/com/logistics/dto/TrackingEventDto.java << 'DTO'
package com.logistics.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TrackingEventDto {
    private String shipmentId;
    private String eventType;
    private String description;
    private String location;
    private Double latitude;
    private Double longitude;
    private LocalDateTime occurredAt;
}
DTO

cat > src/main/java/com/logistics/dto/StatsDto.java << 'DTO'
package com.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatsDto {
    private String label;
    private String value;
    private String icon;
    private String change;
    private boolean positive;
}
DTO

# Security classes
mkdir -p src/main/java/com/logistics/security
cat > src/main/java/com/logistics/security/JwtTokenProvider.java << 'SEC'
package com.logistics.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${spring.security.jwt.secret}")
    private String secret;

    @Value("${spring.security.jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getAuthorities().iterator().next().getAuthority())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        return getClaims(token).get("role", String.class);
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
SEC

cat > src/main/java/com/logistics/security/JwtAuthenticationFilter.java << 'SEC'
package com.logistics.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractJwt(request);
        if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
            String username = tokenProvider.getUsernameFromToken(token);
            String role = tokenProvider.getRoleFromToken(token);
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null,
                            Collections.singletonList(new SimpleGrantedAuthority(role)));
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String extractJwt(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
SEC

cat > src/main/java/com/logistics/security/CustomUserDetailsService.java << 'SEC'
package com.logistics.security;

import com.logistics.model.User;
import com.logistics.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();
    }
}
SEC

# Services
mkdir -p src/main/java/com/logistics/service
cat > src/main/java/com/logistics/service/UserService.java << 'SRV'
package com.logistics.service;

import com.logistics.model.Role;
import com.logistics.model.User;
import com.logistics.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User register(String name, String email, String password, Role role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role != null ? role : Role.CUSTOMER);
        return userRepository.save(user);
    }
}
SRV

cat > src/main/java/com/logistics/service/AuthService.java << 'SRV'
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
SRV

cat > src/main/java/com/logistics/service/ShipmentService.java << 'SRV'
package com.logistics.service;

import com.logistics.model.Shipment;
import com.logistics.model.TrackingEvent;
import com.logistics.repository.ShipmentRepository;
import com.logistics.repository.TrackingEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final TrackingEventRepository trackingEventRepository;

    public Shipment createShipment(Shipment shipment) {
        shipment.setTrackingNumber(generateTrackingNumber());
        shipment.setStatus("PENDING");
        shipment.setCreatedAt(LocalDateTime.now());
        shipment.setUpdatedAt(LocalDateTime.now());
        return shipmentRepository.save(shipment);
    }

    private String generateTrackingNumber() {
        return "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Cacheable(value = "shipments", key = "#trackingNumber")
    public Optional<Shipment> findByTrackingNumber(String trackingNumber) {
        return shipmentRepository.findByTrackingNumber(trackingNumber);
    }

    public List<Shipment> findByCustomerId(String customerId) {
        return shipmentRepository.findByCustomerId(customerId);
    }

    public Shipment updateStatus(String shipmentId, String newStatus) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));
        shipment.setStatus(newStatus);
        shipment.setUpdatedAt(LocalDateTime.now());
        return shipmentRepository.save(shipment);
    }

    public TrackingEvent addTrackingEvent(String shipmentId, String eventType, String description,
                                          String location, Double lat, Double lng) {
        TrackingEvent event = new TrackingEvent();
        event.setShipmentId(shipmentId);
        event.setEventType(eventType);
        event.setDescription(description);
        event.setLocation(location);
        event.setLatitude(lat);
        event.setLongitude(lng);
        event.setOccurredAt(LocalDateTime.now());
        return trackingEventRepository.save(event);
    }

    public List<TrackingEvent> getTrackingEvents(String shipmentId) {
        return trackingEventRepository.findByShipmentIdOrderByOccurredAtDesc(shipmentId);
    }

    public List<Shipment> findAll() {
        return shipmentRepository.findAll();
    }

    public Optional<Shipment> findById(String id) {
        return shipmentRepository.findById(id);
    }

    public void deleteById(String id) {
        shipmentRepository.deleteById(id);
    }
}
SRV

# Controllers
mkdir -p src/main/java/com/logistics/controller
cat > src/main/java/com/logistics/controller/AuthController.java << 'CTRL'
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
CTRL

cat > src/main/java/com/logistics/controller/ShipmentController.java << 'CTRL'
package com.logistics.controller;

import com.logistics.model.Shipment;
import com.logistics.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public List<Shipment> getAll() {
        return shipmentService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public Shipment getById(@PathVariable String id) {
        return shipmentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public Shipment create(@RequestBody Shipment shipment) {
        return shipmentService.createShipment(shipment);
    }

    @GetMapping("/tracking/{trackingNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public Shipment getByTrackingNumber(@PathVariable String trackingNumber) {
        return shipmentService.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteShipment(@PathVariable String id) {
        shipmentService.deleteById(id);
    }
}
CTRL

cat > src/main/java/com/logistics/controller/TrackingController.java << 'CTRL'
package com.logistics.controller;

import com.logistics.dto.TrackingEventDto;
import com.logistics.model.TrackingEvent;
import com.logistics.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final ShipmentService shipmentService;

    @PostMapping("/event")
    public TrackingEvent addEvent(@RequestBody TrackingEventDto dto) {
        return shipmentService.addTrackingEvent(
                dto.getShipmentId(),
                dto.getEventType(),
                dto.getDescription(),
                dto.getLocation(),
                dto.getLatitude(),
                dto.getLongitude()
        );
    }

    @GetMapping("/{shipmentId}")
    public List<TrackingEvent> getEvents(@PathVariable String shipmentId) {
        return shipmentService.getTrackingEvents(shipmentId);
    }
}
CTRL

cat > src/main/java/com/logistics/controller/AdminController.java << 'CTRL'
package com.logistics.controller;

import com.logistics.model.User;
import com.logistics.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.findByEmail(null) // not used; we need a method to list all users
            // For simplicity, we'll add a findAll method to UserService
            // I'll add it below
            // Actually, UserService doesn't have findAll, so add it:
            // We'll quickly add a method in UserService.
            // Let's just return userRepository.findAll() but we need access.
            // Since this is a script, we'll add the method later.
            // For now, we'll leave it and fix later.
    }
}
CTRL
# Actually, I realize we need to add findAll in UserService. I'll include it in the UserService script.

# Let's add a findAll method to UserService
cat >> src/main/java/com/logistics/service/UserService.java << 'SRV'
    public List<User> findAll() {
        return userRepository.findAll();
    }
SRV

# Now we need to add the missing import for List
sed -i '1i import java.util.List;' src/main/java/com/logistics/service/UserService.java

# Recreate AdminController with proper method
cat > src/main/java/com/logistics/controller/AdminController.java << 'CTRL'
package com.logistics.controller;

import com.logistics.model.User;
import com.logistics.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.findAll();
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable String id) {
        userService.deleteById(id);
    }
}
CTRL

# Add deleteById to UserService
cat >> src/main/java/com/logistics/service/UserService.java << 'SRV'

    public void deleteById(String id) {
        userRepository.deleteById(id);
    }
SRV

cat > src/main/java/com/logistics/controller/AgentController.java << 'CTRL'
package com.logistics.controller;

import com.logistics.model.Shipment;
import com.logistics.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agent")
@PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
@RequiredArgsConstructor
public class AgentController {

    private final ShipmentService shipmentService;

    @GetMapping("/tasks")
    public List<Shipment> getTasks() {
        return shipmentService.findAll().stream()
                .filter(s -> !"DELIVERED".equals(s.getStatus()))
                .toList();
    }

    @PatchMapping("/tasks/{id}")
    public Shipment updateStatus(@PathVariable String id, @RequestParam String status) {
        return shipmentService.updateStatus(id, status);
    }
}
CTRL

cat > src/main/java/com/logistics/controller/DashboardController.java << 'CTRL'
package com.logistics.controller;

import com.logistics.dto.StatsDto;
import com.logistics.model.Shipment;
import com.logistics.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final ShipmentService shipmentService;

    @GetMapping("/stats")
    public List<StatsDto> getStats() {
        List<Shipment> all = shipmentService.findAll();
        long total = all.size();
        long delivered = all.stream().filter(s -> "DELIVERED".equals(s.getStatus())).count();
        long inTransit = all.stream().filter(s -> "IN_TRANSIT".equals(s.getStatus())).count();
        long pending = all.stream().filter(s -> "PENDING".equals(s.getStatus())).count();
        return List.of(
                new StatsDto("Total Shipments", String.valueOf(total), "Package", "+12%", true),
                new StatsDto("In Transit", String.valueOf(inTransit), "Truck", "+4%", true),
                new StatsDto("Delivered", String.valueOf(delivered), "Users", "-2%", false),
                new StatsDto("Pending", String.valueOf(pending), "DollarSign", "+18%", true)
        );
    }

    @GetMapping("/weekly-shipments")
    public List<Map<String, Object>> getWeeklyShipments() {
        List<Shipment> all = shipmentService.findAll();
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6);
        Map<String, Long> dailyCounts = all.stream()
                .filter(s -> s.getCreatedAt() != null)
                .filter(s -> s.getCreatedAt().toLocalDate().isAfter(weekAgo.minusDays(1)))
                .collect(Collectors.groupingBy(
                        s -> s.getCreatedAt().toLocalDate().format(DateTimeFormatter.ofPattern("EEE")),
                        Collectors.counting()
                ));
        Map<String, Long> result = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(6 - i);
            String dayName = date.format(DateTimeFormatter.ofPattern("EEE"));
            result.put(dayName, dailyCounts.getOrDefault(dayName, 0L));
        }
        return result.entrySet().stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", e.getKey());
                    map.put("value", e.getValue().intValue());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/status-distribution")
    public List<Map<String, Object>> getStatusDistribution() {
        List<Shipment> all = shipmentService.findAll();
        Map<String, Long> countByStatus = all.stream()
                .collect(Collectors.groupingBy(Shipment::getStatus, Collectors.counting()));
        return countByStatus.entrySet().stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", e.getKey());
                    map.put("value", e.getValue());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/recent-shipments")
    public List<Shipment> getRecentShipments() {
        return shipmentService.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(5)
                .collect(Collectors.toList());
    }
}
CTRL

# Config
mkdir -p src/main/java/com/logistics/config
cat > src/main/java/com/logistics/config/SecurityConfig.java << 'CFG'
package com.logistics.config;

import com.logistics.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/public/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/agent/**").hasAnyRole("AGENT", "ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
CFG

# Exception handling
mkdir -p src/main/java/com/logistics/exception
cat > src/main/java/com/logistics/exception/GlobalExceptionHandler.java << 'EXC'
package com.logistics.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuth(AuthenticationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "Invalid credentials");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("errors", ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", ex.getStatusCode().value());
        body.put("error", ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.FORBIDDEN.value());
        body.put("error", "Access Denied: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal server error: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
EXC

# DataInitializer
mkdir -p src/main/java/com/logistics/initializer
cat > src/main/java/com/logistics/initializer/DataInitializer.java << 'INIT'
package com.logistics.initializer;

import com.logistics.model.Role;
import com.logistics.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;

    @Override
    public void run(String... args) {
        log.info("DataInitializer: Checking if admin user exists...");
        if (userService.findByEmail("admin@logistics.com").isEmpty()) {
            log.info("DataInitializer: Creating admin user...");
            userService.register("Admin", "admin@logistics.com", "password", Role.ADMIN);
            log.info("DataInitializer: Admin user created.");
        } else {
            log.info("DataInitializer: Admin user already exists.");
        }

        if (userService.findByEmail("agent@logistics.com").isEmpty()) {
            log.info("DataInitializer: Creating agent user...");
            userService.register("Agent", "agent@logistics.com", "password", Role.AGENT);
            log.info("DataInitializer: Agent user created.");
        } else {
            log.info("DataInitializer: Agent user already exists.");
        }
    }
}
INIT

# Main application class
cat > src/main/java/com/logistics/LogisticsApplication.java << 'MAIN'
package com.logistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.logistics.repository")
@EnableCaching
@EnableAsync
public class LogisticsApplication {
    public static void main(String[] args) {
        SpringApplication.run(LogisticsApplication.class, args);
    }
}
MAIN

echo "✅ All files generated. Now committing and pushing..."

git add .
git commit -m "Complete backend with PostgreSQL (services, controllers, security)"
git push origin main

echo "🎉 Done! Render will now build the full application."
