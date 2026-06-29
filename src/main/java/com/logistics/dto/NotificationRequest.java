package com.logistics.dto;

import com.logistics.model.Notification;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationRequest {
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "Message is required")
    private String message;
    
    @NotNull(message = "Type is required")
    private Notification.NotificationType type;
    
    private String relatedEntityId;
    private String relatedEntityType;
}
