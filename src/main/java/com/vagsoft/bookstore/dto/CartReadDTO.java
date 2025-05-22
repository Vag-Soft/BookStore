package com.vagsoft.bookstore.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartReadDTO {
    private Integer id;
    private Integer userID;
    private List<CartItemReadDTO> cartItems;
}
