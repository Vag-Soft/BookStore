package com.vagsoft.bookstore.validators;

import com.vagsoft.bookstore.annotations.NullOrNotBlank;
import com.vagsoft.bookstore.annotations.UniqueGenresPerBook;
import com.vagsoft.bookstore.dto.BookWriteDTO;
import com.vagsoft.bookstore.dto.GenreDTO;
import com.vagsoft.bookstore.models.Book;
import com.vagsoft.bookstore.models.Genre;
import com.vagsoft.bookstore.repositories.BookRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Validator class for {@link UniqueGenresPerBook}
 */
public class UniqueGenresPerBookValidator implements ConstraintValidator<UniqueGenresPerBook, List<GenreDTO>> {

    @Override
    public void initialize(UniqueGenresPerBook contactNumber) {
    }

    /**
     * Checks if the given genres are valid according to the constraint.
     * Genres are valid if they are unique.
     *
     * @param genres the genres to be validated
     * @param cxt the validation context
     * @return true if the genres are valid, false otherwise
     */
    @Override
    public boolean isValid(List<GenreDTO> genres,
                           ConstraintValidatorContext cxt) {
        return genres.stream().map(GenreDTO::getGenre).distinct().count() == genres.size();
    }

}
