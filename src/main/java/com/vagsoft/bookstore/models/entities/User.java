package com.vagsoft.bookstore.models.entities;

import com.vagsoft.bookstore.models.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(name = "hashpassword", nullable = false)
    private String hashPassword;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "firstname", nullable = false)
    private String firstName;

    @Column(name = "lastname", nullable = false)
    private String lastName;

    @Column(name = "signupdate", nullable = false)
    private LocalDate signupDate;

    public User(String email, String username, String hashPassword, Role role, String firstName, String lastName, LocalDate signupDate) {
        this.email = email;
        this.username = username;
        this.hashPassword = hashPassword;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.signupDate = signupDate;
    }

    //
//    @OneToOne
//    @JoinColumn(name = "userID")
//    private Cart cart;
//
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//    @JoinColumn(name = "userID")
//    private List<Order> orders = new ArrayList<>();
//
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//    @JoinColumn(name = "userID")
//    private List<Favourite> favourites = new ArrayList<>();
//
//    public void addOrder(Order order) {
//        orders.add(order);
//    }
//
//    public void addFavourite(Favourite favourite) {
//        favourites.add(favourite);
//    }


}
