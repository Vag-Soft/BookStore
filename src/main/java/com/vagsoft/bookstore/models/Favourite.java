package com.vagsoft.bookstore.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Favourites")
public class Favourite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(nullable = false)
    private int bookID;

    public Favourite() {
    }

    public Favourite(int bookID) {
        this.bookID = bookID;
    }
}
