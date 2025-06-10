package com.vagsoft.bookstore.errors.exceptions.cartExceptions;

import com.vagsoft.bookstore.errors.exceptions.ResourceCreationException;

/** Exception thrown when a cart item cannot be created. */
public class CartItemCreationException extends ResourceCreationException {
    public CartItemCreationException(final String message) {
        super(message);
    }
}
