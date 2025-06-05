package com.vagsoft.bookstore.services;

import com.vagsoft.bookstore.dto.orderDTOs.OrderItemReadDTO;
import com.vagsoft.bookstore.mappers.OrderItemMapper;
import com.vagsoft.bookstore.repositories.OrderItemsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service class for order item operations */
@Service
public class OrderItemService {
    private final OrderItemsRepository orderItemsRepository;
    private final OrderItemMapper orderItemMapper;

    public OrderItemService(OrderItemsRepository orderItemsRepository, OrderItemMapper orderItemMapper) {
        this.orderItemsRepository = orderItemsRepository;
        this.orderItemMapper = orderItemMapper;
    }

    /**
     * Retrieves a page of order items for the specified order ID.
     *
     * @param orderID
     *            the ID of the order to retrieve items for
     * @param pageable
     *            the pagination information
     * @return a page of OrderItemReadDTO objects
     */
    @Transactional(readOnly = true)
    public Page<OrderItemReadDTO> getOrderItems(Integer orderID, Pageable pageable) {
        return orderItemMapper.pageOrderItemToPageDto(orderItemsRepository.findAllByOrderId(orderID, pageable));
    }

    /**
     * Retrieves an order item by the specified order ID and book ID.
     *
     * @param orderID
     *            the ID of the order
     * @param bookID
     *            the ID of the book
     * @return an OrderItemReadDTO object representing the order item
     */
    @Transactional(readOnly = true)
    public OrderItemReadDTO getOrderItemByBookID(Integer orderID, Integer bookID) {
        return orderItemMapper.orderItemToReadDto(orderItemsRepository.getReferenceByOrderIdAndBookId(orderID, bookID));
    }
}
