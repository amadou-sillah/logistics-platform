package com.logistics.controller;

import com.logistics.dto.StatsDto;
import com.logistics.model.Shipment;
import com.logistics.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dashboard")
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
                .filter(s -> s.getCreatedAt().toLocalDate().isAfter(weekAgo.minusDays(1)))
                .collect(Collectors.groupingBy(
                        s -> s.getCreatedAt().toLocalDate().format(DateTimeFormatter.ofPattern("EEE")),
                        Collectors.counting()
                ));
        Map<String, Long> result = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(6 - i);
            String dayName = date.format(DateTimeFormatter.ofPattern("EEE"));
            result.put(dayName, dailyCounts.getOrDefault(dayName, 0L));
        }
        return result.entrySet().stream()
                .map(e -> Map.of("name", e.getKey(), "value", e.getValue().intValue()))
                .collect(Collectors.toList());
    }

    @GetMapping("/status-distribution")
    public List<Map<String, Object>> getStatusDistribution() {
        Map<String, Long> countByStatus = shipmentService.findAll().stream()
                .collect(Collectors.groupingBy(Shipment::getStatus, Collectors.counting()));
        return countByStatus.entrySet().stream()
                .map(e -> Map.of("name", e.getKey(), "value", e.getValue()))
                .collect(Collectors.toList());
    }

    @GetMapping("/recent-shipments")
    public List<Shipment> getRecentShipments() {
        return shipmentService.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(5)
                .collect(Collectors.toList());
    }
}
