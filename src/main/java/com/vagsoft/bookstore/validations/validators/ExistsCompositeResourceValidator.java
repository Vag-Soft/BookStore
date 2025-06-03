package com.vagsoft.bookstore.validations.validators;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.vagsoft.bookstore.errors.exceptions.ResourceNotFoundException;
import com.vagsoft.bookstore.utils.AuthUtils;
import com.vagsoft.bookstore.utils.RequestUtils;
import com.vagsoft.bookstore.validations.annotations.ExistsCompositeResource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;

/** Validator for the {@link ExistsCompositeResource} annotation. */
public class ExistsCompositeResourceValidator implements ConstraintValidator<ExistsCompositeResource, Object> {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private AuthUtils authUtils;

    private Class<? extends JpaRepository<?, ?>> repositoryClass;
    private String methodName;
    String firstPathVariable;
    String secondPathVariable;
    private boolean useJWT;

    @Override
    public void initialize(ExistsCompositeResource constraintAnnotation) {
        this.repositoryClass = constraintAnnotation.repository();
        this.methodName = constraintAnnotation.methodName();
        this.firstPathVariable = constraintAnnotation.firstPathVariable();
        this.secondPathVariable = constraintAnnotation.secondPathVariable();
        this.useJWT = constraintAnnotation.useJWT();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        var repository = applicationContext.getBean(repositoryClass);

        try {
            // Getting the IDs from the path variable or the JWT
            Integer firstResourceID;
            Integer secondResourceID;
            if (useJWT) {
                firstResourceID = authUtils.getUserIdFromAuthentication();
            } else {
                firstResourceID = RequestUtils.getPathVariable(request, firstPathVariable, Integer.class);
            }
            secondResourceID = RequestUtils.getPathVariable(request, secondPathVariable, Integer.class);

            // Running the repository method
            Method repositoryMethod = repository.getClass().getMethod(methodName, Integer.class, Integer.class);

            if (!(boolean) repositoryMethod.invoke(repository, firstResourceID, secondResourceID)) {
                throw new ResourceNotFoundException("Composite resource with IDs: " + firstResourceID + " and "
                        + secondResourceID + " does not exist");
            }

            return true;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
