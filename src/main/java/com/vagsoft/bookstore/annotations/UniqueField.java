package com.vagsoft.bookstore.annotations;

import java.lang.annotation.*;

import com.vagsoft.bookstore.validators.UniqueFieldValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.data.jpa.repository.JpaRepository;

/** Validates that a resource field is unique */
@Documented
@Constraint(validatedBy = UniqueFieldValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueField {
    /** The repository class to use for uniqueness validation */
    Class<? extends JpaRepository<?, ?>> repository();

    /** The name of the method in the repository to use for uniqueness validation */
    String methodName();

    /** The name of the path variable to use for uniqueness validation */
    String pathVariable() default "";

    /** Whether the field can be null or not. If true, the field can be null and */
    boolean nullable() default true;

    /** The error message to be used when the constraint is violated */
    String message() default "Unique constraint violated";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
