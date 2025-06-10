package com.vagsoft.bookstore.errors.exceptions.bookExceptions;

import com.vagsoft.bookstore.errors.exceptions.ResourceUpdateException;

/**
 * Exception thrown when a book cannot be updated.
 */
public class BookUpdateException extends ResourceUpdateException {
    public BookUpdateException(final String message) {
        super(message);
    }
}
