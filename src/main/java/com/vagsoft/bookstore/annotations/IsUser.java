package com.vagsoft.bookstore.annotations;

import jakarta.validation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

/**
 * Annotation to check if the user has USER role
 */
@PreAuthorize("hasAuthority('SCOPE_ROLE_USER')")
@Documented
@Target( { ElementType.METHOD } )
@Retention(RetentionPolicy.RUNTIME)
public @interface IsUser {
}
