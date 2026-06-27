package com.logistics.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "audit_logs")
public class AuditLog extends BaseEntity {
    @NotBlank
    private String userId;
    @NotBlank
    private String action;
    private String entityType;
    private String entityId;
    private LocalDateTime timestamp = LocalDateTime.now();
}
