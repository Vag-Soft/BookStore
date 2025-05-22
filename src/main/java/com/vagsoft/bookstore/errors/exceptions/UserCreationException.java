package com.vagsoft.bookstore.errors.exceptions;

/** Exception thrown when a user cannot be created */
public class UserCreationException extends ResourceCreationException {
    public UserCreationException(String message) {
        super(message);
    }
}
