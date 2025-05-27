package com.vagsoft.bookstore.dto;

import com.vagsoft.bookstore.annotations.ExistsResource;
import com.vagsoft.bookstore.repositories.BookRepository;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemUpdateDTO {
    @NotNull(message = "quantity must not be null")
    @Positive(message = "quantity must be greater than 0")
    private Integer quantity;
}
