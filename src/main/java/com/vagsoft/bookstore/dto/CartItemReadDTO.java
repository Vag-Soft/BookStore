package com.vagsoft.bookstore.dto;

import com.vagsoft.bookstore.models.entities.Book;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemReadDTO {
    private Integer id;
    private Integer cartID;
    private BookReadDTO book;
    private Integer quantity;
}
