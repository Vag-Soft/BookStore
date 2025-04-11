package com.vagsoft.bookstore.models;

import com.vagsoft.bookstore.models.enums.Status;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(nullable = false)
    private double totalAmount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private Date orderDate;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "orderID")
    private List<OrderItem> orderItems;

    public Order() {
        orderItems = new ArrayList<>();
    }

    public Order(double totalAmount, Status status, Date orderDate) {
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderDate = orderDate;
        orderItems = new ArrayList<>();
    }

    public Order(List<OrderItem> orderItems, Date orderDate, Status status, double totalAmount) {
        this.orderItems = orderItems;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
    }
}
