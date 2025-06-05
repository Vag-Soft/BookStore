package com.vagsoft.bookstore.controllers;

import java.util.Optional;

import com.vagsoft.bookstore.dto.cartDTOs.CartItemReadDTO;
import com.vagsoft.bookstore.dto.cartDTOs.CartItemUpdateDTO;
import com.vagsoft.bookstore.dto.cartDTOs.CartItemWriteDTO;
import com.vagsoft.bookstore.errors.exceptions.CartItemCreationException;
import com.vagsoft.bookstore.errors.exceptions.CartItemsUpdateException;
import com.vagsoft.bookstore.repositories.BookRepository;
import com.vagsoft.bookstore.repositories.CartItemsRepository;
import com.vagsoft.bookstore.repositories.UserRepository;
import com.vagsoft.bookstore.services.CartItemsService;
import com.vagsoft.bookstore.utils.AuthUtils;
import com.vagsoft.bookstore.validations.annotations.ExistsCompositeResource;
import com.vagsoft.bookstore.validations.annotations.ExistsResource;
import com.vagsoft.bookstore.validations.annotations.IsAdmin;
import com.vagsoft.bookstore.validations.annotations.UniqueCompositeFields;
import com.vagsoft.bookstore.validations.groups.BasicValidation;
import com.vagsoft.bookstore.validations.groups.ExtendedValidation;
import com.vagsoft.bookstore.validations.groups.OrderedValidation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
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

/** REST controller for endpoints related to cart items */
@RestController
@RequestMapping(path = "/carts")
@Validated(OrderedValidation.class)
public class CartItemsController {
    private final CartItemsService cartItemsService;
    private final AuthUtils authUtils;

    public CartItemsController(CartItemsService cartItemsService, AuthUtils authUtils) {
        this.cartItemsService = cartItemsService;
        this.authUtils = authUtils;
    }

    /**
     * Retrieves all cart items for a given user
     *
     * @param userID
     *            the ID of the user
     * @param pageable
     *            pagination information
     * @return paginated list of cart items for the user
     */
    @IsAdmin
    @GetMapping(path = "/{userID}/items")
    public ResponseEntity<Page<CartItemReadDTO>> getAllCartItems(
            @PathVariable @Positive(groups = BasicValidation.class) @ExistsResource(repository = UserRepository.class, message = "User with given ID does not exist", groups = ExtendedValidation.class) Integer userID,
            Pageable pageable) {
        return ResponseEntity.ok(cartItemsService.getAllCartItems(userID, pageable));
    }

    /**
     * Retrieves a specific cart item for a given user using the book's ID
     *
     * @param userID
     *            the ID of the user
     * @param bookID
     *            the ID of the book
     * @return the cart item associated with the user and book's ID
     */
    @IsAdmin
    @GetMapping(path = "/{userID}/items/{bookID}")
    public ResponseEntity<CartItemReadDTO> getCartItem(@PathVariable @Positive(groups = BasicValidation.class) //
    @ExistsResource(repository = UserRepository.class, message = "User with given ID does not exist", groups = ExtendedValidation.class) Integer userID, //
            @PathVariable @Positive(groups = BasicValidation.class) //
            @ExistsResource(repository = BookRepository.class, message = "Book with given ID does not exist", groups = ExtendedValidation.class) //
            @ExistsCompositeResource(repository = CartItemsRepository.class, methodName = "existsByUserIDAndBookID", firstPathVariable = "userID", secondPathVariable = "bookID", message = "The book with the given ID is not in the given user's cart items", groups = ExtendedValidation.class) Integer bookID) {
        CartItemReadDTO cartItem = cartItemsService.getCartItem(userID, bookID);

        return ResponseEntity.ok(cartItem);
    }

    /**
     * Updates a specific cart item for a given user using the book's ID
     *
     * @param userID
     *            the ID of the user
     * @param bookID
     *            the ID of the book
     * @param cartItemUpdateDTO
     *            the new cart item information
     * @return the updated cart item
     */
    @IsAdmin
    @PutMapping(path = "/{userID}/items/{bookID}")
    public ResponseEntity<CartItemReadDTO> updateCartItem(
            @PathVariable @Positive(groups = BasicValidation.class) @ExistsResource(repository = UserRepository.class, message = "User with given ID does not exist", groups = ExtendedValidation.class) Integer userID,
            @PathVariable @Positive(groups = BasicValidation.class) @ExistsResource(repository = BookRepository.class, message = "Book with given ID does not exist", groups = ExtendedValidation.class) Integer bookID,
            @RequestBody @Valid @ExistsCompositeResource(repository = CartItemsRepository.class, methodName = "existsByUserIDAndBookID", firstPathVariable = "userID", secondPathVariable = "bookID", message = "No Cart item found with the given user ID and book ID", groups = ExtendedValidation.class) CartItemUpdateDTO cartItemUpdateDTO) {
        Optional<CartItemReadDTO> updatedCartItem = cartItemsService.updateCartItem(userID, bookID, cartItemUpdateDTO);

        return ResponseEntity.ok(updatedCartItem.orElseThrow(() -> new CartItemsUpdateException(
                "Cart item with user ID: " + userID + " and book ID: " + bookID + " update failed")));
    }

