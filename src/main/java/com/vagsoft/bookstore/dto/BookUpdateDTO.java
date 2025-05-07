package com.vagsoft.bookstore.dto;

import com.vagsoft.bookstore.annotations.NullOrNotBlank;
import com.vagsoft.bookstore.annotations.UniqueGenresPerBook;
import com.vagsoft.bookstore.annotations.UniqueISBN;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookUpdateDTO {
    @NullOrNotBlank
    @Size(max = 63, message = "title must be less than 64 characters")
    private String title;

    @NullOrNotBlank
    @Size(max = 31, message = "author must be less than 32 characters")
    private String author;

    @NullOrNotBlank
    private String description;

    @Positive(message = "pages must be greater than 0")
    private Integer pages;

    @PositiveOrZero(message = "price must be greater than or equal to 0")
    private Double price;

    @PositiveOrZero(message = "availability must be greater than or equal to 0")
    private Integer availability;

    @NullOrNotBlank
    @UniqueISBN
    @Size(max = 31, message = "isbn must be less than 32 characters")
    private String isbn;

    @Valid
    @UniqueGenresPerBook
    private List<GenreDTO> genres = new ArrayList<>();
}
