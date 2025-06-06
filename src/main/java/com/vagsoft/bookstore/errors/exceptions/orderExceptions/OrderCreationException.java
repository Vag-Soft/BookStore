package com.vagsoft.bookstore.errors.exceptions.orderExceptions;

import com.vagsoft.bookstore.errors.exceptions.ResourceCreationException;

/**
 * Exception thrown when an order cannot be created
 */
public class OrderCreationException extends ResourceCreationException {
    public OrderCreationException(String message) {
        super(message);
    }
}
