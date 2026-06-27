package com.logistics.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "warehouses")
public class Warehouse extends BaseEntity {
    @NotBlank
    private String name;

    private String location;

    @PositiveOrZero
    private Integer capacity;

    @PositiveOrZero
    private Integer currentCapacity;
}
