package com.logistics.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Document(collection = "audit_logs")
public class AuditLog extends BaseEntity {
    @NotBlank
    private String userId;
    @NotBlank
    private String action;
    private String entityType;
    private String entityId;
    private LocalDateTime timestamp;
}
