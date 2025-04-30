package com.vagsoft.bookstore.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "CartItems")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(nullable = false)
    private Integer bookID;

    @Column
    private Integer quantity;

    public CartItem() {
    }

    public CartItem(Integer bookID, Integer quantity) {
        this.bookID = bookID;
        this.quantity = quantity;
    }
}
