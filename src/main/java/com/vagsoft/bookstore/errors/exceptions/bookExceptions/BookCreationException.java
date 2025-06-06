package com.vagsoft.bookstore.errors.exceptions.bookExceptions;

import com.vagsoft.bookstore.errors.exceptions.ResourceCreationException;

/** Exception thrown when a book cannot be created */
public class BookCreationException extends ResourceCreationException {
    public BookCreationException(String message) {
        super(message);
    }
}
