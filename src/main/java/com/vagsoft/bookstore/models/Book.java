package com.vagsoft.bookstore.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "Books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column
    private String description;

    @Column(nullable = false)
    private int pages;

    @Column
    private double price;

    @Column
    private int availability;

    @Column(name = "ISBN", unique = true)
    private String isbn;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "bookID")
    private List<Genre> genres;

    public Book() {
        genres = new ArrayList<>();
    }

    public Book(String title, String author, int pages) {
        this.title = title;
        this.author = author;
        this.pages = pages;
        genres = new ArrayList<>();
    }

    public Book(String title, String author, String description, int pages, double price, int availability, String isbn) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.pages = pages;
        this.price = price;
        this.availability = availability;
        this.isbn = isbn;
        genres = new ArrayList<>();
    }

    public Book(String title, String author, String description, int pages, double price, int availability, String isbn, List<Genre> genres) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.pages = pages;
        this.price = price;
        this.availability = availability;
        this.isbn = isbn;
        this.genres = genres;
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }
}
