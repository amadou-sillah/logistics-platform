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
