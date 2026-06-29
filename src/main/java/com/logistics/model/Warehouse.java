package com.logistics.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "warehouses")
public class Warehouse {
    
    @Id
    private String id;
    
    @NotBlank(message = "Warehouse name is required")
    @Size(min = 2, max = 100)
    @Indexed(unique = true)
    private String name;
    
    // ✅ ADD THIS - The missing field that's causing the error
    @NotBlank(message = "Warehouse code is required")
    @Size(min = 3, max = 10)
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Code must be uppercase letters and numbers")
    @Indexed(unique = true)
    private String code;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    private Address address;
    private Coordinates coordinates;
    private Integer capacity = 1000;
    private Integer currentOccupancy = 0;
    private Manager manager;
    private Contact contact;
    private OperatingHours operatingHours;
    private Boolean isActive = true;
    private String type = "DISTRIBUTION";
    private List<String> features;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        private String street;
        private String city;
        private String state;
        private String country;
        private String zipCode;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Coordinates {
        private Double latitude;
        private Double longitude;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Manager {
        private String name;
        private String email;
        private String phone;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Contact {
        private String phone;
        private String email;
        private String emergency;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OperatingHours {
        private String open = "09:00";
        private String close = "18:00";
        private String timezone = "UTC";
    }
}
