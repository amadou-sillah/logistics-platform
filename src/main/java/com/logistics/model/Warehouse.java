package com.logistics.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "Warehouses".toLowerCase())
public class Warehouse extends BaseEntity {
    @NotBlank
    private String name; // adjust as needed
}
