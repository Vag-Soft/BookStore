package com.vagsoft.bookstore.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
@Entity
@Table(name = "Carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "cartID")
    private List<CartItem> cartItems;

    public Cart() {
        cartItems = new ArrayList<>();
    }

    public Cart(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }
}
