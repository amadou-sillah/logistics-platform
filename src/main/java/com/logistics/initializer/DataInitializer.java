package com.logistics.initializer;

import com.logistics.model.Role;
import com.logistics.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserService userService;

    @Override
    public void run(String... args) {
        System.out.println("DataInitializer: Checking if admin user exists...");
        if (userService.findByEmail("admin@logistics.com").isEmpty()) {
            System.out.println("DataInitializer: Creating admin user...");
            userService.register("Admin", "admin@logistics.com", "password", Role.ADMIN);
            System.out.println("DataInitializer: Admin user created.");
        }
        if (userService.findByEmail("agent@logistics.com").isEmpty()) {
            System.out.println("DataInitializer: Creating agent user...");
            userService.register("Agent", "agent@logistics.com", "password", Role.AGENT);
            System.out.println("DataInitializer: Agent user created.");
        }
    }
}
