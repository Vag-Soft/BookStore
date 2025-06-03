package com.vagsoft.bookstore.dto.cartDTOs;

import com.vagsoft.bookstore.validations.groups.BasicValidation;
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
    @NotNull(message = "quantity must not be null", groups = BasicValidation.class)
    @Positive(message = "quantity must be greater than 0", groups = BasicValidation.class)
    private Integer quantity;
}
