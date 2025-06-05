package com.vagsoft.bookstore.unit.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.vagsoft.bookstore.controllers.OrderItemController;
import com.vagsoft.bookstore.dto.bookDTOs.BookReadDTO;
import com.vagsoft.bookstore.dto.genreDTOs.GenreDTO;
import com.vagsoft.bookstore.dto.orderDTOs.OrderItemReadDTO;
import com.vagsoft.bookstore.dto.orderDTOs.OrderReadDTO;
import com.vagsoft.bookstore.models.enums.Status;
import com.vagsoft.bookstore.repositories.BookRepository;
import com.vagsoft.bookstore.repositories.CartItemsRepository;
import com.vagsoft.bookstore.repositories.OrderItemsRepository;
import com.vagsoft.bookstore.repositories.OrderRepository;
import com.vagsoft.bookstore.repositories.UserRepository;
import com.vagsoft.bookstore.services.OrderItemService;
import com.vagsoft.bookstore.utils.AuthUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderItemController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ActiveProfiles("test")
public class OrderItemsControllerTest {
    @MockitoBean
    private OrderItemService orderItemService;
    @MockitoBean
    private OrderItemsRepository orderItemsRepository;
    @MockitoBean
    private OrderRepository orderRepository;
    @MockitoBean
    private BookRepository bookRepository;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private CartItemsRepository cartItemsRepository;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AuthUtils authUtils;

    BookReadDTO book1, book2;
    OrderItemReadDTO orderItem1, orderItem2, orderItem3;
    OrderReadDTO order1, order2;

    @BeforeEach
    void setUp() {
        book1 = new BookReadDTO(1, "The Lord of the Rings", "J. R. R. Tolkien",
                "The Lord of the Rings is a series of three fantasy novels written by English author and scholar J. R. R. Tolkien.",
                1178, 15.0, 5, "978-0-395-36381-0", new ArrayList<>());

        book2 = new BookReadDTO(2, "Harry Potter and the Philosopher's Stone", "J. K. Rowling",
                "Harry Potter and the Philosopher's Stone is a fantasy novel written by British author J. K. Rowling.",
                223, 20.0, 10, "978-0-7-152-20664-5", List.of(new GenreDTO(1, "Fantasy")));

        orderItem1 = new OrderItemReadDTO(1, 1, book1, 2);
        orderItem2 = new OrderItemReadDTO(2, 1, book2, 1);
        orderItem3 = new OrderItemReadDTO(3, 2, book1, 3);

        order1 = new OrderReadDTO(1, 1, 50.0, Status.PROCESSING, LocalDate.now(), List.of(orderItem1, orderItem2));
        order2 = new OrderReadDTO(2, 2, 60.0, Status.DELIVERED, LocalDate.now(), List.of(orderItem3));

    }

    @Test
    @DisplayName("GET /orders/1/items - Success")
    void getOrderItemsByOrderID() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        Page<OrderItemReadDTO> page = new PageImpl<>(List.of(orderItem1, orderItem2), pageable, 2);

        when(orderRepository.existsById(1)).thenReturn(true);
        when(orderItemService.getOrderItems(1, pageable)).thenReturn(page);

