package com.vagsoft.bookstore.repositories;

import java.util.Optional;

import com.vagsoft.bookstore.models.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing cart data
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    /**
     * Finds a cart by its user ID
     *
     * @param userID
     *            the ID of the user whose cart is to be found
     * @return an Optional containing the Cart if found, or empty if not found
     */
    Optional<Cart> findByUser_Id(Integer userID);

    /**
     * Gets a reference to a cart by its user ID
     *
     * @param userID
     *            the ID of the user whose cart is to be referenced
     * @return the Cart entity associated with the user ID
     */
    Cart getReferenceByUser_Id(Integer userID);
}
