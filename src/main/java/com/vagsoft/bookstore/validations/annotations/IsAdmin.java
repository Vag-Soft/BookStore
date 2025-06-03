package com.vagsoft.bookstore.validations.annotations;

import java.lang.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

/** Annotation to check if the user has ADMIN role */
@PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsAdmin {
}
