package com.vagsoft.bookstore.validators;

import com.vagsoft.bookstore.annotations.ExistsResource;
import com.vagsoft.bookstore.errors.exceptions.ResourceNotFoundException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/** Validator for the {@link ExistsResource} annotation. */
public class ExistsResourceValidator implements ConstraintValidator<ExistsResource, Integer> {
    @Autowired
    private ApplicationContext applicationContext;

    private Class<? extends JpaRepository<?, Integer>> repositoryClass;
    private boolean nullable;

    @Override
    public void initialize(ExistsResource constraintAnnotation) {
        this.repositoryClass = constraintAnnotation.repository();
        this.nullable = constraintAnnotation.nullable();
    }
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
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
