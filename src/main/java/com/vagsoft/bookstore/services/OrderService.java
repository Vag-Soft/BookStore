package com.vagsoft.bookstore.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.vagsoft.bookstore.dto.orderDTOs.OrderReadDTO;
import com.vagsoft.bookstore.dto.orderDTOs.OrderUpdateDTO;
import com.vagsoft.bookstore.errors.exceptions.cartExceptions.CartItemsNotFoundException;
import com.vagsoft.bookstore.mappers.OrderItemMapper;
import com.vagsoft.bookstore.mappers.OrderMapper;
import com.vagsoft.bookstore.models.entities.CartItem;
import com.vagsoft.bookstore.models.entities.Order;
import com.vagsoft.bookstore.models.entities.OrderItem;
import com.vagsoft.bookstore.models.enums.Status;
import com.vagsoft.bookstore.repositories.OrderRepository;
import com.vagsoft.bookstore.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service class for order operations. */
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartItemsService cartItemsService;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public OrderService(final OrderRepository orderRepository, final UserRepository userRepository,
            final CartItemsService cartItemsService, final OrderMapper orderMapper,
            final OrderItemMapper orderItemMapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartItemsService = cartItemsService;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    /**
     * Retrieves a page of orders filtered by the specified parameters.
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
    @Transactional(readOnly = true)
    public Page<OrderReadDTO> getOrders(final Integer userID, final Double minTotalAmount, final Double maxTotalAmount,
            final Status status, Pageable pageable) {
        return orderMapper.pageOrderToPageDto(
                orderRepository.findOrders(userID, minTotalAmount, maxTotalAmount, status, pageable));
    }

    /**
     * Places a new order for the user with the specified ID.
     *
     * @param userID
     *            the ID of the user placing the order
     * @return an Optional containing the created OrderReadDTO
     */
    @Transactional
    public Optional<OrderReadDTO> addOrderByUserID(final Integer userID) {
        // Checking out the user's cart items
        List<CartItem> cartItems = cartItemsService.checkout(userID);
        if (cartItems.isEmpty()) {
            throw new CartItemsNotFoundException("No items in the cart of the user with ID: " + userID);
        }

        // Mapping cart items to order items and creating an order
        Order orderToSave = new Order();

        List<OrderItem> orderItems = orderItemMapper.cartItemsToOrderItems(cartItems);
        orderItems.forEach(orderItem -> orderItem.setOrder(orderToSave));

        // Setting order properties
        orderToSave.setUser(userRepository.getReferenceById(userID));
        orderToSave.setOrderItems(orderItems);
        orderToSave.setStatus(Status.PROCESSING);
        orderToSave.setOrderDate(LocalDate.now());
        orderToSave.setTotalAmount(orderItems.stream()
                .mapToDouble(orderItem -> orderItem.getBook().getPrice() * orderItem.getQuantity()).sum());

        Order savedOrder = orderRepository.save(orderToSave);

        return Optional.of(orderMapper.orderToReadDto(savedOrder));
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param orderID
     *            the ID of the order to be retrieved
     * @return an Optional containing the OrderReadDTO
     */
    @Transactional(readOnly = true)
    public OrderReadDTO getOrderByID(final Integer orderID) {
        return orderMapper.orderToReadDto(orderRepository.getReferenceById(orderID));
    }

    /**
     * Updates an order by its ID with the given order information.
     *
     * @param orderID
     *            the ID of the order to be updated
     * @param orderUpdateDTO
     *            the new order information
     * @return an Optional containing the updated OrderReadDTO
     */
    @Transactional
    public Optional<OrderReadDTO> updateOrderByID(final Integer orderID, final OrderUpdateDTO orderUpdateDTO) {
        Order foundOrder = orderRepository.getReferenceById(orderID);

        orderMapper.updateOrderFromDto(orderUpdateDTO, foundOrder);

        Order updatedOrder = orderRepository.save(foundOrder);

        return Optional.of(orderMapper.orderToReadDto(updatedOrder));
    }
}
