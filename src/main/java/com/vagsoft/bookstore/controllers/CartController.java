package com.vagsoft.bookstore.controllers;

import com.vagsoft.bookstore.dto.cartDTOs.CartReadDTO;
import com.vagsoft.bookstore.repositories.UserRepository;
import com.vagsoft.bookstore.services.CartService;
import com.vagsoft.bookstore.utils.AuthUtils;
import com.vagsoft.bookstore.validations.annotations.ExistsResource;
import com.vagsoft.bookstore.validations.annotations.IsAdmin;
import com.vagsoft.bookstore.validations.groups.BasicValidation;
import com.vagsoft.bookstore.validations.groups.ExtendedValidation;
import com.vagsoft.bookstore.validations.groups.OrderedValidation;
import jakarta.validation.constraints.Positive;
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
@Validated(OrderedValidation.class)
public class CartController {
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
            @PathVariable @Positive(groups = BasicValidation.class) @ExistsResource(repository = UserRepository.class, message = "User with given ID does not exist", groups = ExtendedValidation.class) Integer userID) {
        CartReadDTO cart = cartService.getCartByUserId(userID);
        return ResponseEntity.ok(cart);
    }

    /**
     * Retrieves the cart of the currently authenticated user
     *
     * @return the cart associated with the authenticated user
     */
    @GetMapping("/me")
    public ResponseEntity<CartReadDTO> getCartByUserId() {
        Integer userID = authUtils.getUserIdFromAuthentication();

        CartReadDTO cart = cartService.getCartByUserId(userID);
        return ResponseEntity.ok(cart);
    }
}
