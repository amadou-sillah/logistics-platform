package com.logistics.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "shipments")
public class Shipment {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String trackingNumber;
    
    private String assignedAgentId;
    private String warehouseId;
    private String status; // PENDING, ASSIGNED, PICKED_UP, IN_TRANSIT, DELIVERED, FAILED
    private String description;
    private String origin;
    private String destination;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deliveredAt;
    
    private Double weight;
    private Double price;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    
    // Priority field for UrgentShipmentQueue
    private Integer priority = 0; // 0 = normal, 1 = high, 2 = urgent
    
    // Getter for priority (used by UrgentShipmentQueue)
    public Integer getPriority() {
        return priority != null ? priority : 0;
    }
    
    // Setter for priority
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
