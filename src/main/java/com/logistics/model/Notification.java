package com.logistics.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity {
    @NotBlank
    private String userId;

    private String title;

    private String message;

    private boolean read = false;
}
