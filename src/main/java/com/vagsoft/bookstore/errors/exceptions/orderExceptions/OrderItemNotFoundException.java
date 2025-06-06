package com.vagsoft.bookstore.errors.exceptions.orderExceptions;

import com.vagsoft.bookstore.errors.exceptions.ResourceNotFoundException;

/** Exception thrown when an order item is not found */
public class OrderItemNotFoundException extends ResourceNotFoundException {
    public OrderItemNotFoundException(String message) {
        super(message);
    }
}
