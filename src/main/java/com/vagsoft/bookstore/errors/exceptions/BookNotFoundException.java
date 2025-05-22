package com.vagsoft.bookstore.errors.exceptions;

/** Exception thrown when a book is not found */
public class BookNotFoundException extends ResourceNotFoundException {
    public BookNotFoundException(String message) {
        super(message);
    }
}
