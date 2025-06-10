package com.vagsoft.bookstore.repositories;

import java.util.Optional;

import com.vagsoft.bookstore.models.entities.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository interface for accessing order item data. */
@Repository
public interface OrderItemsRepository extends JpaRepository<OrderItem, Integer> {
    /**
     * Retrieves a page of order items for the specified order ID.
     *
     * @param orderID
     *            the ID of the order to retrieve items for
     * @param pageable
     *            the pagination information
     * @return a page of OrderItem entities
     */
    Page<OrderItem> findAllByOrderId(Integer orderID, Pageable pageable);

    /**
     * Checks if an order item exists for the specified order ID and book ID.
     *
     * @param orderID
     *            the ID of the order
     * @param bookID
     *            the ID of the book
     * @return true if the order item exists, false otherwise
     */
    boolean existsByOrderIdAndBookId(Integer orderID, Integer bookID);

    /**
     * Finds an order item by the specified order ID and book ID.
     *
     * @param orderID
     *            the ID of the order
     * @param bookID
     *            the ID of the book
     * @return an Optional containing the OrderItem if found, or empty if not found
     */
    Optional<OrderItem> findByOrderIdAndBookId(Integer orderID, Integer bookID);

    /**
     * Retrieves a reference to an order item by the specified order ID and book ID.
     *
     * @param orderID
     *            the ID of the order
     * @param bookID
     *            the ID of the book
     * @return an OrderItem entity reference
     */
    OrderItem getReferenceByOrderIdAndBookId(Integer orderID, Integer bookID);
}
