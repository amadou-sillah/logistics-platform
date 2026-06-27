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
        if (userService.findByEmail("admin@logistics.com").isEmpty()) {
            log.info("Creating admin user...");
            userService.register("Admin", "admin@logistics.com", "password", Role.ADMIN);
        }
        if (userService.findByEmail("agent@logistics.com").isEmpty()) {
            log.info("Creating agent user...");
            userService.register("Agent", "agent@logistics.com", "password", Role.AGENT);
        }
    }
}
