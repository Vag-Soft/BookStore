package com.vagsoft.bookstore.dto.bookDTOs;

import java.util.ArrayList;
import java.util.List;

import com.vagsoft.bookstore.dto.genreDTOs.GenreDTO;
import com.vagsoft.bookstore.repositories.BookRepository;
import com.vagsoft.bookstore.validations.annotations.NullOrNotBlank;
import com.vagsoft.bookstore.validations.annotations.UniqueField;
import com.vagsoft.bookstore.validations.annotations.UniqueGenresPerBook;
import com.vagsoft.bookstore.validations.groups.BasicValidation;
import com.vagsoft.bookstore.validations.groups.ExtendedValidation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookWriteDTO {
    @NotBlank(message = "title must not be blank", groups = BasicValidation.class)
    @Size(max = 61, message = "title must be less than 64 characters", groups = BasicValidation.class)
    private String title;

    @NotBlank(message = "author must not be blank", groups = BasicValidation.class)
    @Size(max = 31, message = "author must be less than 32 characters", groups = BasicValidation.class)
    private String author;

    private String description;

    @NotNull(message = "pages must not be null", groups = BasicValidation.class)
    @Positive(message = "pages must be greater than 0", groups = BasicValidation.class)
    private Integer pages;

    @PositiveOrZero(message = "price must be greater than or equal to 0", groups = BasicValidation.class)
    private Double price;

    @PositiveOrZero(message = "availability must be greater than or equal to 0", groups = BasicValidation.class)
    private Integer availability;

    @UniqueField(repository = BookRepository.class, methodName = "existsByIsbn", message = "isbn must be unique", groups = ExtendedValidation.class)
    @NullOrNotBlank(groups = BasicValidation.class)
    @Size(max = 31, message = "isbn must be less than 32 characters", groups = BasicValidation.class)
    private String isbn;

    @Builder.Default
    @Valid
    @UniqueGenresPerBook(groups = ExtendedValidation.class)
    private List<GenreDTO> genres = new ArrayList<>();
}
