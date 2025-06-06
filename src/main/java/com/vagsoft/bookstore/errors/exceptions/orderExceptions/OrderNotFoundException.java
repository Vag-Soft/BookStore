package com.vagsoft.bookstore.errors.exceptions.orderExceptions;

import com.vagsoft.bookstore.errors.exceptions.ResourceNotFoundException;

/**
 * Exception thrown when an order cannot be found
 */
public class OrderNotFoundException extends ResourceNotFoundException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}
