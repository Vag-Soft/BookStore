package com.vagsoft.bookstore.validations.validators;

import com.vagsoft.bookstore.errors.exceptions.ResourceNotFoundException;
import com.vagsoft.bookstore.validations.annotations.ExistsResource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;

/** Validator for the {@link ExistsResource} annotation. */
public class ExistsResourceValidator implements ConstraintValidator<ExistsResource, Integer> {
    @Autowired
    private ApplicationContext applicationContext;

    private Class<? extends JpaRepository<?, Integer>> repositoryClass;
    private boolean nullable;

    @Override
    public void initialize(final ExistsResource constraintAnnotation) {
        this.repositoryClass = constraintAnnotation.repository();
        this.nullable = constraintAnnotation.nullable();
    }
    @Override
    public boolean isValid(final Integer value, final ConstraintValidatorContext context) {
        if (nullable && value == null) {
            return true;
        }

        var repository = applicationContext.getBean(repositoryClass);

        if (!repository.existsById(value)) {
            throw new ResourceNotFoundException("Resource with ID: " + value + " does not exist");
        }

        return true;
    }
}
