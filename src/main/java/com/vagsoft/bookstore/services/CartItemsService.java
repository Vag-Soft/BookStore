package com.vagsoft.bookstore.services;

import java.util.List;
import java.util.Optional;

import com.vagsoft.bookstore.dto.cartDTOs.CartItemReadDTO;
import com.vagsoft.bookstore.dto.cartDTOs.CartItemUpdateDTO;
import com.vagsoft.bookstore.dto.cartDTOs.CartItemWriteDTO;
import com.vagsoft.bookstore.errors.exceptions.cartExceptions.CartItemsNotFoundException;
import com.vagsoft.bookstore.mappers.CartItemMapper;
import com.vagsoft.bookstore.models.entities.CartItem;
import com.vagsoft.bookstore.repositories.BookRepository;
import com.vagsoft.bookstore.repositories.CartItemsRepository;
import com.vagsoft.bookstore.repositories.CartRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for handling cart item-related operations
 */
@Service
public class CartItemsService {
    private final CartItemsRepository cartItemsRepository;
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;
    private final CartItemMapper cartItemMapper;

    public CartItemsService(CartItemsRepository cartItemsRepository, CartRepository cartRepository,
            BookRepository bookRepository, BookService bookService, CartItemMapper cartItemMapper) {
        this.cartItemsRepository = cartItemsRepository;
        this.cartRepository = cartRepository;
        this.bookRepository = bookRepository;
        this.bookService = bookService;
        this.cartItemMapper = cartItemMapper;
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
    @Transactional(readOnly = true)
    public Page<CartItemReadDTO> getAllCartItems(Integer userID, Pageable pageable) {
        return cartItemMapper.pageCartItemsToPageDto(cartItemsRepository.findAllByUserID(userID, pageable));
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
    @Transactional(readOnly = true)
    public CartItemReadDTO getCartItem(Integer userID, Integer bookID) {
        return cartItemMapper.cartItemToReadDto(cartItemsRepository.getReferenceByUserIDAndBookID(userID, bookID));
    }

    /**
     * Adds a new cart item for a given user
     *
     * @param cartItemWriteDTO
     *            the cart item to be added
     * @return the created cart item
     */
    @Transactional
    public Optional<CartItemReadDTO> addCartItem(Integer userID, CartItemWriteDTO cartItemWriteDTO) {
        // Requesting books from the book service to ensure availability
        bookService.requestBooks(cartItemWriteDTO.getBookID(), cartItemWriteDTO.getQuantity());

        CartItem cartItemToSave = CartItem.builder().quantity(cartItemWriteDTO.getQuantity())
                .book(bookRepository.getReferenceById(cartItemWriteDTO.getBookID()))
                .cart(cartRepository.getReferenceByUser_Id(userID)).build();

        CartItem savedCartItem = cartItemsRepository.save(cartItemToSave);
        return Optional.of(cartItemMapper.cartItemToReadDto(savedCartItem));
    }

    /**
     * Updates a specific cart item for a given user using the book's ID
     *
     * @param userID
     *            the ID of the user
     * @param bookID
     *            the ID of the book
     * @param cartItemUpdateDTO
     *            the updated cart item information
     * @return the updated cart item
     */
    @Transactional
    public Optional<CartItemReadDTO> updateCartItem(Integer userID, Integer bookID,
            CartItemUpdateDTO cartItemUpdateDTO) {
        // Requesting books from the book service to ensure availability
        bookService.requestBooks(bookID, cartItemUpdateDTO.getQuantity());

        CartItem cartItem = cartItemsRepository.getReferenceByUserIDAndBookID(userID, bookID);

        cartItem.setQuantity(cartItemUpdateDTO.getQuantity());
        CartItem updatedCartItem = cartItemsRepository.save(cartItem);

        return Optional.of(cartItemMapper.cartItemToReadDto(updatedCartItem));
    }

    /**
     * Deletes a specific cart item for a given user using the book's ID
     *
     * @param userID
     *            the ID of the user
     * @param bookID
     *            the ID of the book
     */
    @Transactional
    public void deleteCartItem(Integer userID, Integer bookID) {
        CartItem cartItem = cartItemsRepository.getReferenceByUserIDAndBookID(userID, bookID);

        // Returning books to the book service to increase availability
        bookService.returnBooks(bookID, cartItem.getQuantity());

        cartItemsRepository.deleteByUserIDAndBookID(userID, bookID);
    }

    /**
     * Retrieves all cart items for a given user and deletes them from the cart
     *
     * @param userID
     *            the ID of the user
     * @return a list of cart items that were checked out
     * @throws CartItemsNotFoundException
     *             if no items are found in the user's cart
     */
    @Transactional
    public List<CartItem> checkout(Integer userID) {
        List<CartItem> cartItems = cartItemsRepository.findAllByUserID(userID);
        cartItemsRepository.deleteAllByUserID(userID);
        return cartItems;
    }
}
