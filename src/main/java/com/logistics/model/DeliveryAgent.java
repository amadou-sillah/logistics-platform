package com.logistics.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "delivery_agents")
public class DeliveryAgent extends BaseEntity {

    @NotBlank(message = "User ID is required")
    private String userId;

    private String vehicleNumber;

    private String phoneNumber;

    private boolean active = true;
}
