package com.vagsoft.bookstore.dto;

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
