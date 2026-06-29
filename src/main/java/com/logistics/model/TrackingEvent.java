package com.logistics.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tracking_events")
public class TrackingEvent {
    
    @Id
    private String id;
    
    @Indexed
    private String shipmentId;
    
    private String status;
    private String location;
    private String description;
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;
}
