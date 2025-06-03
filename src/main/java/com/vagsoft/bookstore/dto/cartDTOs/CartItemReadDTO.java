package com.vagsoft.bookstore.dto.cartDTOs;

import com.vagsoft.bookstore.dto.bookDTOs.BookReadDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemReadDTO {
    private Integer cartID;
    private BookReadDTO book;
    private Integer quantity;
}
