package com.vagsoft.bookstore.errors.exceptions;

/**
 * Exception thrown when a resource cannot be updated.
 */
public class ResourceUpdateException extends RuntimeException {
    public ResourceUpdateException(final String message) {
        super(message);
    }
}
