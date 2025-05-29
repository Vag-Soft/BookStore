package com.vagsoft.bookstore.validations.validators;

import java.util.List;

import com.vagsoft.bookstore.validations.annotations.UniqueGenresPerBook;
import com.vagsoft.bookstore.dto.genreDTOs.GenreDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/** Validator class for {@link UniqueGenresPerBook} */
public class UniqueGenresPerBookValidator implements ConstraintValidator<UniqueGenresPerBook, List<GenreDTO>> {

    @Override
    public void initialize(UniqueGenresPerBook contactNumber) {
    }

    /**
     * Checks if the given genres are valid according to the constraint. Genres are
     * valid if they are unique.
     *
     * @param genres
     *            the genres to be validated
     * @param cxt
     *            the validation context
     * @return true if the genres are valid, false otherwise
     */
    @Override
    public boolean isValid(List<GenreDTO> genres, ConstraintValidatorContext cxt) {
        return genres.stream().map(GenreDTO::getGenre).distinct().count() == genres.size();
    }
}
