package com.vagsoft.bookstore.errors.exceptions;

/** Exception thrown when a cart cannot be created */
public class CartCreationException extends ResourceCreationException {
    public CartCreationException(String message) {
        super(message);
    }
}
