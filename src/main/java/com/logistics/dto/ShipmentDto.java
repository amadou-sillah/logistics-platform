package com.logistics.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ShipmentDto {
    private String id;
    private String trackingNumber;
    private String customerId;
    private String origin;
    private String destination;
    private String status;
    private LocalDateTime eta;
    private Double amount;
    private String priority;
}
