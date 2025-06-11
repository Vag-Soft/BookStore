package com.vagsoft.bookstore.services;

import java.util.List;
import java.util.Optional;

import com.vagsoft.bookstore.dto.cartDTOs.CartItemReadDTO;
import com.vagsoft.bookstore.dto.cartDTOs.CartItemUpdateDTO;
import com.vagsoft.bookstore.dto.cartDTOs.CartItemWriteDTO;
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
 * Service class for handling cart item-related operations.
 */
@Service
public class CartItemsService {
    private final CartItemsRepository cartItemsRepository;
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;
    private final CartItemMapper cartItemMapper;

    public CartItemsService(final CartItemsRepository cartItemsRepository, final CartRepository cartRepository,
            final BookRepository bookRepository, final BookService bookService, final CartItemMapper cartItemMapper) {
        this.cartItemsRepository = cartItemsRepository;
        this.cartRepository = cartRepository;
        this.bookRepository = bookRepository;
        this.bookService = bookService;
        this.cartItemMapper = cartItemMapper;
    }

    /**
     * Retrieves all cart items for a given user.
     *
     * @param userID
     *            the ID of the user
     * @param pageable
     *            pagination information
     * @return paginated list of cart items for the user
     */
    @Transactional(readOnly = true)
    public Page<CartItemReadDTO> getAllCartItems(final Integer userID, final Pageable pageable) {
        return cartItemMapper.pageCartItemsToPageDto(cartItemsRepository.findAllByUserID(userID, pageable));
    }

    /**
     * Retrieves a specific cart item for a given user using the book's ID.
     *
     * @param userID
     *            the ID of the user
     * @param bookID
     *            the ID of the book
     * @return the cart item associated with the user and book's ID
     */
    @Transactional(readOnly = true)
    public CartItemReadDTO getCartItem(final Integer userID, final Integer bookID) {
        return cartItemMapper.cartItemToReadDto(cartItemsRepository.getReferenceByUserIDAndBookID(userID, bookID));
    }

    /**
     * Adds a new cart item for a given user.
     *
     * @param cartItemWriteDTO
     *            the cart item to be added
     * @return the created cart item
     */
    @Transactional
    public Optional<CartItemReadDTO> addCartItem(final Integer userID, final CartItemWriteDTO cartItemWriteDTO) {
        CartItem cartItemToSave = CartItem.builder().quantity(cartItemWriteDTO.getQuantity())
                .book(bookRepository.getReferenceById(cartItemWriteDTO.getBookID()))
                .cart(cartRepository.getReferenceByUser_Id(userID)).build();

        // Check the book's availability before adding to the cart
        if (!bookService.isBookQuantityAvailable(cartItemWriteDTO.getBookID(), cartItemWriteDTO.getQuantity())) {
            throw new IllegalArgumentException("Not enough stock for book with ID: " + cartItemWriteDTO.getBookID());
        }

        CartItem savedCartItem = cartItemsRepository.save(cartItemToSave);
        return Optional.of(cartItemMapper.cartItemToReadDto(savedCartItem));
    }

    /**
     * Updates a specific cart item for a given user using the book's ID.
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
    public Optional<CartItemReadDTO> updateCartItem(final Integer userID, final Integer bookID,
            final CartItemUpdateDTO cartItemUpdateDTO) {
        CartItem cartItem = cartItemsRepository.getReferenceByUserIDAndBookID(userID, bookID);

        cartItem.setQuantity(cartItemUpdateDTO.getQuantity());
        CartItem updatedCartItem = cartItemsRepository.save(cartItem);

        return Optional.of(cartItemMapper.cartItemToReadDto(updatedCartItem));
    }

    /**
     * Deletes a specific cart item for a given user using the book's ID.
     *
     * @param userID
     *            the ID of the user
     * @param bookID
     *            the ID of the book
     */
    @Transactional
    public void deleteCartItem(final Integer userID, final Integer bookID) {
        cartItemsRepository.deleteByUserIDAndBookID(userID, bookID);
    }

    /**
     * Retrieves all cart items for a given user and deletes them from the cart.
     *
     * @param userID
     *            the ID of the user
     * @return a list of cart items that were checked out
     */
    @Transactional
    public List<CartItem> checkout(final Integer userID) {
        List<CartItem> cartItems = cartItemsRepository.findAllByUserID(userID);
        cartItemsRepository.deleteAllByUserID(userID);
        return cartItems;
    }
}
