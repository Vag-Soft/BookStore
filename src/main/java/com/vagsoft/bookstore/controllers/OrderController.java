package com.vagsoft.bookstore.controllers;

import java.util.Optional;

import com.vagsoft.bookstore.dto.orderDTOs.OrderReadDTO;
import com.vagsoft.bookstore.dto.orderDTOs.OrderUpdateDTO;
import com.vagsoft.bookstore.errors.exceptions.OrderCreationException;
import com.vagsoft.bookstore.errors.exceptions.OrderNotFoundException;
import com.vagsoft.bookstore.errors.exceptions.OrderUpdateException;
import com.vagsoft.bookstore.models.enums.Status;
import com.vagsoft.bookstore.repositories.OrderRepository;
import com.vagsoft.bookstore.repositories.UserRepository;
import com.vagsoft.bookstore.services.OrderService;
import com.vagsoft.bookstore.utils.AuthUtils;
import com.vagsoft.bookstore.validations.annotations.ExistsCompositeResource;
import com.vagsoft.bookstore.validations.annotations.ExistsResource;
import com.vagsoft.bookstore.validations.annotations.IsAdmin;
import com.vagsoft.bookstore.validations.groups.BasicValidation;
import com.vagsoft.bookstore.validations.groups.ExtendedValidation;
import com.vagsoft.bookstore.validations.groups.OrderedValidation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
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

/** REST controller for endpoints related to orders */
@RestController
@RequestMapping(path = "/orders")
@Validated(OrderedValidation.class)
public class OrderController {
    private final OrderService orderService;
    private final AuthUtils authUtils;

    public OrderController(OrderService orderService, AuthUtils authUtils) {
        this.orderService = orderService;
        this.authUtils = authUtils;
    }

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
    @IsAdmin
    @GetMapping
    public ResponseEntity<Page<OrderReadDTO>> getOrders(
            @RequestParam(name = "userID", required = false) @Positive(groups = BasicValidation.class) @ExistsResource(repository = UserRepository.class, nullable = true, message = "User with given ID does not exist", groups = ExtendedValidation.class) Integer userID,
            @RequestParam(name = "minTotalAmount", required = false) @Min(value = 0, message = "minTotalAmount must be equal or  greater than 0", groups = BasicValidation.class) Double minTotalAmount,
            @RequestParam(name = "maxTotalAmount", required = false) @Min(value = 0, message = "maxTotalAmount must be equal or  greater than 0", groups = BasicValidation.class) Double maxTotalAmount,
            @RequestParam(name = "status", required = false) Status status, Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrders(userID, minTotalAmount, maxTotalAmount, status, pageable));
    }

    /**
     * Retrieves an order by its ID
     *
     * @param orderID
     *            the ID of the order to retrieve
     * @return the order with the specified ID
     */
    @IsAdmin
    @GetMapping(path = "/{orderID}")
    public ResponseEntity<OrderReadDTO> getOrderByID(
            @PathVariable @Positive(groups = BasicValidation.class) @ExistsResource(repository = OrderRepository.class, message = "Order with given ID does not exist", groups = ExtendedValidation.class) Integer orderID) {
        OrderReadDTO order = orderService.getOrderByID(orderID);

        return ResponseEntity.ok(order);
    }

    /**
     * Updates an order by its ID with the given order information
     *
     * @param orderID
     *            the ID of the order to be updated
     * @param orderUpdateDTO
     *            the new order information
     * @return the updated order
     */
    @IsAdmin
    @PutMapping(path = "/{orderID}")
    public ResponseEntity<OrderReadDTO> updateOrderByID(
            @PathVariable @Positive(groups = BasicValidation.class) @ExistsResource(repository = OrderRepository.class, message = "Order with given ID does not exist", groups = ExtendedValidation.class) Integer orderID,
            @RequestBody @Valid OrderUpdateDTO orderUpdateDTO) {
        Optional<OrderReadDTO> updatedOrder = orderService.updateOrderByID(orderID, orderUpdateDTO);

        return ResponseEntity.ok(updatedOrder
                .orElseThrow(() -> new OrderUpdateException("Order with ID: " + orderID + " update failed")));
    }

    /**
     * Retrieves a page of orders for the authenticated user, filtered by the
     * specified parameters
     *
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
    @GetMapping(path = "/me")
    public ResponseEntity<Page<OrderReadDTO>> getOrders(
            @RequestParam(name = "minTotalAmount", required = false) @Min(value = 0, message = "minTotalAmount must be equal or  greater than 0", groups = BasicValidation.class) Double minTotalAmount,
            @RequestParam(name = "maxTotalAmount", required = false) @Min(value = 0, message = "maxTotalAmount must be equal or  greater than 0", groups = BasicValidation.class) Double maxTotalAmount,
            @RequestParam(name = "status", required = false) Status status, Pageable pageable) {
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
    public ResponseEntity<OrderReadDTO> addOrder() {
        Integer userID = authUtils.getUserIdFromAuthentication();

        Optional<OrderReadDTO> savedOrder = orderService.addOrderByUserID(userID);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedOrder.orElseThrow(() -> new OrderCreationException("Order creation failed")));
    }

    /**
     * Retrieves an order by its ID, accessible only to the user who placed the
     * order.
     *
     * @param orderID
     *            the ID of the order to retrieve
     * @return the order with the specified ID
     */
    @GetMapping(path = "/me/{orderID}")
    public ResponseEntity<OrderReadDTO> getOrderMeByID(
            @PathVariable @Positive(groups = BasicValidation.class) @ExistsCompositeResource(repository = OrderRepository.class, methodName = "existsByUser_IdAndId", useJWT = true, secondPathVariable = "orderID", message = "The order with the given ID does not exist in your submitted orders", groups = ExtendedValidation.class) Integer orderID) {
        OrderReadDTO order = orderService.getOrderByID(orderID);

        return ResponseEntity.ok(order);
    }
}
