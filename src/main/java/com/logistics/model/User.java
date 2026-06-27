package com.logistics.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "users")
public class User extends BaseEntity {
    @NotBlank
    private String name;
    @Email @NotBlank
    @Indexed(unique = true)
    private String email;
    @NotBlank
    private String password;
    private Role role = Role.CUSTOMER;
    private boolean active = true;
}
