package com.vagsoft.bookstore.errors.exceptions;

/**
 * Exception thrown when a favourite cannot be created
 */
public class FavouriteCreationException extends ResourceCreationException {
    public FavouriteCreationException(String message) {
        super(message);
    }
}
