package com.vagsoft.bookstore.mappers;

import com.vagsoft.bookstore.dto.OrderItemReadDTO;
import com.vagsoft.bookstore.models.entities.CartItem;
import com.vagsoft.bookstore.models.entities.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "orderItem.order.id", target = "orderID")
    OrderItemReadDTO orderItemToReadDto(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    OrderItem cartItemToOrderItem(CartItem cartItem);

    List<OrderItem> cartItemsToOrderItems(List<CartItem> cartItems);
}
