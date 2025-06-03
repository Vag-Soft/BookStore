package com.vagsoft.bookstore.errors.exceptions;

/** Exception thrown when a cart is not found */
public class CartNotFoundException extends ResourceNotFoundException {
    public CartNotFoundException(String message) {
        super(message);
    }
}
