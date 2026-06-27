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
