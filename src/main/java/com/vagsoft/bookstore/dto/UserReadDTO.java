package com.vagsoft.bookstore.dto;

import com.vagsoft.bookstore.models.Cart;
import com.vagsoft.bookstore.models.Favourite;
import com.vagsoft.bookstore.models.Order;
import com.vagsoft.bookstore.models.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserReadDTO {
    private Integer id;
    private String email;
    private String username;
    @Enumerated(EnumType.STRING) //TODO: check validation
    private Role role;
    private String firstName;
    private String lastName;
    private LocalDate signupDate;
}
