package com.logistics.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "delivery_agents")
public class DeliveryAgent extends BaseEntity {
    @NotBlank
    private String userId;
    private String vehicleNumber;
    private String phoneNumber;
    private boolean active = true;
}
