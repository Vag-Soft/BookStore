package com.vagsoft.bookstore.errors.exceptions;

/**
 * Exception thrown when a book is not found
 */
public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String message) {
        super(message);
    }
}
