package com.logistics.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "warehouses")
public class Warehouse extends BaseEntity {

    @NotBlank(message = "Warehouse name is required")
    private String name;

    private String location;

    @PositiveOrZero(message = "Capacity cannot be negative")
    private Integer capacity;

    @PositiveOrZero(message = "Current capacity cannot be negative")
    private Integer currentCapacity = 0;
}