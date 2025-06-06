package com.vagsoft.bookstore.errors.exceptions.orderExceptions;

import com.vagsoft.bookstore.errors.exceptions.ResourceUpdateException;

/**
 * Exception thrown when an order cannot be updated
 */
public class OrderUpdateException extends ResourceUpdateException {
    public OrderUpdateException(String message) {
        super(message);
    }
}
