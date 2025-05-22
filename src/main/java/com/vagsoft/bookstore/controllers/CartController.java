package com.vagsoft.bookstore.controllers;


import com.vagsoft.bookstore.annotations.IsAdmin;
import com.vagsoft.bookstore.dto.CartReadDTO;
import com.vagsoft.bookstore.services.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for endpoints related to carts
 */
@RestController
@RequestMapping(path = "/carts")
@Validated
public class CartController {
    private static final Logger log = LoggerFactory.getLogger(CartController.class);
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @IsAdmin
    @GetMapping
    public ResponseEntity<Page<CartReadDTO>> getAllCarts(Pageable pageable) {
        log.info("GET /carts");

        return ResponseEntity.ok(cartService.getAllCarts(pageable));
    }
}
