package com.vagsoft.bookstore.errors.exceptions.userExceptions;

import com.vagsoft.bookstore.errors.exceptions.ResourceCreationException;

/**
 * Exception thrown when a user cannot be updated
 */
public class UserUpdateException extends ResourceCreationException {
    public UserUpdateException(String message) {
        super(message);
    }
}
