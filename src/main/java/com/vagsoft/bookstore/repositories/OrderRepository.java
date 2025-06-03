package com.vagsoft.bookstore.repositories;

import com.vagsoft.bookstore.models.entities.Order;
import com.vagsoft.bookstore.models.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/** Repository interface for accessing order data */
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    /**
     * Retrieves a page of orders filtered by the specified parameters
     *
     * @param userID
     *            the ID of the user who placed the orders (optional)
     * @param minTotalAmount
     *            the minimum total amount of the orders to search for (optional)
     * @param maxTotalAmount
     *            the maximum total amount of the orders to search for (optional)
     * @param status
     *            the status of the orders to search for (optional)
     * @param pageable
     *            the pagination information (optional)
     * @return a page of orders
     */
    @Query("""
                SELECT o
                FROM Order o
                WHERE (:userID IS NULL OR o.user.id = :userID)
                AND (:minTotalAmount IS NULL OR o.totalAmount >= :minTotalAmount)
                AND (:maxTotalAmount IS NULL OR o.totalAmount <= :maxTotalAmount)
                AND (:status IS NULL OR o.status = :status)
            """)
    Page<Order> findOrders(Integer userID, Double minTotalAmount, Double maxTotalAmount, Status status,
            Pageable pageable);

    /**
     * Checks if an order with the given ID exists for the specified user
     *
     * @param userID
     *            the ID of the user who placed the order
     * @param orderID
     *            the ID of the order to be checked
     * @return true if the order exists, false otherwise
     */
    boolean existsByUser_IdAndId(Integer userID, Integer orderID);
}
