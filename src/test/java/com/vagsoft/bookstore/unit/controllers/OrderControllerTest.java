package com.vagsoft.bookstore.unit.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vagsoft.bookstore.controllers.OrderController;
import com.vagsoft.bookstore.dto.bookDTOs.BookReadDTO;
import com.vagsoft.bookstore.dto.genreDTOs.GenreDTO;
import com.vagsoft.bookstore.dto.orderDTOs.OrderItemReadDTO;
import com.vagsoft.bookstore.dto.orderDTOs.OrderReadDTO;
import com.vagsoft.bookstore.dto.orderDTOs.OrderUpdateDTO;
import com.vagsoft.bookstore.models.entities.Book;
import com.vagsoft.bookstore.models.entities.Cart;
import com.vagsoft.bookstore.models.entities.CartItem;
import com.vagsoft.bookstore.models.entities.User;
import com.vagsoft.bookstore.models.enums.Status;
import com.vagsoft.bookstore.repositories.BookRepository;
import com.vagsoft.bookstore.repositories.CartItemsRepository;
import com.vagsoft.bookstore.repositories.OrderRepository;
import com.vagsoft.bookstore.repositories.UserRepository;
import com.vagsoft.bookstore.services.OrderService;
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

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ActiveProfiles("test")
public class OrderControllerTest {
    @MockitoBean
    private OrderService orderService;
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
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private AuthUtils authUtils;

    BookReadDTO book1, book2;
    OrderItemReadDTO orderItem1, orderItem2, orderItem3;
    OrderReadDTO order1, order2;
    List<OrderReadDTO> storedOrders;

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

