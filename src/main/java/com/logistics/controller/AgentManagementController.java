package com.logistics.controller;

import com.logistics.model.DeliveryAgent;
import com.logistics.repository.DeliveryAgentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/agents")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class AgentManagementController {

    private final DeliveryAgentRepository deliveryAgentRepository;

    @GetMapping
    public ResponseEntity<List<DeliveryAgent>> getAll() {
        try {
            return ResponseEntity.ok(deliveryAgentRepository.findAll());
        } catch (Exception e) {
            log.error("Error fetching agents: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody DeliveryAgent agent) {
        try {
            log.info("Creating agent: {}", agent);
            if (agent.getUserId() == null || agent.getUserId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("User ID is required");
            }
            agent.setCreatedAt(LocalDateTime.now());
            agent.setUpdatedAt(LocalDateTime.now());
            DeliveryAgent saved = deliveryAgentRepository.save(agent);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            log.error("Error creating agent: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating agent: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            if (!deliveryAgentRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Agent not found");
            }
            deliveryAgentRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting agent: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting agent: " + e.getMessage());
        }
    }
}
