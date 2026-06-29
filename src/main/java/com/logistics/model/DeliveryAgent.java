package com.logistics.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Document(collection = "delivery_agents")
public class DeliveryAgent extends BaseEntity {
    @NotBlank
    private String userId;
    private String vehicleNumber;
    private String phoneNumber;
    private boolean active = true;
}
