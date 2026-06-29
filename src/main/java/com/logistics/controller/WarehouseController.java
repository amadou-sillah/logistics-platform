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
import java.util.List;

@RestController
@RequestMapping("/warehouses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class WarehouseController {

    private final WarehouseRepository warehouseRepository;

    @GetMapping
    public ResponseEntity<List<Warehouse>> getAll() {
        try {
            return ResponseEntity.ok(warehouseRepository.findAll());
        } catch (Exception e) {
            log.error("Error fetching warehouses: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Warehouse warehouse) {
        try {
            log.info("Creating warehouse: {}", warehouse);
            if (warehouse.getName() == null || warehouse.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Warehouse name is required");
            }
            warehouse.setCreatedAt(LocalDateTime.now());
            warehouse.setUpdatedAt(LocalDateTime.now());
            Warehouse saved = warehouseRepository.save(warehouse);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            log.error("Error creating warehouse: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating warehouse: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            if (!warehouseRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Warehouse not found");
            }
            warehouseRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting warehouse: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting warehouse: " + e.getMessage());
        }
    }
}
