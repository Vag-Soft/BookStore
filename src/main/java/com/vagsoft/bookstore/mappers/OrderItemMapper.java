package com.vagsoft.bookstore.mappers;

import com.vagsoft.bookstore.dto.BookReadDTO;
import com.vagsoft.bookstore.dto.OrderItemReadDTO;
import com.vagsoft.bookstore.models.entities.Book;
import com.vagsoft.bookstore.models.entities.CartItem;
import com.vagsoft.bookstore.models.entities.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "orderItem.order.id", target = "orderID")
    OrderItemReadDTO orderItemToReadDto(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    OrderItem cartItemToOrderItem(CartItem cartItem);

    List<OrderItem> cartItemsToOrderItems(List<CartItem> cartItems);


    /**
     * Converts a list of OrderItem entities to a list of OrderItemReadDTOs
     *
     * @param orderItems
     *            the list of OrderItem entities to be converted
     * @return the list of converted OrderItemReadDTOs
     */
    List<OrderItemReadDTO> listOrderItemToListDto(List<OrderItem> orderItems);

    /**
     * Converts a page of OrderItem entities to a page of OrderItemReadDTOs
     *
     * @param page
     *            the page of OrderItem entities to be converted
     * @return the page of converted OrderItemReadDTOs
     */
    default Page<OrderItemReadDTO> pageOrderItemToPageDto(Page<OrderItem> page) {
        return new PageImpl<>(listOrderItemToListDto(page.getContent()), page.getPageable(), page.getTotalElements());
    }
}
