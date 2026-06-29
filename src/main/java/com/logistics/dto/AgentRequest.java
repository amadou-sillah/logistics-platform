package com.logistics.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AgentRequest {
    
    @NotBlank(message = "User ID is required")
    @Size(min = 3, max = 20, message = "User ID must be between 3 and 20 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "User ID can only contain uppercase letters and numbers")
    private String userId;
    
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String fullName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    private String email;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Please provide a valid phone number")
    private String phone;
    
    @NotBlank(message = "Vehicle type is required")
    private String vehicle;
    
    private String vehicleNumber;
}
