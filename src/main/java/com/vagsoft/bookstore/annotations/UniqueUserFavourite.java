package com.vagsoft.bookstore.annotations;

import com.vagsoft.bookstore.validators.UniqueUserFavouriteValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that a use has unique favourite books
 */
@Documented
@Constraint(validatedBy = UniqueUserFavouriteValidator.class)
@Target( { ElementType.TYPE, ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueUserFavourite {
    /**
     * The error message to be used when the constraint is violated
     */
    String message() default "User already has this book in their favourites";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
