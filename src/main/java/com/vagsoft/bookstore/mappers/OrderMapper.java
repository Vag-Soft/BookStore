package com.vagsoft.bookstore.mappers;

import com.vagsoft.bookstore.dto.BookReadDTO;
import com.vagsoft.bookstore.dto.OrderReadDTO;
import com.vagsoft.bookstore.dto.OrderUpdateDTO;
import com.vagsoft.bookstore.models.entities.Book;
import com.vagsoft.bookstore.models.entities.Order;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

/** Mapper class for converting Order entities and DTOs */
@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {
    /**
     * Converts an Order entity to an OrderReadDTO
     * @param order the Order entity to be converted
     * @return the converted OrderReadDTO
     */
    OrderReadDTO orderToReadDto(Order order);

    /**
     * Updates an Order entity from an OrderUpdateDTO, ignoring null values
     *
     * @param orderUpdateDTO the OrderUpdateDTO containing the updated values
     * @param order the Order entity to be updated
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateOrderFromDto(OrderUpdateDTO orderUpdateDTO, @MappingTarget Order order);

    /**
     * Converts a list of Order entities to a list of OrderReadDTOs
     * @param orders the list of Order entities to be converted
     * @return the list of converted OrderReadDTOs
     */
    List<OrderReadDTO> ListOrderToListDto(List<Order> orders);

    /**
     * Converts a Page of Order entities to a Page of OrderReadDTOs
     * @param page the Page of Order entities to be converted
     * @return the converted Page of OrderReadDTOs
     */
    default Page<OrderReadDTO> PageOrderToPageDto(Page<Order> page) {
        return new PageImpl<>(ListOrderToListDto(page.getContent()), page.getPageable(), page.getTotalElements());
    }
}
