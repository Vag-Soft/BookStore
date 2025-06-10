package com.vagsoft.bookstore.services;

import com.vagsoft.bookstore.dto.cartDTOs.CartReadDTO;
import com.vagsoft.bookstore.mappers.CartMapper;
import com.vagsoft.bookstore.models.entities.Cart;
import com.vagsoft.bookstore.models.entities.User;
import com.vagsoft.bookstore.repositories.CartRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service class for handling cart-related operations. */
@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;

    public CartService(final CartRepository cartRepository, final CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartMapper = cartMapper;
    }

    /**
     * Get all carts.
     *
     * @param pageable
     *            pagination information
     * @return paginated list of carts
     */
    @Transactional(readOnly = true)
    public Page<CartReadDTO> getAllCarts(final Pageable pageable) {
        return cartMapper.pageCartToPageDto(cartRepository.findAll(pageable));
    }

    /**
     * Retrieves cart by user ID.
     *
     * @param userID
     *            the ID of the user
     * @return the cart associated with the user ID
     */
    @Transactional(readOnly = true)
    public CartReadDTO getCartByUserId(final Integer userID) {
        return cartMapper.cartToReadDto(cartRepository.getReferenceByUser_Id(userID));
    }

    /**
     * Creates an empty cart for the specified user ID.
     *
     * @param user
     *            the ID of the user for whom to create the cart
     */
    @Transactional
    public void createEmptyCart(final User user) {
        Cart cart = Cart.builder().user(user).build();
        cartRepository.save(cart);
    }

}
