package com.logistics.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Document(collection = "shipments")
public class Shipment extends BaseEntity {
    @NotBlank @Indexed(unique = true)
    private String trackingNumber;
    private String customerId;
    private String origin;
    private String destination;
    private String status;
    private LocalDateTime eta;
    @Positive
    private Double amount;
    private String priority;
}
