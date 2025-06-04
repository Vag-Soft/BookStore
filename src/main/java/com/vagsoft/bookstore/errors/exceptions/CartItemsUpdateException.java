package com.vagsoft.bookstore.errors.exceptions;

/**
 * Exception thrown when a cart item cannot be updated
 */
public class CartItemsUpdateException extends ResourceUpdateException{
    public CartItemsUpdateException(String message) {
        super(message);
    }
}
