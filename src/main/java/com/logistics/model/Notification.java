package com.logistics.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {
    
    @Id
    private String id;
    
    @NotBlank(message = "User ID is required")
    @Indexed
    private String userId; // agent or admin ID
    
    @NotBlank(message = "Message is required")
    private String message;
    
    @NotNull(message = "Type is required")
    private NotificationType type; // INFO, WARNING, TASK, DELIVERY, SYSTEM
    
    private boolean read = false;
    
    @Indexed
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Optional: link to related entity
    private String relatedEntityId; // shipmentId, warehouseId, etc.
    private String relatedEntityType; // SHIPMENT, WAREHOUSE, AGENT
    
    public enum NotificationType {
        INFO, WARNING, TASK, DELIVERY, SYSTEM, ALERT
    }
}
