package com.vagsoft.bookstore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vagsoft.bookstore.models.Genre;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    private List<GenreDTO> genres = new ArrayList<>();
}
