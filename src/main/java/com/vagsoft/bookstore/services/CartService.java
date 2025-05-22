package com.vagsoft.bookstore.services;

import com.vagsoft.bookstore.dto.CartReadDTO;
import com.vagsoft.bookstore.mappers.CartMapper;
import com.vagsoft.bookstore.repositories.CartRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;

    public CartService(CartRepository cartRepository, CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartMapper = cartMapper;
    }

    @Transactional(readOnly = true)
    public Page<CartReadDTO> getAllCarts(Pageable pageable) {
        return cartMapper.pageCartToPageDto(cartRepository.findAll(pageable));
    }
}
