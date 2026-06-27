package com.logistics.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "DeliveryAgents".toLowerCase())
public class DeliveryAgent extends BaseEntity {
    @NotBlank
    private String name; // or userId for some
    // add other fields as needed – for simplicity, minimal
}
