package com.vagsoft.bookstore.dto.cartDTOs;

import java.util.ArrayList;
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
    @Builder.Default
    private List<CartItemReadDTO> cartItems = new ArrayList<>();
}
