package com.vagsoft.bookstore.errors.exceptions;

/**
 * Exception thrown when an order cannot be created
 */
public class OrderCreationException extends ResourceCreationException {
    public OrderCreationException(String message) {
        super(message);
    }
}
