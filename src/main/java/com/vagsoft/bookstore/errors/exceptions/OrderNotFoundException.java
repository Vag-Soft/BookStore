package com.vagsoft.bookstore.errors.exceptions;

/**
 * Exception thrown when an order cannot be found
 */
public class OrderNotFoundException extends ResourceNotFoundException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}
