package com.vagsoft.bookstore.mappers;

import java.util.List;

import com.vagsoft.bookstore.dto.cartDTOs.CartItemReadDTO;
import com.vagsoft.bookstore.models.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    /**
     * Converts a CartItem entity to a CartItemReadDTO
     *
     * @param cartItem
     *            the CartItem entity to be converted
     * @return the converted CartItemReadDTO
     */
    @Mapping(source = "cartItem.cart.id", target = "cartID")
    CartItemReadDTO cartItemToReadDto(CartItem cartItem);

    /**
     * Converts a list of CartItem entities to a list of CartItemReadDTOs
     *
     * @param cartItems
     *            the list of CartItem entities to be converted
     * @return the list of converted CartItemReadDTOs
     */
    List<CartItemReadDTO> listCartItemsToDto(List<CartItem> cartItems);

    /**
     * Converts a page of CartItem entities to a page of CartItemReadDTOs
     *
     * @param page
     *            the page of CartItem entities to be converted
     * @return the page of converted CartItemReadDTOs
     */
    default Page<CartItemReadDTO> pageCartItemsToPageDto(Page<CartItem> page) {
        return new PageImpl<>(listCartItemsToDto(page.getContent()), page.getPageable(), page.getTotalElements());
    }
}
