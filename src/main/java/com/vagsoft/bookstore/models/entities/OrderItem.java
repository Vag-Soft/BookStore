package com.vagsoft.bookstore.models.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "orderitems")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(nullable = false)
    private Integer bookID;

    @Column
    private Integer quantity;

    public OrderItem() {
    }

    public OrderItem(Integer bookID, Integer quantity) {
        this.bookID = bookID;
        this.quantity = quantity;
    }
}
