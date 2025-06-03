package com.vagsoft.bookstore.dto.cartDTOs;

import com.vagsoft.bookstore.repositories.BookRepository;
import com.vagsoft.bookstore.validations.annotations.ExistsResource;
import com.vagsoft.bookstore.validations.groups.BasicValidation;
import com.vagsoft.bookstore.validations.groups.ExtendedValidation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemWriteDTO {
    @NotNull(message = "bookID must not be null", groups = BasicValidation.class)
    @Positive(message = "bookID must be greater than 0", groups = BasicValidation.class)
    @ExistsResource(repository = BookRepository.class, message = "Book with given ID does not exist", groups = ExtendedValidation.class)
    private Integer bookID;

    @Builder.Default
    @Positive(message = "quantity must be greater than 0", groups = BasicValidation.class)
    private Integer quantity = 1;
}
