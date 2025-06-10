package com.vagsoft.bookstore.errors.exceptions.favouriteExceptions;

import com.vagsoft.bookstore.errors.exceptions.ResourceCreationException;

/** Exception thrown when a favourite cannot be created. */
public class FavouriteCreationException extends ResourceCreationException {
    public FavouriteCreationException(final String message) {
        super(message);
    }
}
