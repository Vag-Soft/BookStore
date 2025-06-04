package com.vagsoft.bookstore.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vagsoft.bookstore.dto.orderDTOs.OrderReadDTO;
import com.vagsoft.bookstore.dto.orderDTOs.OrderUpdateDTO;
import com.vagsoft.bookstore.mappers.OrderItemMapper;
import com.vagsoft.bookstore.mappers.OrderMapper;
import com.vagsoft.bookstore.models.entities.Book;
import com.vagsoft.bookstore.models.entities.Cart;
import com.vagsoft.bookstore.models.entities.CartItem;
import com.vagsoft.bookstore.models.entities.Order;
import com.vagsoft.bookstore.models.entities.OrderItem;
import com.vagsoft.bookstore.models.entities.User;
import com.vagsoft.bookstore.models.enums.Role;
import com.vagsoft.bookstore.models.enums.Status;
import com.vagsoft.bookstore.pagination.CustomPageImpl;
import com.vagsoft.bookstore.repositories.BookRepository;
import com.vagsoft.bookstore.repositories.CartItemsRepository;
import com.vagsoft.bookstore.repositories.CartRepository;
import com.vagsoft.bookstore.repositories.OrderItemsRepository;
import com.vagsoft.bookstore.repositories.OrderRepository;
import com.vagsoft.bookstore.repositories.UserRepository;
import com.vagsoft.bookstore.utils.AuthUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ActiveProfiles("test")
public class OrderIntegrationTest {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemsRepository orderItemsRepository;
    @Autowired
    private CartItemsRepository cartItemsRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private TestRestTemplate client;
    @MockitoBean
    private AuthUtils authUtils;

    Book book1, book2;
    User user1, user2;
    Cart cart1, cart2;
    CartItem cartItem1, cartItem2, cartItem3;
    Order order1, order2;
    OrderItem orderItem1, orderItem2, orderItem3;
    @BeforeEach
    public void setUp() {
        book1 = Book.builder().title("The Lord of the Rings").author("J. R. R. Tolkien").description(
                "The Lord of the Rings is a series of three fantasy novels written by English author and scholar J. R. R. Tolkien.")
                .pages(1178).price(15.0).availability(5).isbn("978-0-395-36381-0").genres(new ArrayList<>()).build();

        book2 = Book.builder().title("Harry Potter and the Philosopher's Stone").author("J. K. Rowling").description(
                "Harry Potter and the Philosopher's Stone is a fantasy novel written by British author J. K. Rowling.")
                .pages(223).price(20.0).availability(10).isbn("978-0-7-152-20664-5").genres(new ArrayList<>()).build();

        book1 = bookRepository.save(book1);
        book2 = bookRepository.save(book2);

        user1 = new User("jane.smith@example.com", "janesmith", "hashed_password_value", Role.USER, "Jane", "Smith",
                LocalDate.parse("2022-01-05"));
        user2 = new User("bob.johnson@example.com", "bobjohnson", "hashed_password_value", Role.ADMIN, "Bob", "Johnson",
                LocalDate.parse("2022-01-10"));

        userRepository.save(user1);
        userRepository.save(user2);

        cart1 = Cart.builder().user(user1).cartItems(new ArrayList<>()).build();

        cart2 = Cart.builder().user(user2).cartItems(new ArrayList<>()).build();

        cartItem1 = CartItem.builder().cart(cart1).book(book1).quantity(2).build();

        cartItem2 = CartItem.builder().cart(cart1).book(book2).quantity(1).build();

        cartItem3 = CartItem.builder().cart(cart2).book(book1).quantity(3).build();

        cart1.getCartItems().add(cartItem1);
        cart1.getCartItems().add(cartItem2);
        cart2.getCartItems().add(cartItem3);

        cartRepository.save(cart1);
        cartRepository.save(cart2);

        order1 = Order.builder().user(user1).orderItems(new ArrayList<>()).totalAmount(50.0).orderDate(LocalDate.now())
                .status(Status.PROCESSING).build();

        order2 = Order.builder().user(user2).orderItems(new ArrayList<>()).totalAmount(60.0).orderDate(LocalDate.now())
                .status(Status.DELIVERED).build();

        orderItem1 = OrderItem.builder().order(order1).book(book1).quantity(2).build();

        orderItem2 = OrderItem.builder().order(order1).book(book2).quantity(1).build();

        orderItem3 = OrderItem.builder().order(order2).book(book1).quantity(3).build();

        order1.getOrderItems().add(orderItem1);
        order1.getOrderItems().add(orderItem2);
        order2.getOrderItems().add(orderItem3);

        orderRepository.save(order1);
        orderRepository.save(order2);
    }

