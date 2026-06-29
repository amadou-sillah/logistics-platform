package com.logistics.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Document(collection = "notifications")
public class Notification extends BaseEntity {
    @NotBlank
    private String userId;
    private String title;
    private String message;
    private boolean read = false;
}
