package com.vagsoft.bookstore.validations.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vagsoft.bookstore.validations.validators.ExistsResourceValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.data.jpa.repository.JpaRepository;

/** Validates that a resource exists. */
@Documented
@Constraint(validatedBy = ExistsResourceValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistsResource {
    /** The repository class to use for existence validation. */
    Class<? extends JpaRepository<?, Integer>> repository();

    /** Whether the resource can be null. */
    boolean nullable() default false;

    /** The error message to be used when the constraint is violated. */
    String message() default "Resource not found with the given ID";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
