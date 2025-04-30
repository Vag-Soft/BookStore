package com.vagsoft.bookstore.dto;


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
    Integer id;

    @NotBlank(message = "genre must not be blank")
    @Size(max = 32, message = "genre must be less than 32 characters")
    private String genre;

    public GenreDTO(String genre) {
        this.genre = genre;
    }
}
