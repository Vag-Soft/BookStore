package com.vagsoft.bookstore.errors.exceptions;

/**
 * Exception thrown when a book cannot be created
 */
public class BookCreationException extends RuntimeException{
    public BookCreationException(String message) {
        super(message);
    }
}
