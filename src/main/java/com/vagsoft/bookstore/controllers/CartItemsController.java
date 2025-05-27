package com.vagsoft.bookstore.controllers;

import com.vagsoft.bookstore.annotations.ExistsCompositeResource;
import com.vagsoft.bookstore.annotations.ExistsResource;
import com.vagsoft.bookstore.annotations.IsAdmin;
import com.vagsoft.bookstore.annotations.UniqueCompositeFields;
import com.vagsoft.bookstore.dto.CartItemReadDTO;
import com.vagsoft.bookstore.dto.CartItemUpdateDTO;
import com.vagsoft.bookstore.dto.CartItemWriteDTO;
import com.vagsoft.bookstore.errors.exceptions.CartItemCreationException;
import com.vagsoft.bookstore.errors.exceptions.CartItemsNotFoundException;
import com.vagsoft.bookstore.repositories.BookRepository;
import com.vagsoft.bookstore.repositories.CartItemsRepository;
import com.vagsoft.bookstore.repositories.UserRepository;
import com.vagsoft.bookstore.services.CartItemsService;
import com.vagsoft.bookstore.utils.AuthUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/** REST controller for endpoints related to cart items */
@RestController
@RequestMapping(path = "/carts")
@Validated
public class CartItemsController {
    private static final Logger log = LoggerFactory.getLogger(CartItemsController.class);
    private final CartItemsService cartItemsService;
    private final AuthUtils authUtils;

    public CartItemsController(CartItemsService cartItemsService, AuthUtils authUtils) {
        this.cartItemsService = cartItemsService;
        this.authUtils = authUtils;
    }

    /**
     * Retrieves all cart items for a given user
     *
     * @param userID the ID of the user
     * @param pageable pagination information
     * @return paginated list of cart items for the user
     */
    @IsAdmin
    @GetMapping(path = "/{userID}/items")
    public ResponseEntity<Page<CartItemReadDTO>> getAllCartItems(
            @PathVariable @Positive @ExistsResource(repository = UserRepository.class, message = "User with given ID does not exist") Integer userID,
            Pageable pageable) {
        log.info("GET /carts/{}/items", userID);

        return ResponseEntity.ok(cartItemsService.getAllCartItems(userID, pageable));
    }

    /**
     * Retrieves a specific cart item for a given user using the book's ID
     *
     * @param userID the ID of the user
     * @param bookID the ID of the book
     * @return the cart item associated with the user and book's ID
     */
    @IsAdmin
    @GetMapping(path = "/{userID}/items/{bookID}")
    public ResponseEntity<CartItemReadDTO> getCartItem(
            @PathVariable @Positive @ExistsResource(repository = UserRepository.class, message = "User with given ID does not exist") Integer userID,
            @PathVariable @Positive @ExistsResource(repository = BookRepository.class, message = "Book with given ID does not exist") Integer bookID) {
        log.info("GET /carts/{}/items/{}", userID, bookID);

        Optional<CartItemReadDTO> cartItem = cartItemsService.getCartItem(userID, bookID);

        return ResponseEntity.ok(cartItem.orElseThrow(() -> new CartItemsNotFoundException("Cart item not found for user ID: " + userID + " and book ID: " + bookID)));
    }

    /**
     * Updates a specific cart item for a given user using the book's ID
     *
     * @param userID the ID of the user
     * @param bookID the ID of the book
     * @param cartItemUpdateDTO the new cart item information
     * @return the updated cart item
     */
    @IsAdmin
    @PutMapping(path = "/{userID}/items/{bookID}")
    public ResponseEntity<CartItemReadDTO> updateCartItem(
            @PathVariable @Positive @ExistsResource(repository = UserRepository.class, message = "User with given ID does not exist") Integer userID,
            @PathVariable @Positive @ExistsResource(repository = BookRepository.class, message = "Book with given ID does not exist") Integer bookID,
            @RequestBody @Valid @ExistsCompositeResource(repository = CartItemsRepository.class, methodName = "existsByUserIDAndBookID", firstPathVariable = "userID", secondPathVariable = "bookID", message = "No Cart item found with the given user ID and book ID") CartItemUpdateDTO cartItemUpdateDTO) {
        log.info("GET /carts/{}/items/{}: cartItemUpdateDTO={}", userID, bookID, cartItemUpdateDTO);

        Optional<CartItemReadDTO> updatedCartItem = cartItemsService.updateCartItem(userID, bookID, cartItemUpdateDTO);

        return ResponseEntity.ok(updatedCartItem.orElseThrow(() -> new CartItemsNotFoundException("Cart item not found for user ID: " + userID + " and book ID: " + bookID)));
    }

