package com.logistics.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "shipments")
public class Shipment extends BaseEntity {
    @NotBlank
    @Column(unique = true)
    private String trackingNumber;

    private String customerId;

    private String origin;

    private String destination;

    private String status;

    private LocalDateTime eta;

    @Positive
    private Double amount;   // ensures getAmount() exists

    private String priority;
}
