package com.vagsoft.bookstore.dto.userDTOs;

import com.vagsoft.bookstore.validations.groups.BasicValidation;
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
public class UserLoginDTO {
    @NotBlank(message = "username must not be blank", groups = BasicValidation.class)
    @Size(max = 31, message = "username must be less than 32 characters", groups = BasicValidation.class)
    private String username;

    @NotBlank(message = "password must not be blank", groups = BasicValidation.class)
    @Size(max = 63, message = "password must be less than 64 characters", groups = BasicValidation.class)
    private String password;
}
