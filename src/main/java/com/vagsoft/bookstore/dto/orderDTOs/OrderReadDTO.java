package com.vagsoft.bookstore.dto.orderDTOs;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.vagsoft.bookstore.models.enums.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderReadDTO {
    private Integer id;
    private Integer userID;
    private Double totalAmount;
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDate orderDate;
    @Builder.Default
    private List<OrderItemReadDTO> orderItems = new ArrayList<>();
}
