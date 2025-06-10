package com.vagsoft.bookstore.validations.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vagsoft.bookstore.validations.validators.UniqueCompositeFieldsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.data.jpa.repository.JpaRepository;

/** Validates that two resource fields are unique together. */
@Documented
@Constraint(validatedBy = UniqueCompositeFieldsValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueCompositeFields {
    /** The repository class to use for uniqueness validation. */
    Class<? extends JpaRepository<?, ?>> repository();

    /**
     * The name of the method in the repository to use for uniqueness validation.
     */
    String methodName();

    /** The name of the path variable to use for uniqueness validation. */
    String pathVariable() default "";

    /** The class type of the DTO to use for uniqueness validation. */
    Class<?> dtoClass();

    /** The name of the path variable to use for uniqueness validation. */
    String dtoFieldName();

    /** Whether to use a path variable or not. */
    boolean usePathVariable() default true;

    /** The error message to be used when the constraint is violated. */
    String message() default "Unique constraint violated";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
