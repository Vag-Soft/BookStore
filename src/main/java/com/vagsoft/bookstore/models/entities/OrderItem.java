package com.vagsoft.bookstore.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orderitems")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(nullable = false)
    private Integer orderID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookID", nullable = false)
    private Book book;

    @Column
    private Integer quantity;
}
