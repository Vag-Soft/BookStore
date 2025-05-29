package com.vagsoft.bookstore.dto.userDTOs;

import com.vagsoft.bookstore.validations.annotations.UniqueField;
import com.vagsoft.bookstore.models.enums.Role;
import com.vagsoft.bookstore.repositories.UserRepository;
import com.vagsoft.bookstore.validations.groups.BasicValidation;
import com.vagsoft.bookstore.validations.groups.ExtendedValidation;
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
    @UniqueField(repository = UserRepository.class, methodName = "existsByEmail", message = "email must be unique", groups = ExtendedValidation.class)
    @NotBlank(message = "email must not be blank", groups = BasicValidation.class)
    @Size(max = 320, message = "email must be less than 321 characters", groups = BasicValidation.class)
    private String email;

    @UniqueField(repository = UserRepository.class, methodName = "existsByUsername", message = "username must be unique", groups = ExtendedValidation.class)
    @NotBlank(message = "username must not be blank", groups = BasicValidation.class)
    @Size(max = 31, message = "username must be less than 32 characters", groups = BasicValidation.class)
    private String username;

    @NotBlank(message = "password must not be blank", groups = BasicValidation.class)
    @Size(max = 63, message = "password must be less than 64 characters", groups = BasicValidation.class)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @NotBlank(message = "firstName must not be blank", groups = BasicValidation.class)
    @Size(max = 31, message = "firstName must be less than 32 characters", groups = BasicValidation.class)
    private String firstName;

    @NotBlank(message = "lastName must not be blank", groups = BasicValidation.class)
    @Size(max = 31, message = "lastName must be less than 32 characters", groups = BasicValidation.class)
    private String lastName;
}
