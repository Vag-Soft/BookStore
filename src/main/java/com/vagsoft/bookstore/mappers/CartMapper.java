package com.vagsoft.bookstore.mappers;

import java.util.List;

import com.vagsoft.bookstore.dto.cartDTOs.CartReadDTO;
import com.vagsoft.bookstore.models.entities.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/** Mapper class for converting Cart entities and DTOs. */
@Mapper(componentModel = "spring", uses = CartItemMapper.class)
public interface CartMapper {

    /**
     * Converts a Cart entity to a CartReadDTO.
     *
     * @param cart
     *            the Cart entity to be converted
     * @return the converted CartReadDTO
     */
    @Mapping(source = "cart.user.id", target = "userID")
    CartReadDTO cartToReadDto(Cart cart);

    /**
     * Converts a list of Cart entities to a list of CartReadDTOs.
     *
     * @param carts
     *            the list of Cart entities to be converted
     * @return the list of converted CartReadDTOs
     */
    List<CartReadDTO> listCartToDto(List<Cart> carts);

    /**
     * Converts a page of Cart entities to a page of CartReadDTOs.
     *
     * @param page
     *            the page of Cart entities to be converted
     * @return the page of converted CartReadDTOs
     */
    default Page<CartReadDTO> pageCartToPageDto(Page<Cart> page) {
        return new PageImpl<>(listCartToDto(page.getContent()), page.getPageable(), page.getTotalElements());
    }
}
