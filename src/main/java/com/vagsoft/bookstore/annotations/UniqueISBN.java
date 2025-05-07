package com.vagsoft.bookstore.annotations;

import com.vagsoft.bookstore.validators.UniqueGenresPerBookValidator;
import com.vagsoft.bookstore.validators.UniqueISBNValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that a book has a unique ISBN
 */
@Documented
@Constraint(validatedBy = UniqueISBNValidator.class)
@Target( { ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueISBN {
    /**
     * The error message to be used when the constraint is violated
     *
     * @return the error message
     */
    String message() default "This ISBN already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
