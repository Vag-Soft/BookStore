package com.vagsoft.bookstore.dto.bookDTOs;

import java.util.ArrayList;
import java.util.List;

import com.vagsoft.bookstore.dto.genreDTOs.GenreDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookReadDTO {
    private Integer id;
    private String title;
    private String author;
    private String description;
    private Integer pages;
    private Double price;
    private Integer availability;
    private String isbn;
    @Builder.Default
    private List<GenreDTO> genres = new ArrayList<>();
}
