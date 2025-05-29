package com.vagsoft.bookstore.dto.orderDTOs;

import com.vagsoft.bookstore.dto.bookDTOs.BookReadDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemReadDTO {
    private Integer id;
    private Integer orderID;
    private BookReadDTO book;
    private Integer quantity;
}
