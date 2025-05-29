package com.vagsoft.bookstore.dto.orderDTOs;

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
public class OrderUpdateDTO {
    @Enumerated(EnumType.STRING)
    private Status status;
}
