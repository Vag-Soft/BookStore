package com.vagsoft.bookstore.errors.exceptions.favouriteExceptions;

import com.vagsoft.bookstore.errors.exceptions.ResourceNotFoundException;

/** Exception thrown when a favourite cannot be found */
public class FavouriteNotFoundException extends ResourceNotFoundException {
    public FavouriteNotFoundException(String message) {
        super(message);
    }
}