    /**
     * Deletes a specific cart item for a given user using the book's ID
     *
     * @param userID the ID of the user
     * @param bookID the ID of the book
     * @return no content response if deletion is successful
     */
    @IsAdmin
    @DeleteMapping(path = "/{userID}/items/{bookID}")
    public ResponseEntity<Void> deleteCartItem(
            @PathVariable @Positive @ExistsResource(repository = UserRepository.class, message = "User with given ID does not exist") Integer userID,
            @PathVariable @Positive @ExistsResource(repository = BookRepository.class, message = "Book with given ID does not exist") Integer bookID) {
        log.info("DELETE /carts/{}/items/{}", userID, bookID);

        Integer rowsDeleted = cartItemsService.deleteCartItem(userID, bookID);

        if (rowsDeleted == 0) {
            throw new CartItemsNotFoundException("No cart item found for user ID: " + userID + " and book ID: " + bookID); //TODO: use validation instead
        }

        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all cart items for the authenticated user
     *
     * @param pageable pagination information
     * @return paginated list of cart items for the authenticated user
     */
    @GetMapping(path = "/me/items")
    public ResponseEntity<Page<CartItemReadDTO>> getAllCartItems(Pageable pageable) {
        log.info("GET /carts/me/items");

        Integer userID = authUtils.getUserIdFromAuthentication();

        return ResponseEntity.ok(cartItemsService.getAllCartItems(userID, pageable));
    }


    /**
     * Adds a new cart item for the authenticated user
     *
     * @param cartItemWriteDTO the cart item to be added
     * @return the created cart item
     */
    @PostMapping(path = "/me/items")
    public ResponseEntity<CartItemReadDTO> addCartItem(
            @RequestBody @Valid @UniqueCompositeFields(repository = CartItemsRepository.class, methodName = "existsByUserIDAndBookID", usePathVariable = false, dtoClass = CartItemWriteDTO.class, dtoFieldName = "bookID", message = "This book is already in your cart") CartItemWriteDTO cartItemWriteDTO) {
        log.info("POST /carts/me/items");

        Integer userID = authUtils.getUserIdFromAuthentication();

        Optional<CartItemReadDTO> savedCartItem = cartItemsService.addCartItem(userID, cartItemWriteDTO);

        return ResponseEntity.ok(savedCartItem.orElseThrow(() -> new CartItemCreationException("Failed to create cart item")));
    }


    /**
     * Retrieves a specific cart item for the authenticated user using the book's ID
     *
     * @param bookID the ID of the book
     * @return the cart item associated with the user and book's ID
     */
    @GetMapping(path = "/me/items/{bookID}")
    public ResponseEntity<CartItemReadDTO> getCartItem(
            @PathVariable @Positive @ExistsResource(repository = BookRepository.class, message = "Book with given ID does not exist") Integer bookID) {
        log.info("GET /carts/me/items/{}", bookID);

        Integer userID = authUtils.getUserIdFromAuthentication();

        Optional<CartItemReadDTO> cartItem = cartItemsService.getCartItem(userID, bookID);

        return ResponseEntity.ok(cartItem.orElseThrow(() -> new CartItemsNotFoundException("Cart item not found with the given JWT and book ID: " + bookID)));
    }

    /**
     * Updates a specific cart item for the authenticated user using the book's ID
     *
     * @param bookID the ID of the book
     * @param cartItemUpdateDTO the new cart item information
     * @return the updated cart item
     */
    @PutMapping(path = "/me/items/{bookID}")
    public ResponseEntity<CartItemReadDTO> updateCartItem(
            @PathVariable @Positive @ExistsResource(repository = BookRepository.class, message = "Book with given ID does not exist") Integer bookID,
            @RequestBody @Valid @ExistsCompositeResource(repository = CartItemsRepository.class, methodName = "existsByUserIDAndBookID", useJWT = true, secondPathVariable = "bookID", message = "No Cart item found with the given JWT and book ID") CartItemUpdateDTO cartItemUpdateDTO) {
        log.info("GET /carts/me/items/{}: cartItemUpdateDTO={}", bookID, cartItemUpdateDTO);

        Integer userID = authUtils.getUserIdFromAuthentication();

        Optional<CartItemReadDTO> updatedCartItem = cartItemsService.updateCartItem(userID, bookID, cartItemUpdateDTO);

        return ResponseEntity.ok(updatedCartItem.orElseThrow(() -> new CartItemsNotFoundException("Cart item not found with the given JWT and book ID: " + bookID)));
    }

    /**
     * Deletes a specific cart item for the authenticated user using the book's ID
     *
     * @param bookID the ID of the book
     * @return no content response if deletion is successful
     */
    @DeleteMapping(path = "/me/items/{bookID}")
    public ResponseEntity<Void> deleteCartItem(
            @PathVariable @Positive @ExistsResource(repository = BookRepository.class, message = "Book with given ID does not exist") Integer bookID) {
        log.info("DELETE /carts/me/items/{}", bookID);

        Integer userID = authUtils.getUserIdFromAuthentication();

        Integer rowsDeleted = cartItemsService.deleteCartItem(userID, bookID);

        if (rowsDeleted == 0) {
            throw new CartItemsNotFoundException("Cart item not found with the given JWT and book ID: " + bookID); //TODO: use validation instead
        }

        return ResponseEntity.noContent().build();
    }
}
