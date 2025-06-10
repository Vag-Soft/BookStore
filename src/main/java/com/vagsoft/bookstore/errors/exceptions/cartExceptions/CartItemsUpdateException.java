package com.vagsoft.bookstore.errors.exceptions.cartExceptions;

import com.vagsoft.bookstore.errors.exceptions.ResourceUpdateException;

/**
 * Exception thrown when a cart item cannot be updated.
 */
public class CartItemsUpdateException extends ResourceUpdateException {
    public CartItemsUpdateException(final String message) {
        super(message);
    }
}
