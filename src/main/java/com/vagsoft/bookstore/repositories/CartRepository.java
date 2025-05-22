package com.vagsoft.bookstore.repositories;

import com.vagsoft.bookstore.models.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing cart data
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
}
