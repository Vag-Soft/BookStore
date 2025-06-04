package com.vagsoft.bookstore.errors.exceptions;

/**
 * Exception thrown when a book cannot be updated
 */
public class BookUpdateException extends ResourceUpdateException {
    public BookUpdateException(String message) {
        super(message);
    }
}