        storedOrders = new ArrayList<>();
        storedOrders.add(order1);
        storedOrders.add(order2);
    }

    @Test
    @DisplayName("GET /orders - Success No Filters")
    void getOrdersNoFilters() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        Page<OrderReadDTO> page = new PageImpl<>(storedOrders, pageable, storedOrders.size());

        when(orderService.getOrders(null, null, null, null, pageable)).thenReturn(page);

        mockMvc.perform(get("/orders").param("page", "0").param("size", "20").accept("application/json"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(order1.getId()))
                .andExpect(jsonPath("$.content[0].userID").value(order1.getUserID()))
                .andExpect(jsonPath("$.content[0].totalAmount").value(order1.getTotalAmount()))
                .andExpect(jsonPath("$.content[0].status").value(order1.getStatus().toString()))
                .andExpect(jsonPath("$.content[0].orderDate").value(order1.getOrderDate().toString()))
                .andExpect(jsonPath("$.content[0].orderItems", hasSize(2)))
                .andExpect(jsonPath("$.content[0].orderItems[0].id").value(orderItem1.getId()))
                .andExpect(jsonPath("$.content[0].orderItems[0].book.id").value(book1.getId()))
                .andExpect(jsonPath("$.content[0].orderItems[0].quantity").value(orderItem1.getQuantity()))
                .andExpect(jsonPath("$.content[0].orderItems[1].id").value(orderItem2.getId()))
                .andExpect(jsonPath("$.content[0].orderItems[1].book.id").value(book2.getId()))
                .andExpect(jsonPath("$.content[0].orderItems[1].quantity").value(orderItem2.getQuantity()))
                .andExpect(jsonPath("$.content[1].id").value(order2.getId()))
                .andExpect(jsonPath("$.content[1].userID").value(order2.getUserID()))
                .andExpect(jsonPath("$.content[1].totalAmount").value(order2.getTotalAmount()))
                .andExpect(jsonPath("$.content[1].status").value(order2.getStatus().toString()))
                .andExpect(jsonPath("$.content[1].orderDate").value(order2.getOrderDate().toString()))
                .andExpect(jsonPath("$.content[1].orderItems", hasSize(1)))
                .andExpect(jsonPath("$.content[1].orderItems[0].id").value(orderItem3.getId()))
                .andExpect(jsonPath("$.content[1].orderItems[0].book.id").value(book1.getId()))
                .andExpect(jsonPath("$.content[1].orderItems[0].quantity").value(orderItem3.getQuantity()));
    }

    @Test
    @DisplayName("GET /orders - Success With Filters")
    void getOrdersWithFilters() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        Page<OrderReadDTO> page = new PageImpl<>(storedOrders, pageable, storedOrders.size());

        when(userRepository.existsById(1)).thenReturn(true);
        when(orderService.getOrders(1, 50.0, 100.0, Status.PROCESSING, pageable)).thenReturn(page);

        mockMvc.perform(get("/orders").param("userID", "1").param("minTotalAmount", "50.0")
                .param("maxTotalAmount", "100.0").param("status", Status.PROCESSING.toString()).param("page", "0")
                .param("size", "20").accept("application/json")).andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(order1.getId()))
                .andExpect(jsonPath("$.content[0].userID").value(order1.getUserID()))
                .andExpect(jsonPath("$.content[0].totalAmount").value(order1.getTotalAmount()))
                .andExpect(jsonPath("$.content[0].status").value(order1.getStatus().toString()))
                .andExpect(jsonPath("$.content[0].orderDate").value(order1.getOrderDate().toString()))
                .andExpect(jsonPath("$.content[0].orderItems", hasSize(2)))
                .andExpect(jsonPath("$.content[0].orderItems[0].id").value(orderItem1.getId()))
                .andExpect(jsonPath("$.content[0].orderItems[0].book.id").value(book1.getId()))
                .andExpect(jsonPath("$.content[0].orderItems[0].quantity").value(orderItem1.getQuantity()))
                .andExpect(jsonPath("$.content[0].orderItems[1].id").value(orderItem2.getId()))
                .andExpect(jsonPath("$.content[0].orderItems[1].book.id").value(book2.getId()))
                .andExpect(jsonPath("$.content[0].orderItems[1].quantity").value(orderItem2.getQuantity()));
    }

    @Test
    @DisplayName("GET /orders/1 - Success")
    void getOrderById() throws Exception {
        when(orderRepository.existsById(1)).thenReturn(true);
        when(orderService.getOrderByID(1)).thenReturn(order1);

        mockMvc.perform(get("/orders/1")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order1.getId()))
                .andExpect(jsonPath("$.userID").value(order1.getUserID()))
                .andExpect(jsonPath("$.totalAmount").value(order1.getTotalAmount()))
                .andExpect(jsonPath("$.status").value(order1.getStatus().toString()))
                .andExpect(jsonPath("$.orderDate").value(order1.getOrderDate().toString()))
                .andExpect(jsonPath("$.orderItems", hasSize(2)))
                .andExpect(jsonPath("$.orderItems[0].id").value(orderItem1.getId()))
                .andExpect(jsonPath("$.orderItems[0].book.id").value(book1.getId()))
                .andExpect(jsonPath("$.orderItems[0].quantity").value(orderItem1.getQuantity()))
                .andExpect(jsonPath("$.orderItems[1].id").value(orderItem2.getId()))
                .andExpect(jsonPath("$.orderItems[1].book.id").value(book2.getId()))
                .andExpect(jsonPath("$.orderItems[1].quantity").value(orderItem2.getQuantity()));
    }

    @Test
    @DisplayName("GET /orders/999 - Order Not Found")
    void getOrderByIdNotFound() throws Exception {
        when(orderRepository.existsById(999)).thenReturn(false);

        mockMvc.perform(get("/orders/{orderID}", 999)
                        .accept("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /orders/-1 - Invalid Order ID")
    void getOrderByIdInvalid() throws Exception {
        when(orderRepository.existsById(-1)).thenReturn(false);

        mockMvc.perform(get("/orders/{orderID}", -1)
                        .accept("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /orders/1 - Success")
    void updateOrderById() throws Exception {
        OrderUpdateDTO orderUpdateDTO = new OrderUpdateDTO(Status.DELIVERED);
        OrderReadDTO updatedOrder = new OrderReadDTO(1, 1, 55.0, Status.DELIVERED, LocalDate.now(),
                List.of(orderItem1, orderItem2));

        when(orderRepository.existsById(1)).thenReturn(true);
        when(orderService.updateOrderByID(1, orderUpdateDTO)).thenReturn(Optional.of(updatedOrder));

        String orderUpdateDTOString = objectMapper.writeValueAsString(orderUpdateDTO);
        mockMvc.perform(put("/orders/1").contentType("application/json").content(orderUpdateDTOString)
                .accept("application/json")).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedOrder.getId()))
                .andExpect(jsonPath("$.userID").value(updatedOrder.getUserID()))
                .andExpect(jsonPath("$.totalAmount").value(updatedOrder.getTotalAmount()))
                .andExpect(jsonPath("$.status").value(updatedOrder.getStatus().toString()))
                .andExpect(jsonPath("$.orderDate").value(updatedOrder.getOrderDate().toString()))
                .andExpect(jsonPath("$.orderItems", hasSize(2)))
                .andExpect(jsonPath("$.orderItems[0].id").value(orderItem1.getId()))
                .andExpect(jsonPath("$.orderItems[0].book.id").value(book1.getId()))
                .andExpect(jsonPath("$.orderItems[0].quantity").value(orderItem1.getQuantity()))
                .andExpect(jsonPath("$.orderItems[1].id").value(orderItem2.getId()))
                .andExpect(jsonPath("$.orderItems[1].book.id").value(book2.getId()))
                .andExpect(jsonPath("$.orderItems[1].quantity").value(orderItem2.getQuantity()));
    }

    @Test
    @DisplayName("PUT /orders/999 - Order Not Found")
    void updateOrderByIdNotFound() throws Exception {
        OrderUpdateDTO orderUpdateDTO = new OrderUpdateDTO(Status.DELIVERED);

        when(orderRepository.existsById(999)).thenReturn(false);

        String orderUpdateDTOString = objectMapper.writeValueAsString(orderUpdateDTO);
        mockMvc.perform(put("/orders/999").contentType("application/json").content(orderUpdateDTOString)
                .accept("application/json")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /orders/-1 - Invalid Order ID")
    void updateOrderByIdInvalid() throws Exception {
        OrderUpdateDTO orderUpdateDTO = new OrderUpdateDTO(Status.DELIVERED);

        when(orderRepository.existsById(-1)).thenReturn(false);

        String orderUpdateDTOString = objectMapper.writeValueAsString(orderUpdateDTO);
        mockMvc.perform(put("/orders/-1").contentType("application/json").content(orderUpdateDTOString)
                .accept("application/json")).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /orders/me - Success No Filters")
    void getOrdersForAuthenticatedUserNoFilters() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        Page<OrderReadDTO> page = new PageImpl<>(storedOrders, pageable, storedOrders.size());

        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(orderService.getOrders(1, null, null, null, pageable)).thenReturn(page);

        mockMvc.perform(get("/orders/me").param("page", "0").param("size", "20").accept("application/json"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(order1.getId()))
                .andExpect(jsonPath("$.content[0].userID").value(order1.getUserID()))
                .andExpect(jsonPath("$.content[0].totalAmount").value(order1.getTotalAmount()))
                .andExpect(jsonPath("$.content[0].status").value(order1.getStatus().toString()))
                .andExpect(jsonPath("$.content[0].orderDate").value(order1.getOrderDate().toString()))
                .andExpect(jsonPath("$.content[0].orderItems", hasSize(2)))
                .andExpect(jsonPath("$.content[0].orderItems[0].id").value(orderItem1.getId()))
                .andExpect(jsonPath("$.content[0].orderItems[0].book.id").value(book1.getId()))
                .andExpect(jsonPath("$.content[0].orderItems[0].quantity").value(orderItem1.getQuantity()))
                .andExpect(jsonPath("$.content[0].orderItems[1].id").value(orderItem2.getId()))
                .andExpect(jsonPath("$.content[0].orderItems[1].book.id").value(book2.getId()))
                .andExpect(jsonPath("$.content[0].orderItems[1].quantity").value(orderItem2.getQuantity()));
    }

    @Test
    @DisplayName("GET /orders/me - Success With Filters")
    void getOrdersForAuthenticatedUserWithFilters() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        Page<OrderReadDTO> page = new PageImpl<>(storedOrders, pageable, storedOrders.size());

        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(orderService.getOrders(1, 50.0, 100.0, Status.PROCESSING, pageable)).thenReturn(page);

        mockMvc.perform(get("/orders/me").param("minTotalAmount", "50.0").param("maxTotalAmount", "100.0")
                .param("status", Status.PROCESSING.toString()).param("page", "0").param("size", "20")
                .accept("application/json")).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(order1.getId()))
                .andExpect(jsonPath("$.content[0].userID").value(order1.getUserID()))
                .andExpect(jsonPath("$.content[0].totalAmount").value(order1.getTotalAmount()))
                .andExpect(jsonPath("$.content[0].status").value(order1.getStatus().toString()))
                .andExpect(jsonPath("$.content[0].orderDate").value(order1.getOrderDate().toString()))
                .andExpect(jsonPath("$.content[0].orderItems", hasSize(2)))
                .andExpect(jsonPath("$.content[0].orderItems[0].id").value(orderItem1.getId()))
                .andExpect(jsonPath("$.content[0].orderItems[0].book.id").value(book1.getId()))
                .andExpect(jsonPath("$.content[0].orderItems[0].quantity").value(orderItem1.getQuantity()))
                .andExpect(jsonPath("$.content[0].orderItems[1].id").value(orderItem2.getId()))
                .andExpect(jsonPath("$.content[0].orderItems[1].book.id").value(book2.getId()))
                .andExpect(jsonPath("$.content[0].orderItems[1].quantity").value(orderItem2.getQuantity()));
    }

    @Test
    @DisplayName("POST /orders/me - Success")
    void addOrder() throws Exception {
        Book book1 = Book.builder().title("The Lord of the Rings").author("J. R. R. Tolkien").price(15.0).build();

        Book book2 = Book.builder().title("Harry Potter and the Philosopher's Stone").author("J. K. Rowling")
                .price(20.0).build();

        User testUser = new User();
        testUser.setId(1);

        Cart testCart = new Cart(1, testUser, new ArrayList<>());

        BookReadDTO bookReadDTO1 = BookReadDTO.builder().id(1).title(book1.getTitle()).author(book1.getAuthor())
                .price(book1.getPrice()).build();
        BookReadDTO bookReadDTO2 = BookReadDTO.builder().id(2).title(book2.getTitle()).author(book2.getAuthor())
                .price(book2.getPrice()).build();

        CartItem cartItem1 = new CartItem(1, testCart, book1, 2);
        CartItem cartItem2 = new CartItem(2, testCart, book2, 1);

        OrderItemReadDTO orderItem1 = new OrderItemReadDTO(4, 3, bookReadDTO1, 2);
        OrderItemReadDTO orderItem2 = new OrderItemReadDTO(5, 3, bookReadDTO2, 1);

        OrderReadDTO expectedOrder = new OrderReadDTO(3, 1, 50.0, Status.PROCESSING, LocalDate.now(),
                List.of(orderItem1, orderItem2));

        // Mock setup
        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(cartItemsRepository.findAllByUserID(1)).thenReturn(List.of(cartItem1, cartItem2));
        when(orderService.addOrderByUserID(1)).thenReturn(Optional.of(expectedOrder));

        mockMvc.perform(post("/orders/me").accept("application/json")).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedOrder.getId()))
                .andExpect(jsonPath("$.userID").value(expectedOrder.getUserID()))
                .andExpect(jsonPath("$.totalAmount").value(expectedOrder.getTotalAmount()))
                .andExpect(jsonPath("$.status").value(expectedOrder.getStatus().toString()))
                .andExpect(jsonPath("$.orderDate").value(expectedOrder.getOrderDate().toString()))
                .andExpect(jsonPath("$.orderItems", hasSize(2)))
                .andExpect(jsonPath("$.orderItems[0].id").value(orderItem1.getId()))
                .andExpect(jsonPath("$.orderItems[0].book.id").value(orderItem1.getBook().getId()))
                .andExpect(jsonPath("$.orderItems[0].quantity").value(orderItem1.getQuantity()))
                .andExpect(jsonPath("$.orderItems[1].id").value(orderItem2.getId()))
                .andExpect(jsonPath("$.orderItems[1].book.id").value(orderItem2.getBook().getId()))
                .andExpect(jsonPath("$.orderItems[1].quantity").value(orderItem2.getQuantity()));
    }

    @Test
    @DisplayName("POST /orders/me - Failure No Cart Items")
    void addOrderNoCartItems() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(cartItemsRepository.findAllByUserID(1)).thenReturn(new ArrayList<>());

        mockMvc.perform(post("/orders/me")
                        .accept("application/json"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("POST /orders/me - Error JWT")
    void addOrderErrorJWT() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenThrow(new IllegalArgumentException("JWT Error"));

        mockMvc.perform(post("/orders/me")
                        .accept("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /orders/me/1 - Success")
    void getOrderMeById() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(orderRepository.existsById(1)).thenReturn(true);
        when(orderRepository.existsByUser_IdAndId(1, 1)).thenReturn(true);
        when(orderService.getOrderByID(1)).thenReturn(order1);

        mockMvc.perform(get("/orders/me/1")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order1.getId()))
                .andExpect(jsonPath("$.userID").value(order1.getUserID()))
                .andExpect(jsonPath("$.totalAmount").value(order1.getTotalAmount()))
                .andExpect(jsonPath("$.status").value(order1.getStatus().toString()))
                .andExpect(jsonPath("$.orderDate").value(order1.getOrderDate().toString()))
                .andExpect(jsonPath("$.orderItems", hasSize(2)))
                .andExpect(jsonPath("$.orderItems[0].id").value(orderItem1.getId()))
                .andExpect(jsonPath("$.orderItems[0].book.id").value(book1.getId()))
                .andExpect(jsonPath("$.orderItems[0].quantity").value(orderItem1.getQuantity()))
                .andExpect(jsonPath("$.orderItems[1].id").value(orderItem2.getId()))
                .andExpect(jsonPath("$.orderItems[1].book.id").value(book2.getId()))
                .andExpect(jsonPath("$.orderItems[1].quantity").value(orderItem2.getQuantity()));
    }

    @Test
    @DisplayName("GET /orders/me/1 - Error JWT")
    void getOrderMeByIdErrorJWT() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenThrow(new IllegalArgumentException("JWT Error"));
        when(orderRepository.existsById(1)).thenReturn(true);

        mockMvc.perform(get("/orders/me/1")
                        .accept("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /orders/me/999 - Order Not Found")
    void getOrderMeByIdNotFound() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(orderRepository.existsById(999)).thenReturn(false);
        when(orderRepository.existsByUser_IdAndId(1, 1)).thenReturn(false);

        mockMvc.perform(get("/orders/me/999")
                        .accept("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /orders/me/-1 - Invalid Order ID")
    void getOrderMeByIdInvalid() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(orderRepository.existsById(-1)).thenReturn(false);
        when(orderRepository.existsByUser_IdAndId(1, -1)).thenReturn(false);

        mockMvc.perform(get("/orders/me/-1")
                        .accept("application/json"))
                .andExpect(status().isBadRequest());
    }
}
