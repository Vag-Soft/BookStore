package com.vagsoft.bookstore.repositories;

import aj.org.objectweb.asm.commons.Remapper;
import com.vagsoft.bookstore.models.entities.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing cart items
 */
@Repository
public interface CartItemsRepository extends JpaRepository<CartItem, Integer> {
    /**
     * Retrieves all cart items by user ID
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
                INNER JOIN Cart c ON ci.cartID = c.id AND c.userID = :userID
            """)
    public Page<CartItem> findAllByUserID(Integer userID, Pageable pageable);

    /**
     * Checks if a cart item exists for a given user ID and book ID
     *
     * @param userID the ID of the user
     * @param bookID the ID of the book
     * @return true if the cart item exists, false otherwise
     */
    @Query("""
                SELECT COUNT(ci) > 0
                FROM CartItem ci
                INNER JOIN Cart c ON ci.book.id = :bookID AND ci.cartID = c.id AND c.userID = :userID
            """)
    public boolean existsByUserIDAndBookID(Integer userID, Integer bookID);


    /**
     * Retrieves a cart item by user ID and book ID
     *
     * @param userID the ID of the user
     * @param bookID the ID of the book
     * @return the cart item associated with the user ID and book ID
     */
    @Query("""
                SELECT ci
                FROM CartItem ci
                INNER JOIN Cart c ON ci.book.id = :bookID AND ci.cartID = c.id AND c.userID = :userID
            """)
    Optional<CartItem> findByUserIDAndBookID(Integer userID, Integer bookID);


    /**
     * Deletes a cart item by user ID and book ID
     *
     * @param userID the ID of the user
     * @param bookID the ID of the book
     * @return the number of deleted cart items (should be 1 if successful)
     */
    @Modifying
    @Query("""
                DELETE
                FROM CartItem ci
                WHERE ci.book.id = :bookID AND ci.cartID IN (
                    SELECT c.id
                    FROM Cart c
                    WHERE c.userID = :userID
                )
            """)
    Integer deleteByUserIDAndBookID(Integer userID, Integer bookID);

}
