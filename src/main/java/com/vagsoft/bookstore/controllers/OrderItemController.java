package com.vagsoft.bookstore.controllers;

import com.vagsoft.bookstore.annotations.ExistsCompositeResource;
import com.vagsoft.bookstore.annotations.ExistsResource;
import com.vagsoft.bookstore.annotations.IsAdmin;
import com.vagsoft.bookstore.dto.OrderItemReadDTO;
import com.vagsoft.bookstore.errors.exceptions.OrderItemNotFoundException;
import com.vagsoft.bookstore.repositories.CartItemsRepository;
import com.vagsoft.bookstore.repositories.OrderItemsRepository;
import com.vagsoft.bookstore.repositories.OrderRepository;
import com.vagsoft.bookstore.services.OrderItemService;
import com.vagsoft.bookstore.utils.AuthUtils;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/** REST controller for endpoints related to order items*/
@RestController
@RequestMapping(path = "/orders")
@Validated
public class OrderItemController {
    private static final Logger log = LoggerFactory.getLogger(OrderItemController.class);
    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    /**
     * Retrieves a page of order items for the specified order ID.
     *
     * @param orderID the ID of the order to retrieve items for
     * @param pageable the pagination information
     * @return a page of OrderItemReadDTO objects
     */
    @IsAdmin
    @GetMapping("/{orderID}/items")
    public ResponseEntity<Page<OrderItemReadDTO>> getOrderItems(
            @PathVariable @Positive @ExistsResource(repository = OrderRepository.class, message = "Order with the given ID does not exist") Integer orderID,
            Pageable pageable) {
        log.info("GET /orders/{}/items", orderID);

        return ResponseEntity.ok(orderItemService.getOrderItems(orderID, pageable));
    }

    /**
     * Retrieves an order item by the specified order ID and book ID.
     *
     * @param orderID the ID of the order
     * @param bookID the ID of the book
     * @param pageable the pagination information
     * @return an OrderItemReadDTO object representing the order item
     */
    @IsAdmin
    @GetMapping("/{orderID}/items/{bookID}")
    public ResponseEntity<OrderItemReadDTO> getOrderItemByBookID(
            @PathVariable @Positive Integer orderID,
            @PathVariable @Positive @ExistsCompositeResource(repository = OrderItemsRepository.class, methodName = "existsByOrderIdAndBookId", firstPathVariable = "orderID", secondPathVariable = "bookID", message = "The order with the given ID does not contain the book with the given ID")  Integer bookID,
            Pageable pageable) {
        log.info("GET /orders/{}/items/{}", orderID, bookID);

        Optional<OrderItemReadDTO> orderItem = orderItemService.getOrderItemByBookID(orderID, bookID);

        return ResponseEntity.ok(orderItem.orElseThrow(() -> new OrderItemNotFoundException("The order with ID: " + orderID + "does not contain the book with ID: " + bookID)));
    }

    /**
     * Retrieves a page of order items for the specified order ID, accessible only to the user who placed the order.
     *
     * @param orderID the ID of the order to retrieve items for
     * @param pageable the pagination information
     * @return a page of OrderItemReadDTO objects
     */
    @GetMapping("/me/{orderID}/items")
    public ResponseEntity<Page<OrderItemReadDTO>> getOrderMeItems(
            @PathVariable @Positive @ExistsCompositeResource(repository = OrderRepository.class, methodName = "existsByUserIDAndId", useJWT = true, secondPathVariable = "orderID", message = "The order with the given ID does not exist in your submitted orders") Integer orderID,
            Pageable pageable) {
        log.info("GET /orders/me/{}/items", orderID);

        return ResponseEntity.ok(orderItemService.getOrderItems(orderID, pageable));
    }

    /**
     * Retrieves an order item by the specified order ID and book ID, accessible only to the user who placed the order.
     *
     * @param orderID the ID of the order
     * @param bookID the ID of the book
     * @param pageable the pagination information
     * @return an OrderItemReadDTO object representing the order item
     */
    @GetMapping("/me/{orderID}/items/{bookID}")
    public ResponseEntity<OrderItemReadDTO> getOrderMeItemByBookID(
            @PathVariable @Positive @ExistsCompositeResource(repository = OrderRepository.class, methodName = "existsByUserIDAndId", useJWT = true, secondPathVariable = "orderID", message = "The order with the given ID does not exist in your submitted orders") Integer orderID,
            @PathVariable @Positive @ExistsCompositeResource(repository = OrderItemsRepository.class, methodName = "existsByOrderIdAndBookId", firstPathVariable = "orderID", secondPathVariable = "bookID", message = "The order with the given ID does not contain the book with the given ID")  Integer bookID,
            Pageable pageable) {
        log.info("GET /orders/{}/items/{}", orderID, bookID);

        Optional<OrderItemReadDTO> orderItem = orderItemService.getOrderItemByBookID(orderID, bookID);

        return ResponseEntity.ok(orderItem.orElseThrow(() -> new OrderItemNotFoundException("The order with ID: " + orderID + "does not contain the book with ID: " + bookID)));
    }
}
