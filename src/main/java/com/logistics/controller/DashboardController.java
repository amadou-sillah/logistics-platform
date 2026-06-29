package com.logistics.controller;

import com.logistics.dto.StatsDto;
import com.logistics.model.Shipment;
import com.logistics.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final ShipmentService shipmentService;

    @GetMapping("/stats")
    public List<StatsDto> getStats() {
        List<Shipment> all = shipmentService.findAll();

        long total = all.size();
        long delivered = all.stream().filter(s -> "DELIVERED".equals(s.getStatus())).count();
        long inTransit = all.stream().filter(s -> "IN_TRANSIT".equals(s.getStatus())).count();
        long pending = all.stream().filter(s -> "PENDING".equals(s.getStatus())).count();

        return List.of(
                new StatsDto("Total Shipments", String.valueOf(total), "Package", "+12%", true),
                new StatsDto("In Transit", String.valueOf(inTransit), "Truck", "+4%", true),
                new StatsDto("Delivered", String.valueOf(delivered), "Users", "-2%", false),
                new StatsDto("Pending", String.valueOf(pending), "DollarSign", "+18%", true)
        );
    }

    @GetMapping("/weekly-shipments")
    public List<Map<String, Object>> getWeeklyShipments() {
        List<Shipment> all = shipmentService.findAll();

        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6);

        Map<String, Long> dailyCounts = all.stream()
                .filter(s -> s.getCreatedAt() != null)
                .filter(s -> !s.getCreatedAt().toLocalDate().isBefore(weekAgo))
                .collect(Collectors.groupingBy(
                        s -> s.getCreatedAt().toLocalDate().format(DateTimeFormatter.ofPattern("EEE")),
                        Collectors.counting()
                ));

        List<Map<String, Object>> result = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String day = date.format(DateTimeFormatter.ofPattern("EEE"));

            Map<String, Object> map = new HashMap<>();
            map.put("name", day);
            map.put("value", dailyCounts.getOrDefault(day, 0L));

            result.add(map);
        }

        return result;
    }

    @GetMapping("/status-distribution")
    public List<Map<String, Object>> getStatusDistribution() {
        return shipmentService.findAll().stream()
                .collect(Collectors.groupingBy(Shipment::getStatus, Collectors.counting()))
                .entrySet().stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", e.getKey());
                    map.put("value", e.getValue());
                    return map;
                })
                .toList();
    }

    @GetMapping("/recent-shipments")
    public List<Shipment> getRecentShipments() {
        return shipmentService.findAll().stream()
                .filter(s -> s.getCreatedAt() != null)
                .sorted(Comparator.comparing(Shipment::getCreatedAt).reversed())
                .limit(5)
                .toList();
    }
}