        mockMvc.perform(get("/orders/1/items")).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(orderItem1.getId()))
                .andExpect(jsonPath("$.content[0].orderID").value(orderItem1.getOrderID()))
                .andExpect(jsonPath("$.content[0].book.id").value(orderItem1.getBook().getId()))
                .andExpect(jsonPath("$.content[0].quantity").value(orderItem1.getQuantity()))
                .andExpect(jsonPath("$.content[1].id").value(orderItem2.getId()))
                .andExpect(jsonPath("$.content[1].orderID").value(orderItem2.getOrderID()))
                .andExpect(jsonPath("$.content[1].book.id").value(orderItem2.getBook().getId()))
                .andExpect(jsonPath("$.content[1].quantity").value(orderItem2.getQuantity()));
    }

    @Test
    @DisplayName("GET /orders/999/items - Order Not Found")
    void getOrderItemsByOrderIDNotFoundOrder() throws Exception {
        when(orderRepository.existsById(999)).thenReturn(false);

        mockMvc.perform(get("/orders/999/items"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /orders/-1/items - Invalid Order ID")
    void getOrderItemsByOrderIDInvalidOrderID() throws Exception {
        when(orderRepository.existsById(-1)).thenReturn(false);
        mockMvc.perform(get("/orders/-1/items"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /orders/1/items/1 - Success")
    void getOrderItemByOrderIDAndBookID() throws Exception {
        when(orderRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(1)).thenReturn(true);
        when(orderItemsRepository.existsByOrderIdAndBookId(1, 1)).thenReturn(true);
        when(orderItemService.getOrderItemByBookID(1, 1)).thenReturn(orderItem1);

        mockMvc.perform(get("/orders/1/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderItem1.getId()))
                .andExpect(jsonPath("$.orderID").value(orderItem1.getOrderID()))
                .andExpect(jsonPath("$.book.id").value(orderItem1.getBook().getId()))
                .andExpect(jsonPath("$.quantity").value(orderItem1.getQuantity()));
    }

    @Test
    @DisplayName("GET /orders/999/items/1 - Order Not Found")
    void getOrderItemByOrderIDAndBookIDNotFoundOrder() throws Exception {
        when(orderRepository.existsById(999)).thenReturn(false);
        when(bookRepository.existsById(1)).thenReturn(true);
        when(orderItemsRepository.existsByOrderIdAndBookId(999, 1)).thenReturn(false);

        mockMvc.perform(get("/orders/999/items/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /orders/-1/items/1 - Invalid Order ID")
    void getOrderItemByOrderIDAndBookIDInvalidOrderID() throws Exception {
        when(orderRepository.existsById(-1)).thenReturn(false);
        when(bookRepository.existsById(1)).thenReturn(true);
        when(orderItemsRepository.existsByOrderIdAndBookId(-1, 1)).thenReturn(false);

        mockMvc.perform(get("/orders/-1/items/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /orders/1/items/999 - Book Not Found")
    void getOrderItemByOrderIDAndBookIDNotFoundBook() throws Exception {
        when(orderRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(999)).thenReturn(false);
        when(orderItemsRepository.existsByOrderIdAndBookId(1, 999)).thenReturn(false);

        mockMvc.perform(get("/orders/1/items/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /orders/1/items/-1 - Invalid Book ID")
    void getOrderItemByOrderIDAndBookIDInvalidBookID() throws Exception {
        when(orderRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(-1)).thenReturn(false);
        when(orderItemsRepository.existsByOrderIdAndBookId(1, -1)).thenReturn(false);

        mockMvc.perform(get("/orders/1/items/-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /orders/999/items/999 - Order and Book Not Found")
    void getOrderItemByOrderIDAndBookIDNotFoundBoth() throws Exception {
        when(orderRepository.existsById(999)).thenReturn(false);
        when(bookRepository.existsById(999)).thenReturn(false);
        when(orderItemsRepository.existsByOrderIdAndBookId(999, 999)).thenReturn(false);

        mockMvc.perform(get("/orders/999/items/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /orders/-1/items/-1 - Invalid Order and Book ID")
    void getOrderItemByOrderIDAndBookIDInvalidBoth() throws Exception {
        when(orderRepository.existsById(-1)).thenReturn(false);
        when(bookRepository.existsById(-1)).thenReturn(false);
        when(orderItemsRepository.existsByOrderIdAndBookId(-1, -1)).thenReturn(false);

        mockMvc.perform(get("/orders/-1/items/-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /orders/me/1/items - Success")
    void getOrderItemsMeByOrderID() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        Page<OrderItemReadDTO> page = new PageImpl<>(List.of(orderItem1, orderItem2), pageable, 2);

        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(orderRepository.existsById(1)).thenReturn(true);
        when(orderRepository.existsByUser_IdAndId(1, 1)).thenReturn(true);
        when(orderItemService.getOrderItems(1, pageable)).thenReturn(page);

        mockMvc.perform(get("/orders/me/1/items")).andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(orderItem1.getId()))
                .andExpect(jsonPath("$.content[0].orderID").value(orderItem1.getOrderID()))
                .andExpect(jsonPath("$.content[0].book.id").value(orderItem1.getBook().getId()))
                .andExpect(jsonPath("$.content[0].quantity").value(orderItem1.getQuantity()))
                .andExpect(jsonPath("$.content[1].id").value(orderItem2.getId()))
                .andExpect(jsonPath("$.content[1].orderID").value(orderItem2.getOrderID()))
                .andExpect(jsonPath("$.content[1].book.id").value(orderItem2.getBook().getId()))
                .andExpect(jsonPath("$.content[1].quantity").value(orderItem2.getQuantity()));
    }

    @Test
    @DisplayName("GET /orders/me/1/items - Error JWT")
    void getOrderItemsMeByOrderIDErrorJWT() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenThrow(new IllegalArgumentException("Invalid JWT token"));

        mockMvc.perform(get("/orders/me/1/items"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /orders/me/999/items - Order Not Found")
    void getOrderItemsMeByOrderIDNotFoundOrder() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(orderRepository.existsById(999)).thenReturn(false);
        when(orderRepository.existsByUser_IdAndId(1, 999)).thenReturn(false);

        mockMvc.perform(get("/orders/me/999/items"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /orders/me/-1/items - Invalid Order ID")
    void getOrderItemsMeByOrderIDInvalidOrder() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(orderRepository.existsById(-1)).thenReturn(false);
        when(orderRepository.existsByUser_IdAndId(1, -1)).thenReturn(false);

        mockMvc.perform(get("/orders/me/-1/items"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /orders/me/1/items/1 - Success")
    void getOrderItemsMeByOrderIDAndBookID() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(orderRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(1)).thenReturn(true);
        when(orderRepository.existsByUser_IdAndId(1, 1)).thenReturn(true);
        when(orderItemsRepository.existsByOrderIdAndBookId(1, 1)).thenReturn(true);
        when(orderItemService.getOrderItemByBookID(1, 1)).thenReturn(orderItem1);

        mockMvc.perform(get("/orders/me/1/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderItem1.getId()))
                .andExpect(jsonPath("$.orderID").value(orderItem1.getOrderID()))
                .andExpect(jsonPath("$.book.id").value(orderItem1.getBook().getId()))
                .andExpect(jsonPath("$.quantity").value(orderItem1.getQuantity()));
    }

    @Test
    @DisplayName("GET /orders/me/1/items/1 - Error JWT")
    void getOrderItemsMeByOrderIDAndBookIDErrorJWT() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenThrow(new IllegalArgumentException("Invalid JWT token"));

        mockMvc.perform(get("/orders/me/1/items/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /orders/me/999/items/1 - Order Not Found")
    void getOrderItemsMeByOrderIDAndBookIDNotFoundOrder() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(orderRepository.existsById(999)).thenReturn(false);
        when(bookRepository.existsById(1)).thenReturn(true);
        when(orderRepository.existsByUser_IdAndId(1, 999)).thenReturn(false);
        when(orderItemsRepository.existsByOrderIdAndBookId(999, 1)).thenReturn(false);

        mockMvc.perform(get("/orders/me/999/items/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /orders/me/-1/items/1 - Invalid Order ID")
    void getOrderItemsMeByOrderIDAndBookIDInvalidOrder() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(orderRepository.existsById(-1)).thenReturn(false);
        when(bookRepository.existsById(1)).thenReturn(true);
        when(orderRepository.existsByUser_IdAndId(1, -1)).thenReturn(false);
        when(orderItemsRepository.existsByOrderIdAndBookId(-1, 1)).thenReturn(false);

        mockMvc.perform(get("/orders/me/-1/items/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /orders/me/1/items/999 - Book Not Found")
    void getOrderItemsMeByOrderIDAndBookIDNotFoundBook() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(orderRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(999)).thenReturn(false);
        when(orderRepository.existsByUser_IdAndId(1, 1)).thenReturn(true);
        when(orderItemsRepository.existsByOrderIdAndBookId(1, 999)).thenReturn(false);

        mockMvc.perform(get("/orders/me/1/items/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /orders/me/1/items/-1 - Invalid Book ID")
    void getOrderItemsMeByOrderIDAndBookIDInvalidBook() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(orderRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(-1)).thenReturn(false);
        when(orderRepository.existsByUser_IdAndId(1, 1)).thenReturn(true);
        when(orderItemsRepository.existsByOrderIdAndBookId(1, -1)).thenReturn(false);

        mockMvc.perform(get("/orders/me/1/items/-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /orders/me/999/items/999 - Order and Book Not Found")
    void getOrderItemsMeByOrderIDAndBookIDNotFoundBoth() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(orderRepository.existsById(999)).thenReturn(false);
        when(bookRepository.existsById(999)).thenReturn(false);
        when(orderRepository.existsByUser_IdAndId(1, 999)).thenReturn(false);
        when(orderItemsRepository.existsByOrderIdAndBookId(999, 999)).thenReturn(false);

        mockMvc.perform(get("/orders/me/999/items/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /orders/me/-1/items/-1 - Invalid Order and Book ID")
    void getOrderItemsMeByOrderIDAndBookIDInvalidBoth() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(orderRepository.existsById(-1)).thenReturn(false);
        when(bookRepository.existsById(-1)).thenReturn(false);
        when(orderRepository.existsByUser_IdAndId(1, -1)).thenReturn(false);
        when(orderItemsRepository.existsByOrderIdAndBookId(-1, -1)).thenReturn(false);

        mockMvc.perform(get("/orders/me/-1/items/-1"))
                .andExpect(status().isBadRequest());
    }
}
