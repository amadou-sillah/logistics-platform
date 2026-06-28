package com.logistics.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "warehouses")
public class Warehouse extends BaseEntity {
    @NotBlank
    @Column(nullable = false)
    private String name;

    private String location;

    @PositiveOrZero
    private Integer capacity;

    @PositiveOrZero
    private Integer currentCapacity = 0;
}
