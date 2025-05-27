package com.vagsoft.bookstore.repositories;

import com.vagsoft.bookstore.models.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

}
