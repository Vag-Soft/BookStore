package com.vagsoft.bookstore.dto;

import com.vagsoft.bookstore.models.entities.OrderItem;
import com.vagsoft.bookstore.models.enums.Status;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderReadDTO {
    private Integer id;
    private Double totalAmount;
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDate orderDate;
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();
}
