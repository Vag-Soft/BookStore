package com.vagsoft.bookstore.validations.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vagsoft.bookstore.validations.validators.UniqueGenresPerBookValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/** Validates that a book has unique genres. */
@Documented
@Constraint(validatedBy = UniqueGenresPerBookValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueGenresPerBook {
    /**
     * The error message to be used when the constraint is violated.
     *
     * @return the error message
     */
    String message() default "A book cannot have duplicate genres";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
