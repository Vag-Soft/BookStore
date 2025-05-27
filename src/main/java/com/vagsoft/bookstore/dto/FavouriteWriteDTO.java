package com.vagsoft.bookstore.dto;

import com.vagsoft.bookstore.annotations.ExistsResource;
import com.vagsoft.bookstore.repositories.BookRepository;
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
public class FavouriteWriteDTO {
    @NotNull(message = "bookID must not be null")
    @Positive(message = "bookID must be greater than 0")
    @ExistsResource(repository = BookRepository.class, message = "Book with given ID does not exist")
    private Integer bookID;
}
