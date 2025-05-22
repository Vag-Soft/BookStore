package com.vagsoft.bookstore.validators;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.vagsoft.bookstore.annotations.UniqueCompositeFields;
import com.vagsoft.bookstore.utils.AuthUtils;
import com.vagsoft.bookstore.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;

/** Validator for the {@link UniqueCompositeFields} annotation. */
public class UniqueCompositeFieldsValidator implements ConstraintValidator<UniqueCompositeFields, Object> {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private AuthUtils authUtils;

    private Class<? extends JpaRepository<?, ?>> repositoryClass;
    private String methodName;
    private String pathVariable;
    private Class<?> dtoClass;
    private String dtoFieldName;
    private boolean usePathVariable;

    @Override
    public void initialize(UniqueCompositeFields constraintAnnotation) {
        this.repositoryClass = constraintAnnotation.repository();
        this.methodName = constraintAnnotation.methodName();
        this.pathVariable = constraintAnnotation.pathVariable();
        this.dtoClass = constraintAnnotation.dtoClass();
        this.dtoFieldName = constraintAnnotation.dtoFieldName();
        this.usePathVariable = constraintAnnotation.usePathVariable();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        var repository = applicationContext.getBean(repositoryClass);

        try {
            // Getting the ID from the path variable or the JWT
            Integer resourceID;
            if (usePathVariable) {
                resourceID = RequestUtils.getPathVariable(request, pathVariable, Integer.class);
            } else {
                resourceID = authUtils.getUserIdFromAuthentication();
            }

            // Getting the field value from the DTO
            var field = dtoClass.getDeclaredField(dtoFieldName);
            field.setAccessible(true);
            var fieldValue = field.get(value);

            // Running the repository method
            Method repositoryMethod = repository.getClass().getMethod(methodName, Integer.class, fieldValue.getClass());
            return !(boolean) repositoryMethod.invoke(repository, resourceID, fieldValue);
        } catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
