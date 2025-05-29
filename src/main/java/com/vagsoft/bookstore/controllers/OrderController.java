package com.vagsoft.bookstore.controllers;

import com.vagsoft.bookstore.annotations.ExistsCompositeResource;
import com.vagsoft.bookstore.annotations.ExistsResource;
import com.vagsoft.bookstore.annotations.IsAdmin;
import com.vagsoft.bookstore.dto.OrderReadDTO;
import com.vagsoft.bookstore.dto.OrderUpdateDTO;
import com.vagsoft.bookstore.errors.exceptions.FavouriteCreationException;
import com.vagsoft.bookstore.errors.exceptions.OrderCreationException;
import com.vagsoft.bookstore.errors.exceptions.OrderNotFoundException;
import com.vagsoft.bookstore.models.enums.Status;
import com.vagsoft.bookstore.repositories.OrderRepository;
import com.vagsoft.bookstore.repositories.UserRepository;
import com.vagsoft.bookstore.services.OrderService;
import com.vagsoft.bookstore.utils.AuthUtils;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/** REST controller for endpoints related to orders */
@RestController
@RequestMapping(path = "/orders")
@Validated
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;
    private final AuthUtils authUtils;

    public OrderController(OrderService orderService, AuthUtils authUtils) {
        this.orderService = orderService;
        this.authUtils = authUtils;
    }

    /**
     * Retrieves a page of orders filtered by the specified parameters
     * @param userID the ID of the user who placed the orders (optional)
     * @param minTotalAmount the minimum total amount of the orders to search for (optional)
     * @param maxTotalAmount the maximum total amount of the orders to search for (optional)
     * @param status the status of the orders to search for (optional)
     * @param pageable the pagination information (optional)
     * @return a page of orders
     */
    @IsAdmin
    @GetMapping
    public ResponseEntity<Page<OrderReadDTO>> getOrders(
            @RequestParam(name = "userID", required = false) @Positive  @ExistsResource(repository = UserRepository.class, nullable = true, message = "User with given ID does not exist")  Integer userID,
            @RequestParam(name = "minTotalAmount", required = false) @Min(value = 0, message = "minTotalAmount must be equal or  greater than 0") Double minTotalAmount,
            @RequestParam(name = "maxTotalAmount", required = false) @Min(value = 0, message = "maxTotalAmount must be equal or  greater than 0") Double maxTotalAmount,
            @RequestParam(name = "status", required = false) Status status,
            Pageable pageable) {

        log.info("GET /orders: userID={}, minTotalAmount={}, maxTotalAmount={}, status={}, pageable={}",
                userID, minTotalAmount, maxTotalAmount, status, pageable);

        return ResponseEntity.ok(orderService.getOrders(userID, minTotalAmount, maxTotalAmount, status, pageable));
    }

    /**
     * Retrieves an order by its ID
     *
     * @param orderID the ID of the order to retrieve
     * @return the order with the specified ID
     */
    @IsAdmin
    @GetMapping(path = "/{orderID}")
    public ResponseEntity<OrderReadDTO> getOrderByID(
            @PathVariable @Positive @ExistsResource(repository = OrderRepository.class, message = "Order with given ID does not exist") Integer orderID) {

        log.info("GET /orders/{}", orderID);

        Optional<OrderReadDTO> order = orderService.getOrderByID(orderID);

        return ResponseEntity.ok(order.orElseThrow(() -> new OrderNotFoundException("No order found with the given ID: " + orderID)));
    }


    /**
     * Updates an order by its ID with the given order information
     *
     * @param orderID the ID of the order to be updated
     * @param orderUpdateDTO the new order information
     * @return the updated order
     */
    @IsAdmin
    @PutMapping(path = "/{orderID}")
    public ResponseEntity<OrderReadDTO> updateOrderByID(
            @PathVariable @Positive @ExistsResource(repository = OrderRepository.class, message = "Order with given ID does not exist") Integer orderID,
            @RequestBody @Valid OrderUpdateDTO orderUpdateDTO) {

        log.info("GET /orders/{}: orderUpdateDTO={}", orderID, orderUpdateDTO);

        Optional<OrderReadDTO> updatedOrder = orderService.updateOrderByID(orderID, orderUpdateDTO);

        return ResponseEntity.ok(updatedOrder.orElseThrow(() -> new OrderNotFoundException("No order found with the given ID: " + orderID)));
    }

    /**
     * Retrieves a page of orders for the authenticated user, filtered by the specified parameters
     *
     * @param minTotalAmount the minimum total amount of the orders to search for (optional)
     * @param maxTotalAmount the maximum total amount of the orders to search for (optional)
     * @param status the status of the orders to search for (optional)
     * @param pageable the pagination information (optional)
     * @return a page of orders
     */
    @GetMapping(path = "/me")
    public ResponseEntity<Page<OrderReadDTO>> getOrders(
            @RequestParam(name = "minTotalAmount", required = false) @Min(value = 0, message = "minTotalAmount must be equal or  greater than 0") Double minTotalAmount,
            @RequestParam(name = "maxTotalAmount", required = false) @Min(value = 0, message = "maxTotalAmount must be equal or  greater than 0") Double maxTotalAmount,
            @RequestParam(name = "status", required = false) Status status,
            Pageable pageable) {

        log.info("GET /orders/me: minTotalAmount={}, maxTotalAmount={}, status={}, pageable={}",
                minTotalAmount, maxTotalAmount, status, pageable);

        Integer userID = authUtils.getUserIdFromAuthentication();

        return ResponseEntity.ok(orderService.getOrders(userID, minTotalAmount, maxTotalAmount, status, pageable));
    }

    /**
     * Places a new order for the authenticated user
     *
     * @return the added order
     */
    @ApiResponse(responseCode = "201")
    @PostMapping(path = "/me")
    public ResponseEntity<OrderReadDTO> getOrders() {
        log.info("POST /orders/me");

        Integer userID = authUtils.getUserIdFromAuthentication();

        Optional<OrderReadDTO> savedOrder = orderService.addOrderByUserID(userID);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedOrder.orElseThrow(() -> new OrderCreationException("Order creation failed")));
    }

    /**
     * Retrieves an order by its ID, accessible only to the user who placed the order.
     *
     * @param orderID the ID of the order to retrieve
     * @return the order with the specified ID
     */
    @GetMapping(path = "/me/{orderID}")
    public ResponseEntity<OrderReadDTO> getOrderMeByID(
            @PathVariable @Positive @ExistsCompositeResource(repository = OrderRepository.class, methodName = "existsByUserIDAndId", useJWT = true, secondPathVariable = "orderID", message = "The order with the given ID does not exist in your submitted orders") Integer orderID) {

        log.info("GET /orders/me/{}", orderID);

        Optional<OrderReadDTO> order = orderService.getOrderByID(orderID);

        return ResponseEntity.ok(order.orElseThrow(() -> new OrderNotFoundException("No order found with the given ID: " + orderID)));
    }
}
