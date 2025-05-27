package com.vagsoft.bookstore.dto;

import com.vagsoft.bookstore.annotations.ExistsResource;
import com.vagsoft.bookstore.models.entities.Book;
import com.vagsoft.bookstore.repositories.BookRepository;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
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
    @NotNull(message = "bookID must not be null")
    @Positive(message = "bookID must be greater than 0")
    @ExistsResource(repository = BookRepository.class, message = "Book with given ID does not exist")
    private Integer bookID;

    @Builder.Default
    @Positive(message = "quantity must be greater than 0")
    private Integer quantity = 1;
}
