package com.vagsoft.bookstore.annotations;

import java.lang.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

/** Annotation to check if the user has USER role */
@PreAuthorize("hasAuthority('SCOPE_ROLE_USER')")
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsUser {
}
