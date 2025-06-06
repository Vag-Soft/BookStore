package com.vagsoft.bookstore.errors.exceptions.cartExceptions;

import com.vagsoft.bookstore.errors.exceptions.ResourceNotFoundException;

/** Exception thrown when a cart is not found */
public class CartNotFoundException extends ResourceNotFoundException {
    public CartNotFoundException(String message) {
        super(message);
    }
}
