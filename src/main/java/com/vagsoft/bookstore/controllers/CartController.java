package com.vagsoft.bookstore.controllers;

import java.util.Optional;

import com.vagsoft.bookstore.annotations.ExistsResource;
import com.vagsoft.bookstore.annotations.IsAdmin;
import com.vagsoft.bookstore.dto.CartReadDTO;
import com.vagsoft.bookstore.errors.exceptions.CartNotFoundException;
import com.vagsoft.bookstore.errors.exceptions.ResourceNotFoundException;
import com.vagsoft.bookstore.repositories.UserRepository;
import com.vagsoft.bookstore.services.CartService;
import com.vagsoft.bookstore.utils.AuthUtils;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST controller for endpoints related to carts */
@RestController
@RequestMapping(path = "/carts")
@Validated
public class CartController {
    private static final Logger log = LoggerFactory.getLogger(CartController.class);
    private final CartService cartService;
    private final AuthUtils authUtils;

    public CartController(CartService cartService, AuthUtils authUtils) {
        this.cartService = cartService;
        this.authUtils = authUtils;
    }

    /**
     * Retrieves all carts
     *
     * @param pageable
     *            pagination information
     * @return paginated list of carts
     */
    @IsAdmin
    @GetMapping
    public ResponseEntity<Page<CartReadDTO>> getAllCarts(Pageable pageable) {
        log.info("GET /carts: pageable={}", pageable);

        return ResponseEntity.ok(cartService.getAllCarts(pageable));
    }

    /**
     * Retrieves cart by user ID
     *
     * @param userID
     *            the ID of the user
     * @return the cart associated with the user ID
     */
    @IsAdmin
    @GetMapping("/{userID}")
    public ResponseEntity<CartReadDTO> getCartByUserId(
            @PathVariable @Positive @ExistsResource(repository = UserRepository.class, message = "User with given ID does not exist") Integer userID) {
        log.info("GET /carts/{}", userID);

        Optional<CartReadDTO> cart = cartService.getCartByUserId(userID);
        return ResponseEntity.ok(cart.orElseThrow(() -> new CartNotFoundException("Cart not found for user ID: " + userID)));
    }

    /**
     * Retrieves the cart of the currently authenticated user
     *
     * @return the cart associated with the authenticated user
     */
    @IsAdmin
    @GetMapping("/me")
    public ResponseEntity<CartReadDTO> getCartByUserId() {
        log.info("GET /carts/me");

        Integer userID = authUtils.getUserIdFromAuthentication();

        Optional<CartReadDTO> cart = cartService.getCartByUserId(userID);
        return ResponseEntity.ok(cart.orElseThrow(() -> new CartNotFoundException("Cart not found for user ID: " + userID)));
    }
}
