package com.logistics.controller;

import com.logistics.model.Warehouse;
import com.logistics.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warehouses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class WarehouseController {

    private final WarehouseRepository warehouseRepository;

    @GetMapping
    public List<Warehouse> getAll() {
        return warehouseRepository.findAll();
    }

    @PostMapping
    public Warehouse create(@RequestBody Warehouse warehouse) {
        warehouse.setCreatedAt(java.time.LocalDateTime.now());
        warehouse.setUpdatedAt(java.time.LocalDateTime.now());
        return warehouseRepository.save(warehouse);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        warehouseRepository.deleteById(id);
    }
}