    /**
     * Deletes a specific cart item for a given user using the book's ID
     *
     * @param userID
     *            the ID of the user
     * @param bookID
     *            the ID of the book
     * @return no content response if deletion is successful
     */
    @IsAdmin
    @DeleteMapping(path = "/{userID}/items/{bookID}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable @Positive(groups = BasicValidation.class) //
    @ExistsResource(repository = UserRepository.class, message = "User with given ID does not exist", groups = ExtendedValidation.class) //
    Integer userID, //
            @PathVariable @Positive(groups = BasicValidation.class) //
            @ExistsResource(repository = BookRepository.class, message = "Book with given ID does not exist", groups = ExtendedValidation.class) //
            @ExistsCompositeResource(repository = CartItemsRepository.class, methodName = "existsByUserIDAndBookID", firstPathVariable = "userID", secondPathVariable = "bookID", message = "The book with the given ID is not in the given user's cart items", groups = ExtendedValidation.class) //
            Integer bookID) {
        cartItemsService.deleteCartItem(userID, bookID);

        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all cart items for the authenticated user
     *
     * @param pageable
     *            pagination information
     * @return paginated list of cart items for the authenticated user
     */
    @GetMapping(path = "/me/items")
    public ResponseEntity<Page<CartItemReadDTO>> getAllCartItems(Pageable pageable) {
        Integer userID = authUtils.getUserIdFromAuthentication();

        return ResponseEntity.ok(cartItemsService.getAllCartItems(userID, pageable));
    }

    /**
     * Adds a new cart item for the authenticated user
     *
     * @param cartItemWriteDTO
     *            the cart item to be added
     * @return the created cart item
     */
    @PostMapping(path = "/me/items")
    public ResponseEntity<CartItemReadDTO> addCartItem(
            @RequestBody @Valid @UniqueCompositeFields(repository = CartItemsRepository.class, methodName = "existsByUserIDAndBookID", usePathVariable = false, dtoClass = CartItemWriteDTO.class, dtoFieldName = "bookID", message = "This book is already in your cart", groups = ExtendedValidation.class) CartItemWriteDTO cartItemWriteDTO) {
        Integer userID = authUtils.getUserIdFromAuthentication();

        Optional<CartItemReadDTO> savedCartItem = cartItemsService.addCartItem(userID, cartItemWriteDTO);

        return ResponseEntity
                .ok(savedCartItem.orElseThrow(() -> new CartItemCreationException("Failed to create cart item")));
    }

    /**
     * Retrieves a specific cart item for the authenticated user using the book's ID
     *
     * @param bookID
     *            the ID of the book
     * @return the cart item associated with the user and book's ID
     */
    @GetMapping(path = "/me/items/{bookID}")
    public ResponseEntity<CartItemReadDTO> getCartItem(@PathVariable @Positive(groups = BasicValidation.class) //
    @ExistsResource(repository = BookRepository.class, message = "Book with given ID does not exist", groups = ExtendedValidation.class) //
    @ExistsCompositeResource(repository = CartItemsRepository.class, methodName = "existsByUserIDAndBookID", useJWT = true, secondPathVariable = "bookID", message = "The book with the given ID is not in the given user's cart items", groups = ExtendedValidation.class) //
    Integer bookID) {
        Integer userID = authUtils.getUserIdFromAuthentication();

        CartItemReadDTO cartItem = cartItemsService.getCartItem(userID, bookID);

        return ResponseEntity.ok(cartItem);
    }

    /**
     * Updates a specific cart item for the authenticated user using the book's ID
     *
     * @param bookID
     *            the ID of the book
     * @param cartItemUpdateDTO
     *            the new cart item information
     * @return the updated cart item
     */
    @PutMapping(path = "/me/items/{bookID}")
    public ResponseEntity<CartItemReadDTO> updateCartItem(@PathVariable @Positive(groups = BasicValidation.class) //
    @ExistsResource(repository = BookRepository.class, message = "Book with given ID does not exist", groups = ExtendedValidation.class) //
    Integer bookID, //
            @RequestBody @Valid @ExistsCompositeResource(repository = CartItemsRepository.class, methodName = "existsByUserIDAndBookID", useJWT = true, secondPathVariable = "bookID", message = "No Cart item found with the given JWT and book ID", groups = ExtendedValidation.class) CartItemUpdateDTO cartItemUpdateDTO) {
        Integer userID = authUtils.getUserIdFromAuthentication();

        Optional<CartItemReadDTO> updatedCartItem = cartItemsService.updateCartItem(userID, bookID, cartItemUpdateDTO);

        return ResponseEntity.ok(updatedCartItem.orElseThrow(() -> new CartItemsUpdateException(
                "Cart item with the given JWT and book ID: " + bookID + " update failed")));
    }

    /**
     * Deletes a specific cart item for the authenticated user using the book's ID
     *
     * @param bookID
     *            the ID of the book
     * @return no content response if deletion is successful
     */
    @DeleteMapping(path = "/me/items/{bookID}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable @Positive(groups = BasicValidation.class) //
    @ExistsResource(repository = BookRepository.class, message = "Book with given ID does not exist", groups = ExtendedValidation.class) //
    @ExistsCompositeResource(repository = CartItemsRepository.class, methodName = "existsByUserIDAndBookID", useJWT = true, secondPathVariable = "bookID", message = "The book with the given ID is not in your cart items", groups = ExtendedValidation.class) //
    Integer bookID) {
        Integer userID = authUtils.getUserIdFromAuthentication();

        cartItemsService.deleteCartItem(userID, bookID);

        return ResponseEntity.noContent().build();
    }
}
