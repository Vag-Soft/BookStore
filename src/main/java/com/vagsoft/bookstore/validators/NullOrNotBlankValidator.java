package com.vagsoft.bookstore.validators;

import com.vagsoft.bookstore.annotations.NullOrNotBlank;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator class for {@link NullOrNotBlank}
 */
public class NullOrNotBlankValidator implements ConstraintValidator<NullOrNotBlank, String> {


    @Override
    public void initialize(NullOrNotBlank contactNumber) {
    }

    /**
     * Checks if the given string is valid according to the constraint.
     * A string is valid if it is either null or not empty.
     *
     * @param contactField the string to be validated
     * @param cxt the validation context
     * @return true if the string is valid, false otherwise
     */
    @Override
    public boolean isValid(String contactField,
            ConstraintValidatorContext cxt) {
        return contactField == null || !contactField.isEmpty();
    }

}
