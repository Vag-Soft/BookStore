package com.vagsoft.bookstore.errors.exceptions;

/**
 * Exception thrown when a resource cannot be created
 */
public class ResourceCreationException extends RuntimeException {
    public ResourceCreationException(String message) {
        super(message);
    }
}
