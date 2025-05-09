package com.vagsoft.bookstore.annotations;

import com.vagsoft.bookstore.validators.UniqueFieldValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.annotation.*;

/**
 * Validates that a resource field is unique
 */
@Documented
@Constraint(validatedBy = UniqueFieldValidator.class)
@Target( { ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueField {
    /**
     * The repository class to use for uniqueness validation
     */
    Class<? extends JpaRepository<?, ?>> repository();

    /**
     * The name of the method in the repository to use for uniqueness validation
     */
    String methodName();

    /**
     * The name of the path variable to use for uniqueness validation
     */
    String pathVariable() default "id";

    /**
     * The error message to be used when the constraint is violated
     *
     * @return the error message
     */
    boolean nullable() default true;

    /**
     * The error message to be used when the constraint is violated
     *
     * @return the error message
     */
    String message() default "Unique constraint violated";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
