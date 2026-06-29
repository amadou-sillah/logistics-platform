package com.logistics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import com.logistics.model.Notification;

@Data
public class NotificationRequest {

    @NotBlank
    private String userId;

    @NotBlank
    private String message;

    @NotNull
    private Notification.NotificationType type;

    private String relatedEntityId;
    private String relatedEntityType;
}