package com.logistics.controller;

import com.logistics.model.Warehouse;
import com.logistics.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class WarehouseController {

    private final WarehouseRepository warehouseRepository;

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<Warehouse> warehouses = warehouseRepository.findAll();
            log.info("Found {} warehouses", warehouses.size());
            return ResponseEntity.ok(warehouses);
        } catch (Exception e) {
            log.error("Error fetching warehouses: ", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Warehouse warehouse) {
        try {
            log.info("Creating warehouse: {}", warehouse);
            if (warehouse.getName() == null || warehouse.getName().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Warehouse name is required");
                return ResponseEntity.badRequest().body(error);
            }
            warehouse.setCreatedAt(LocalDateTime.now());
            warehouse.setUpdatedAt(LocalDateTime.now());
            Warehouse saved = warehouseRepository.save(warehouse);
            log.info("Warehouse created with ID: {}", saved.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            log.error("Error creating warehouse: ", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            if (!warehouseRepository.existsById(id)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Warehouse not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            warehouseRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting warehouse: ", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
