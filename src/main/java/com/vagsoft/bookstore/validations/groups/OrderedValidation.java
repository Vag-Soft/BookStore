package com.vagsoft.bookstore.validations.groups;

import jakarta.validation.GroupSequence;

@GroupSequence({BasicValidation.class, ExtendedValidation.class})
public interface OrderedValidation {
}
