package com.vagsoft.bookstore.dto.userDTOs;

import com.vagsoft.bookstore.repositories.UserRepository;
import com.vagsoft.bookstore.validations.annotations.NullOrNotBlank;
import com.vagsoft.bookstore.validations.annotations.UniqueField;
import com.vagsoft.bookstore.validations.groups.BasicValidation;
import com.vagsoft.bookstore.validations.groups.ExtendedValidation;
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
    @UniqueField(repository = UserRepository.class, methodName = "existsByEmailAndIdNot", pathVariable = "userID", message = "email must be unique", groups = ExtendedValidation.class)
    @NullOrNotBlank(groups = BasicValidation.class)
    @Size(max = 320, message = "email must be less than 321 characters", groups = BasicValidation.class)
    private String email;

    @UniqueField(repository = UserRepository.class, methodName = "existsByUsernameAndIdNot", pathVariable = "userID", message = "username must be unique", groups = ExtendedValidation.class)
    @NullOrNotBlank(groups = BasicValidation.class)
    @Size(max = 31, message = "username must be less than 32 characters", groups = BasicValidation.class)
    private String username;

    @NullOrNotBlank(groups = BasicValidation.class)
    @Size(max = 63, message = "password must be less than 64 characters", groups = BasicValidation.class)
    @Size(min = 8, message = "password must be at least 8 characters", groups = BasicValidation.class)
    private String password;

    @NullOrNotBlank(groups = BasicValidation.class)
    @Size(max = 31, message = "firstName must be less than 32 characters", groups = BasicValidation.class)
    private String firstName;

    @NullOrNotBlank(groups = BasicValidation.class)
    @Size(max = 31, message = "lastName must be less than 32 characters", groups = BasicValidation.class)
    private String lastName;
}