    @AfterEach
    public void tearDown() {
        orderItemsRepository.deleteAll();

        orderRepository.deleteAll();

        cartItemsRepository.deleteAll();

        cartRepository.deleteAll();

        bookRepository.deleteAll();

        userRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /orders - Success No Filters")
    void getAllOrdersNoFilters() {
        URI uri = UriComponentsBuilder.fromUriString("/orders").build().encode().toUri();

        ParameterizedTypeReference<CustomPageImpl<OrderReadDTO>> classType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<CustomPageImpl<OrderReadDTO>> response = client.exchange(uri, HttpMethod.GET, null, classType);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        OrderReadDTO firstOrder = response.getBody().getContent().getFirst();
        assertEquals(orderMapper.orderToReadDto(order1), firstOrder);

        OrderReadDTO secondCart = response.getBody().getContent().getLast();
        assertEquals(orderMapper.orderToReadDto(order2), secondCart);
    }

    @Test
    @DisplayName("GET /orders - Success With Filters")
    void getAllOrdersWithFilters() {
        URI uri = UriComponentsBuilder.fromUriString("/orders").queryParam("status", "PROCESSING").build().encode()
                .toUri();

        ParameterizedTypeReference<CustomPageImpl<OrderReadDTO>> classType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<CustomPageImpl<OrderReadDTO>> response = client.exchange(uri, HttpMethod.GET, null, classType);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        OrderReadDTO firstOrder = response.getBody().getContent().getFirst();
        assertEquals(orderMapper.orderToReadDto(order1), firstOrder);
    }

    @Test
    @DisplayName("GET /orders/{orderID} - Success")
    void getOrderById() {
        ResponseEntity<OrderReadDTO> response = client.getForEntity("/orders/" + order1.getId(), OrderReadDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        OrderReadDTO firstOrder = response.getBody();
        assertEquals(orderMapper.orderToReadDto(order1), firstOrder);
    }

    @Test
    @DisplayName("GET /orders/999 - Not Found")
    void getOrderByIdNotFound() {
        ResponseEntity<ProblemDetail> response = client.getForEntity("/orders/999", ProblemDetail.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /orders/-1 - Invalid ID")
    void getOrderByIdInvalid() {
        ResponseEntity<ProblemDetail> response = client.getForEntity("/orders/-1", ProblemDetail.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("PUT /orders/{orderID} - Success")
    void putOrderById() throws Exception {
        OrderUpdateDTO orderUpdate = OrderUpdateDTO.builder().status(Status.DELIVERED).build();

        String orderUpdateString = objectMapper.writeValueAsString(orderUpdate);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(orderUpdateString, headers);

        ResponseEntity<OrderReadDTO> response = client.exchange("/orders/" + order1.getId(), HttpMethod.PUT, request,
                OrderReadDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        OrderReadDTO updatedOrder = response.getBody();
        assertEquals(Status.DELIVERED, updatedOrder.getStatus());
        assertEquals(order1.getId(), updatedOrder.getId());
        assertEquals(order1.getUser().getId(), updatedOrder.getUserID());
        assertEquals(order1.getTotalAmount(), updatedOrder.getTotalAmount());
        assertEquals(order1.getOrderItems().size(), updatedOrder.getOrderItems().size());
    }

    @Test
    @DisplayName("PUT /orders/999 - Not Found")
    void putOrderByIdNotFound() throws Exception {
        OrderUpdateDTO orderUpdate = OrderUpdateDTO.builder().status(Status.DELIVERED).build();

        String orderUpdateString = objectMapper.writeValueAsString(orderUpdate);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(orderUpdateString, headers);

        ResponseEntity<ProblemDetail> response = client.exchange("/orders/999", HttpMethod.PUT, request,
                ProblemDetail.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("PUT /orders/-1 - Invalid ID")
    void putOrderByIdInvalid() throws Exception {
        OrderUpdateDTO orderUpdate = OrderUpdateDTO.builder().status(Status.DELIVERED).build();

        String orderUpdateString = objectMapper.writeValueAsString(orderUpdate);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(orderUpdateString, headers);

        ResponseEntity<ProblemDetail> response = client.exchange("/orders/-1", HttpMethod.PUT, request,
                ProblemDetail.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /orders/me - Success No Filters")
    void getOrdersMeNoFilters() {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());
        URI uri = UriComponentsBuilder.fromUriString("/orders")
                .queryParam("status", "PROCESSING")
                .build().encode().toUri();

        ParameterizedTypeReference<CustomPageImpl<OrderReadDTO>> classType = new ParameterizedTypeReference<>() {};
        ResponseEntity<CustomPageImpl<OrderReadDTO>> response = client.exchange(uri, HttpMethod.GET, null, classType);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        OrderReadDTO firstOrder = response.getBody().getContent().getFirst();
        assertEquals(orderMapper.orderToReadDto(order1), firstOrder);
    }

    @Test
    @DisplayName("GET /orders/me - Success With Filters")
    void getOrdersMeWithFilters() {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());
        URI uri = UriComponentsBuilder.fromUriString("/orders")
                .queryParam("status", "PROCESSING")
                .build().encode().toUri();

        ParameterizedTypeReference<CustomPageImpl<OrderReadDTO>> classType = new ParameterizedTypeReference<>() {};
        ResponseEntity<CustomPageImpl<OrderReadDTO>> response = client.exchange(uri, HttpMethod.GET, null, classType);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        OrderReadDTO firstOrder = response.getBody().getContent().getFirst();
        assertEquals(orderMapper.orderToReadDto(order1), firstOrder);
    }

    @Test
    @DisplayName("GET /orders/me - Error JWT")
    void getOrdersMeErrorJWT() {
        when(authUtils.getUserIdFromAuthentication())
                .thenThrow(new IllegalArgumentException("Invalid Jwt token"));
        ResponseEntity<ProblemDetail> response = client.getForEntity("/orders/me", ProblemDetail.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("POST /orders/me - Success")
    void postOrderMe() {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());
        ResponseEntity<OrderReadDTO> response = client.postForEntity("/orders/me", null, OrderReadDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        assertNotNull(response.getBody());

        OrderReadDTO createdOrder = response.getBody();
        assertEquals(order2.getId()+1, createdOrder.getId());
        assertEquals(user1.getId(), createdOrder.getUserID());
        assertEquals(Status.PROCESSING, createdOrder.getStatus());
        assertEquals(50.0, createdOrder.getTotalAmount());
        assertEquals(2, createdOrder.getOrderItems().size());
        OrderItem firstOrderItem = orderItemMapper.cartItemToOrderItem(cartItem1);
        firstOrderItem.setOrder(orderMapper.readDtoToOrder(createdOrder));
        firstOrderItem.setId(orderItem3.getId()+1);
        assertEquals(orderItemMapper.orderItemToReadDto(firstOrderItem), createdOrder.getOrderItems().getFirst());

        assertTrue(orderRepository.existsById(createdOrder.getId()));
        assertTrue(orderItemsRepository.existsById(createdOrder.getOrderItems().getFirst().getId()));
        assertFalse(cartItemsRepository.existsById(cartItem1.getId()));
    }

    @Test
    @DisplayName("POST /orders/me - Empty Cart")
    void postOrderMeEmptyCart() {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());
        // First order successfully created and emptying the cart
        ResponseEntity<OrderReadDTO> response1 = client.postForEntity("/orders/me", null, OrderReadDTO.class);

        assertEquals(HttpStatus.CREATED, response1.getStatusCode());
        // Second order should fail due to empty cart
        ResponseEntity<ProblemDetail> response2 = client.postForEntity("/orders/me", null, ProblemDetail.class);

        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
    }

    @Test
    @DisplayName("POST /orders/me - Error JWT")
    void postOrderMeErrorJWT() {
        when(authUtils.getUserIdFromAuthentication())
                .thenThrow(new IllegalArgumentException("Invalid Jwt token"));
        ResponseEntity<ProblemDetail> response = client.postForEntity("/orders/me", null, ProblemDetail.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /orders/me/{orderID} - Success")
    void getOrderMeById() {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());
        ResponseEntity<OrderReadDTO> response = client.getForEntity("/orders/me/" + order1.getId(), OrderReadDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        OrderReadDTO firstOrder = response.getBody();
        assertEquals(orderMapper.orderToReadDto(order1), firstOrder);
    }

    @Test
    @DisplayName("GET /orders/me/999 - Not Found")
    void getOrderMeByIdNotFound() {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());
        ResponseEntity<ProblemDetail> response = client.getForEntity("/orders/me/999", ProblemDetail.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /orders/me/-1 - Invalid ID")
    void getOrderMeByIdInvalid() {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());
        ResponseEntity<ProblemDetail> response = client.getForEntity("/orders/me/-1", ProblemDetail.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
