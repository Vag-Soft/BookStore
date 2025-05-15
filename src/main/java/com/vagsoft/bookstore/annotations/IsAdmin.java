package com.vagsoft.bookstore.annotations;

import com.vagsoft.bookstore.validators.NullOrNotBlankValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

/**
 * Annotation to check if the user has ADMIN role
 */
@PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
@Documented
@Target( { ElementType.METHOD } )
@Retention(RetentionPolicy.RUNTIME)
public @interface IsAdmin {
}
