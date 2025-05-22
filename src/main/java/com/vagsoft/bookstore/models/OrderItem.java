package com.vagsoft.bookstore.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "OrderItems")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(nullable = false)
    private int bookID;

    @Column
    private int quantity;

    public OrderItem() {
    }

    public OrderItem(int bookID, int quantity) {
        this.bookID = bookID;
        this.quantity = quantity;
    }
}
