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
    private Integer id;

    @Column(nullable = false)
    private Integer bookID;

    public Favourite() {
    }

    public Favourite(Integer bookID) {
        this.bookID = bookID;
    }
}
