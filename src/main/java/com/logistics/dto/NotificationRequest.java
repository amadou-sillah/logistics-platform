package com.logistics.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotificationRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Message is required")
    private String message;

    // 🔥 FIX: use String instead of Enum to prevent conversion crash
    private String type;

    private String relatedEntityId;
    private String relatedEntityType;
}
