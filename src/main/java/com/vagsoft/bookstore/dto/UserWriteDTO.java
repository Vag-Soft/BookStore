package com.vagsoft.bookstore.dto;

import com.vagsoft.bookstore.models.Cart;
import com.vagsoft.bookstore.models.Favourite;
import com.vagsoft.bookstore.models.Order;
import com.vagsoft.bookstore.models.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWriteDTO {
    @NotBlank(message = "email must not be blank")
    @Size(max = 320, message = "email must be less than 321 characters")
    private String email;

    @NotBlank(message = "username must not be blank")
    @Size(max = 31, message = "username must be less than 32 characters")
    private String username;

    @NotBlank(message = "password must not be blank")
    @Size(max = 63, message = "password must be less than 64 characters")
    private String password;

    @Enumerated(EnumType.STRING) //TODO: check validation
    private Role role;

    @NotBlank(message = "firstName must not be blank")
    @Size(max = 31, message = "firstName must be less than 32 characters")
    private String firstName;

    @NotBlank(message = "lastName must not be blank")
    @Size(max = 31, message = "lastName must be less than 32 characters")
    private String lastName;
}
