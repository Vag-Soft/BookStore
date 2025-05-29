package com.vagsoft.bookstore.validations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vagsoft.bookstore.validations.validators.AdminRegistrationValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Validates that the user has the 'ADMIN' role when registering other 'ADMIN'
 * accounts
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AdminRegistrationValidator.class)
public @interface ValidAdminRegistration {
    String message() default "Only accounts with 'ADMIN' role can register accounts for other users";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
