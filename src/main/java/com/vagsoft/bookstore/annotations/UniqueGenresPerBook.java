package com.vagsoft.bookstore.annotations;

import com.vagsoft.bookstore.validators.NullOrNotBlankValidator;
import com.vagsoft.bookstore.validators.UniqueGenresPerBookValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that a book has unique genres
 */
@Documented
@Constraint(validatedBy = UniqueGenresPerBookValidator.class)
@Target( { ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueGenresPerBook {
    /**
     * The error message to be used when the constraint is violated
     *
     * @return the error message
     */
    String message() default "A book cannot have duplicate genres";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
