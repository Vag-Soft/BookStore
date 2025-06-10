package com.vagsoft.bookstore.dto.genreDTOs;

import com.vagsoft.bookstore.validations.groups.BasicValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenreDTO {
    private Integer id;

    @NotBlank(message = "genre must not be blank", groups = BasicValidation.class)
    @Size(max = 31, message = "genre must be less than 32 characters", groups = BasicValidation.class)
    private String genre;

    public GenreDTO(final String genre) {
        this.genre = genre;
    }
}
