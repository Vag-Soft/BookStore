package com.vagsoft.bookstore.errors.exceptions;

/**
 * Exception thrown when an order cannot be updated
 */
public class OrderUpdateException extends ResourceUpdateException {
    public OrderUpdateException(String message) {
        super(message);
    }
}
