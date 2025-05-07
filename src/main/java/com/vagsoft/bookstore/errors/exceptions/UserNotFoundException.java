package com.vagsoft.bookstore.errors.exceptions;

/**
 * Exception thrown when a user is not found
 */
public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
