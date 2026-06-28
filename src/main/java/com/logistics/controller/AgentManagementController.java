package com.logistics.controller;

import com.logistics.model.DeliveryAgent;
import com.logistics.repository.DeliveryAgentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agents")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AgentManagementController {

    private final DeliveryAgentRepository deliveryAgentRepository;

    @GetMapping
    public List<DeliveryAgent> getAll() {
        return deliveryAgentRepository.findAll();
    }

    @PostMapping
    public DeliveryAgent create(@RequestBody DeliveryAgent agent) {
        agent.setCreatedAt(java.time.LocalDateTime.now());
        agent.setUpdatedAt(java.time.LocalDateTime.now());
        return deliveryAgentRepository.save(agent);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        deliveryAgentRepository.deleteById(id);
    }
}
