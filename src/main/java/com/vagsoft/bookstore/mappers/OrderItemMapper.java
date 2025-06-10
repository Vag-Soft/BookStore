package com.vagsoft.bookstore.mappers;

import java.util.List;

import com.vagsoft.bookstore.dto.orderDTOs.OrderItemReadDTO;
import com.vagsoft.bookstore.models.entities.CartItem;
import com.vagsoft.bookstore.models.entities.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    /**
     * Converts an OrderItem entity to an OrderItemReadDTO.
     *
     * @param orderItem
     *            the OrderItem entity to be converted
     * @return the converted OrderItemReadDTO
     */
    @Mapping(source = "orderItem.order.id", target = "orderID")
    OrderItemReadDTO orderItemToReadDto(OrderItem orderItem);

    /**
     * Converts a CartItem to an OrderItem entity.
     *
     * @param cartItem
     *            the CartItem to be converted
     * @return the converted OrderItem entity
     */
    @Mapping(target = "id", ignore = true)
    OrderItem cartItemToOrderItem(CartItem cartItem);

    /**
     * Converts a list of CartItem entities to a list of OrderItem entities.
     *
     * @param cartItems
     *            the list of CartItem entities to be converted
     * @return the list of converted OrderItem entities
     */
    List<OrderItem> cartItemsToOrderItems(List<CartItem> cartItems);

    /**
     * Converts a list of OrderItem entities to a list of OrderItemReadDTOs.
     *
     * @param orderItems
     *            the list of OrderItem entities to be converted
     * @return the list of converted OrderItemReadDTOs
     */
    List<OrderItemReadDTO> listOrderItemToListDto(List<OrderItem> orderItems);

    /**
     * Converts a page of OrderItem entities to a page of OrderItemReadDTOs.
     *
     * @param page
     *            the page of OrderItem entities to be converted
     * @return the page of converted OrderItemReadDTOs
     */
    default Page<OrderItemReadDTO> pageOrderItemToPageDto(Page<OrderItem> page) {
        return new PageImpl<>(listOrderItemToListDto(page.getContent()), page.getPageable(), page.getTotalElements());
    }
}
