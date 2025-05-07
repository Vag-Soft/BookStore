package com.vagsoft.bookstore.validators;

import com.vagsoft.bookstore.annotations.UniqueGenresPerBook;
import com.vagsoft.bookstore.annotations.UniqueISBN;
import com.vagsoft.bookstore.dto.GenreDTO;
import com.vagsoft.bookstore.repositories.BookRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Validator class for {@link UniqueISBN}
 */
public class UniqueISBNValidator implements ConstraintValidator<UniqueISBN, String> {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public void initialize(UniqueISBN contactNumber) {
    }

    /**
     * Checks if the given ISBN is valid according to the constraint.
     * An ISBN is valid if it does not exists in the database.
     *
     * @param isbn the ISBN to be validated
     * @param cxt the validation context
     * @return true if the ISBN is valid, false otherwise
     */
    @Override
    public boolean isValid(String isbn,
                           ConstraintValidatorContext cxt) {

        return !bookRepository.existsByIsbn(isbn);
    }

}
