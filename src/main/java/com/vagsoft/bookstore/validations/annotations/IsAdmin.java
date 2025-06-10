package com.vagsoft.bookstore.validations.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.access.prepost.PreAuthorize;

/** Annotation to check if the user has ADMIN role. */
@PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsAdmin {
}
