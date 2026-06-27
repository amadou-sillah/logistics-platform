package com.logistics.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tracking_events")
public class TrackingEvent extends BaseEntity {
    @NotBlank
    private String shipmentId;

    private String eventType;

    private String description;

    private String location;

    private Double latitude;

    private Double longitude;

    private LocalDateTime occurredAt = LocalDateTime.now();
}
