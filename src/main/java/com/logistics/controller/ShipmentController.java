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
