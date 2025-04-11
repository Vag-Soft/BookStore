package com.vagsoft.bookstore.models;

import com.vagsoft.bookstore.models.enums.Role;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String hashPassword;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private Date signupDate;

    @OneToOne
    @JoinColumn(name = "userID")
    private Cart cart;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "userID")
    private List<Order> orders;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "userID")
    private List<Favourite> favourites;

    public User() {
        orders = new ArrayList<>();
        favourites = new ArrayList<>();
    }

    public User(String email, String username, String hashPassword, Role role, String firstName, String lastName, Date signupDate) {
        this.email = email;
        this.username = username;
        this.hashPassword = hashPassword;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.signupDate = signupDate;
        orders = new ArrayList<>();
        favourites = new ArrayList<>();
    }

    public User(List<Favourite> favourites, List<Order> orders, Cart cart, Date signupDate, String lastName, String firstName, Role role, String hashPassword, String username, String email) {
        this.cart = cart;
        this.signupDate = signupDate;
        this.lastName = lastName;
        this.firstName = firstName;
        this.role = role;
        this.hashPassword = hashPassword;
        this.username = username;
        this.email = email;
        this.favourites = favourites;
        this.orders = orders;
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public void addFavourite(Favourite favourite) {
        favourites.add(favourite);
    }


}
