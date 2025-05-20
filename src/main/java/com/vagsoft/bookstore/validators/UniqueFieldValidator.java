package com.vagsoft.bookstore.validators;

import com.vagsoft.bookstore.annotations.UniqueField;
import com.vagsoft.bookstore.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.servlet.HandlerMapping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Validates that a resource field is unique
 */
public class UniqueFieldValidator implements ConstraintValidator<UniqueField, Object> {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private HttpServletRequest request;

    private Class<? extends JpaRepository<?, ?>>  repositoryClass;
    private String methodName;
    private String pathVariable;
    private boolean nullable;

    @Override
    public void initialize(UniqueField constraintAnnotation) {
        this.repositoryClass = constraintAnnotation.repository();
        this.methodName = constraintAnnotation.methodName();
        this.pathVariable = constraintAnnotation.pathVariable();
        this.nullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (nullable && value == null) {
            return true;
        }

        var repository = applicationContext.getBean(repositoryClass);
        var requestMethod = request.getMethod();

        switch(requestMethod) {
            case "POST":
                try {
                    Method repositoryMethod = repository.getClass().getMethod(methodName, value.getClass());
                    return ! (boolean) repositoryMethod.invoke(repository, value);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            case "PUT":
                try {
                    Integer userID;
                    if (request.getRequestURI().contains("/me")) {
                        try {
                            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

                            userID = Integer.valueOf(jwt.getClaimAsString("id"));
                        } catch (Exception e) {
                            throw new IllegalStateException("No JWT token found in authenticated request");
                        }
                    } else {
                        userID = RequestUtils.getPathVariable(request, pathVariable, Integer.class);
                    }

                    Method repositoryMethod = repository.getClass().getMethod(methodName, value.getClass(), Integer.class);
                    return ! (boolean) repositoryMethod.invoke(repository, value, userID);

                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + requestMethod);
        }
    }
}
