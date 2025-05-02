package com.vagsoft.bookstore.dto;

import com.vagsoft.bookstore.models.Cart;
import com.vagsoft.bookstore.models.Favourite;
import com.vagsoft.bookstore.models.Order;
import com.vagsoft.bookstore.models.enums.Role;
import jakarta.persistence.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class UserReadDTO {
    private Integer id;
    private String email;
    private String username;
    private String hashPassword;
    @Enumerated(EnumType.STRING) //TODO: check validation
    private Role role;
    private String firstName;
    private String lastName;
    private Date signupDate;
}
