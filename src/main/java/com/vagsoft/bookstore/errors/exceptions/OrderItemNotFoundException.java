package com.vagsoft.bookstore.errors.exceptions;

/** Exception thrown when an order item is not found */
public class OrderItemNotFoundException extends ResourceNotFoundException {
    public OrderItemNotFoundException(String message) {
        super(message);
    }
}
