package com.logistics.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "agents")
public class Agent {
    
    @Id
    private String id;
    
    @NotBlank(message = "User ID is required")
    @Size(min = 3, max = 20)
    @Pattern(regexp = "^[A-Z0-9]+$")
    @Indexed(unique = true)
    private String userId;
    
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 50)
    private String fullName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    @Indexed(unique = true)
    private String email;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$")
    private String phone;
    
    @NotBlank(message = "Vehicle type is required")
    private String vehicle;
    
    private String vehicleNumber;
    
    private Boolean active = true;
    private Boolean isAvailable = true;
    
    // ✅ ADDED: Missing field that was causing the error
    private String warehouseId;
    
    private Double rating = 0.0;
    private Integer totalDeliveries = 0;
    private LocalDateTime joinedDate = LocalDateTime.now();
}
