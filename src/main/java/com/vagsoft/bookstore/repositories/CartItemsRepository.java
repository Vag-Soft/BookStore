package com.vagsoft.bookstore.repositories;

import java.util.List;
import java.util.Optional;

import com.vagsoft.bookstore.models.entities.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing cart items.
 */
@Repository
public interface CartItemsRepository extends JpaRepository<CartItem, Integer> {
    /**
     * Retrieves all cart items by user ID.
     *
     * @param userID
     *            the ID of the user
     * @param pageable
     *            pagination information
     * @return paginated list of cart items associated with the user ID
     */
    @Query("""
                SELECT ci
                FROM CartItem ci
                INNER JOIN Cart c ON ci.cart = c AND c.user.id = :userID
            """)
    Page<CartItem> findAllByUserID(Integer userID, Pageable pageable);

    /**
     * Retrieves all cart items by user ID.
     *
     * @param userID
     *            the ID of the user
     * @return list of cart items associated with the user ID
     */
    @Query("""
                SELECT ci
                FROM CartItem ci
                INNER JOIN Cart c ON ci.cart = c AND c.user.id = :userID
            """)
    List<CartItem> findAllByUserID(Integer userID);

    /**
     * Checks if a cart item exists for a given user ID and book ID.
     *
     * @param userID
     *            the ID of the user
     * @param bookID
     *            the ID of the book
     * @return true if the cart item exists, false otherwise
     */
    @Query("""
                SELECT COUNT(ci) > 0
                FROM CartItem ci
                INNER JOIN Cart c ON ci.book.id = :bookID AND ci.cart = c AND c.user.id = :userID
            """)
    boolean existsByUserIDAndBookID(Integer userID, Integer bookID);

    /**
     * Retrieves a cart item by user ID and book ID.
     *
     * @param userID
     *            the ID of the user
     * @param bookID
     *            the ID of the book
     * @return the cart item associated with the user ID and book ID
     */
    @Query("""
                SELECT ci
                FROM CartItem ci
                INNER JOIN Cart c ON ci.book.id = :bookID AND ci.cart = c AND c.user.id = :userID
            """)
    Optional<CartItem> findByUserIDAndBookID(Integer userID, Integer bookID);

    /**
     * Retrieves a cart item by user ID and book ID, returning a reference.
     *
     * @param userID
     *            the ID of the user
     * @param bookID
     *            the ID of the book
     * @return a reference to the cart item associated with the user ID and book ID
     */
    @Query("""
                SELECT ci
                FROM CartItem ci
                INNER JOIN Cart c ON ci.book.id = :bookID AND ci.cart = c AND c.user.id = :userID
            """)
    CartItem getReferenceByUserIDAndBookID(Integer userID, Integer bookID);

    /**
     * Deletes a cart item by user ID and book ID.
     *
     * @param userID
     *            the ID of the user
     * @param bookID
     *            the ID of the book
     */
    @Modifying
    @Query("""
                DELETE
                FROM CartItem ci
                WHERE ci.book.id = :bookID AND ci.cart IN (
                    SELECT c
                    FROM Cart c
                    WHERE c.user.id = :userID
                )
            """)
    void deleteByUserIDAndBookID(Integer userID, Integer bookID);

    /**
     * Deletes all cart items associated with a user ID.
     *
     * @param userID
     *            the ID of the user
     */
    @Modifying
    @Query("""
                DELETE
                FROM CartItem ci
                WHERE ci.cart IN (
                    SELECT c
                    FROM Cart c
                    WHERE c.user.id = :userID
                )
            """)
    void deleteAllByUserID(Integer userID);

}
