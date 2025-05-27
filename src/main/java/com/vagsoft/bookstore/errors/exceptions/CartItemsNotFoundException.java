package com.vagsoft.bookstore.errors.exceptions;

/**
 * Exception thrown when a cart item is not found
 */
public class CartItemsNotFoundException extends ResourceNotFoundException{
    public CartItemsNotFoundException(String message) {
        super(message);
    }
}
