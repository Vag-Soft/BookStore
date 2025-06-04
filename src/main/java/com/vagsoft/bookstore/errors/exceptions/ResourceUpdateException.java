package com.vagsoft.bookstore.errors.exceptions;

/**
 * Exception thrown when a resource cannot be updated
 */
public class ResourceUpdateException extends RuntimeException {
    public ResourceUpdateException(String message) {
        super(message);
    }
}
