package com.vagsoft.bookstore.validations.validators;

import com.vagsoft.bookstore.dto.userDTOs.UserWriteDTO;
import com.vagsoft.bookstore.models.enums.Role;
import com.vagsoft.bookstore.validations.annotations.ValidAdminRegistration;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Validates that the user has the 'ADMIN' role when registering other 'ADMIN'
 * accounts.
 */
public class AdminRegistrationValidator implements ConstraintValidator<ValidAdminRegistration, UserWriteDTO> {
    @Override
    public void initialize(final ValidAdminRegistration constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(final UserWriteDTO userWriteDTO,
            final ConstraintValidatorContext constraintValidatorContext) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is trying to register an 'ADMIN' account
        if (userWriteDTO.getRole().equals(Role.ADMIN)) {
            // Check if the user that made the request has the 'ADMIN' role
            return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                    .anyMatch(authority -> authority.equals("SCOPE_ROLE_ADMIN"));
        } else if (userWriteDTO.getRole().equals(Role.USER)) {
            // Check if the user that made the request has the 'ADMIN' role or is anonymous
            return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                    .anyMatch(authority -> authority.equals("SCOPE_ROLE_ADMIN") || authority.equals("ROLE_ANONYMOUS"));
        }
        return true;
    }
}
