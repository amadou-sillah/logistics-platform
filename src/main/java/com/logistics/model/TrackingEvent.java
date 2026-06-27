package com.logistics.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "tracking_events")
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
