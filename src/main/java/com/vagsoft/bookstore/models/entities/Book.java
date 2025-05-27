package com.vagsoft.bookstore.models.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column
    private String description;

    @Column(nullable = false)
    private Integer pages;

    @Column
    private Double price;

    @Column
    private Integer availability;

    @Column(name = "ISBN", unique = true)
    private String isbn;

    @Builder.Default
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Genre> genres = new ArrayList<>();

    public void addGenre(Genre genre) {
        genres.add(genre);
    }
}
