package com.vagsoft.bookstore.errors.exceptions.userExceptions;

import com.vagsoft.bookstore.errors.exceptions.ResourceCreationException;

/** Exception thrown when a user cannot be created. */
public class UserCreationException extends ResourceCreationException {
    public UserCreationException(final String message) {
        super(message);
    }
}
