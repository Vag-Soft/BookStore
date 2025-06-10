package com.vagsoft.bookstore.errors.exceptions.userExceptions;

import com.vagsoft.bookstore.errors.exceptions.ResourceNotFoundException;

/** Exception thrown when a user is not found. */
public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(final String message) {
        super(message);
    }
}
