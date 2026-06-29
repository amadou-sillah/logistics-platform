package com.logistics.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "shipments")
public class Shipment {

    @Id
    private String id;

    private String trackingNumber;

    private String customerId;

    private String origin;

    private String destination;

    // ✅ FIX: must be String (you already use "DELIVERED", "PENDING", etc.)
    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime eta;

    private Double amount;

    // 🔥 FIXED: was Integer → now Enum (matches frontend "STANDARD")
    private Priority priority;

    private String agentId;

    private String warehouseId;
}