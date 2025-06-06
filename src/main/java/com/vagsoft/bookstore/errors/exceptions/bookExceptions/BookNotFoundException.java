package com.vagsoft.bookstore.errors.exceptions.bookExceptions;

import com.vagsoft.bookstore.errors.exceptions.ResourceNotFoundException;

/** Exception thrown when a book is not found */
public class BookNotFoundException extends ResourceNotFoundException {
    public BookNotFoundException(String message) {
        super(message);
    }
}
