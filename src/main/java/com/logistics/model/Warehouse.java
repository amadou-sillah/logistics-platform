package com.logistics.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Document(collection = "warehouses")
public class Warehouse extends BaseEntity {
    @NotBlank
    private String name;
    private String location;
    @PositiveOrZero
    private Integer capacity;
    @PositiveOrZero
    private Integer currentCapacity = 0;
}
