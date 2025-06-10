package com.vagsoft.bookstore.models.entities;

import java.time.LocalDate;

import com.vagsoft.bookstore.models.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public User(final String email, final String username, final String hashPassword, final Role role,
            final String firstName, final String lastName, final LocalDate signupDate) {
        this.email = email;
        this.username = username;
        this.hashPassword = hashPassword;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.signupDate = signupDate;
    }
}
