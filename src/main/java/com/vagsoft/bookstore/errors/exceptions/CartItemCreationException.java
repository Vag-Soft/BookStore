package com.vagsoft.bookstore.errors.exceptions;

/** Exception thrown when a cart item cannot be created */
public class CartItemCreationException extends ResourceCreationException {
    public CartItemCreationException(String message) {
        super(message);
    }
}
