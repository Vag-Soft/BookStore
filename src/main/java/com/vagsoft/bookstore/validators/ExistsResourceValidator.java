package com.vagsoft.bookstore.validators;

import com.vagsoft.bookstore.annotations.ExistsResource;
import com.vagsoft.bookstore.annotations.UniqueField;
import com.vagsoft.bookstore.utils.AuthUtils;
import com.vagsoft.bookstore.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Validator for the {@link ExistsResource} annotation.
 */
public class ExistsResourceValidator implements ConstraintValidator<ExistsResource, Integer> {
    @Autowired
    private ApplicationContext applicationContext;

    private Class<? extends JpaRepository<?, Integer>>  repositoryClass;

    @Override
    public void initialize(ExistsResource constraintAnnotation) {
        this.repositoryClass = constraintAnnotation.repository();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        var repository = applicationContext.getBean(repositoryClass);

        return repository.existsById(value);
    }
}
