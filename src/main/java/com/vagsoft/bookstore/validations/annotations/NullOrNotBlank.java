package com.vagsoft.bookstore.validations.annotations;

import java.lang.annotation.*;

import com.vagsoft.bookstore.validations.validators.NullOrNotBlankValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/** Validates that a field is null or not blank */
@Documented
@Constraint(validatedBy = NullOrNotBlankValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NullOrNotBlank {
    /**
     * The error message to be used when the constraint is violated
     *
     * @return the error message
     */
    String message() default "Should not be blank";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
