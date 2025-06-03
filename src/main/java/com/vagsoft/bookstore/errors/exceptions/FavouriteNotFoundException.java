package com.vagsoft.bookstore.errors.exceptions;

/** Exception thrown when a favourite cannot be found */
public class FavouriteNotFoundException extends ResourceNotFoundException {
    public FavouriteNotFoundException(String message) {
        super(message);
    }
}
