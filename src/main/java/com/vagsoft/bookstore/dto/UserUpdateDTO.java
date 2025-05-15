package com.vagsoft.bookstore.dto;

import com.vagsoft.bookstore.annotations.NullOrNotBlank;
import com.vagsoft.bookstore.annotations.UniqueField;
import com.vagsoft.bookstore.models.enums.Role;
import com.vagsoft.bookstore.repositories.UserRepository;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class UserUpdateDTO {
    @UniqueField(repository = UserRepository.class, methodName = "existsByEmailAndIdNot", pathVariable = "userID", message = "email must be unique")
    @NullOrNotBlank
    @Size(max = 320, message = "email must be less than 321 characters")
    private String email;

    @UniqueField(repository = UserRepository.class, methodName = "existsByUsernameAndIdNot", pathVariable = "userID", message = "username must be unique")
    @NullOrNotBlank
    @Size(max = 31, message = "username must be less than 32 characters")
    private String username;

    @NullOrNotBlank
    @Size(max = 63, message = "password must be less than 64 characters")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @NullOrNotBlank
    @Size(max = 31, message = "firstName must be less than 32 characters")
    private String firstName;

    @NullOrNotBlank
    @Size(max = 31, message = "lastName must be less than 32 characters")
    private String lastName;
}
