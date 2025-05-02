package com.vagsoft.bookstore.dto;

import com.vagsoft.bookstore.models.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserUpdateDTO {
    @Size(max = 320, message = "email must be less than 321 characters")
    private String email;

    @Size(max = 31, message = "username must be less than 32 characters")
    private String username;

    @Size(max = 63, message = "password must be less than 64 characters")
    private String password;

    @Enumerated(EnumType.STRING) //TODO: check validation
    private Role role;

    @Size(max = 31, message = "firstName must be less than 32 characters")
    private String firstName;

    @Size(max = 31, message = "lastName must be less than 32 characters")
    private String lastName;
}
