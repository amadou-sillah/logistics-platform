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
    @Size(min = 3, max = 20, message = "User ID must be between 3 and 20 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "User ID can only contain uppercase letters and numbers")
    @Indexed(unique = true)
    private String userId;
    
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String fullName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    @Indexed(unique = true)
    private String email;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Please provide a valid phone number")
    private String phone;
    
    @NotBlank(message = "Vehicle type is required")
    private String vehicle;
    
    private String vehicleNumber;
    
    private Boolean active = true;
    private Boolean isAvailable = true;
    
    private Double rating = 0.0;
    private Integer totalDeliveries = 0;
    private LocalDateTime joinedDate = LocalDateTime.now();
}
