package com.vagsoft.bookstore.errors.exceptions.cartExceptions;

import com.vagsoft.bookstore.errors.exceptions.ResourceNotFoundException;

/**
 * Exception thrown when a cart item is not found
 */
public class CartItemsNotFoundException extends ResourceNotFoundException {
    public CartItemsNotFoundException(String message) {
        super(message);
    }
}
