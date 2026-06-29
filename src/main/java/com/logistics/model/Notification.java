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

    @NotBlank
    @Indexed
    private String userId;

    @NotBlank
    private String message;

    @NotNull
    private NotificationType type;

    private boolean read = false;

    @Indexed
    private LocalDateTime createdAt = LocalDateTime.now();

    private String relatedEntityId;
    private String relatedEntityType;

    public enum NotificationType {
        INFO, WARNING, TASK, DELIVERY, SYSTEM, ALERT
    }
}