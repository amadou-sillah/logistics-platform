package com.logistics.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TrackingEventDto {
    private String shipmentId;
    private String eventType;
    private String description;
    private String location;
    private Double latitude;
    private Double longitude;
    private LocalDateTime occurredAt;
}
