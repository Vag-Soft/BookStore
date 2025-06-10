package com.vagsoft.bookstore.validations.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vagsoft.bookstore.validations.validators.ExistsCompositeResourceValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.data.jpa.repository.JpaRepository;

/** Validates that two resource fields are unique together. */
@Documented
@Constraint(validatedBy = ExistsCompositeResourceValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistsCompositeResource {
    /** The repository class to use for uniqueness validation. */
    Class<? extends JpaRepository<?, ?>> repository();

    /**
     * The name of the method in the repository to use for uniqueness validation.
     */
    String methodName();

    /** The first name of the path variable to use for uniqueness validation. */
    String firstPathVariable() default "";

    /** The second name of the path variable to use for uniqueness validation. */
    String secondPathVariable() default "";

    /** Whether to get the user info from JWT or not. */
    boolean useJWT() default false;

    /** The error message to be used when the constraint is violated. */
    String message() default "Composite resource does not exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
