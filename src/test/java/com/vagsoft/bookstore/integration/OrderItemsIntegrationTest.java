package com.vagsoft.bookstore.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vagsoft.bookstore.dto.cartDTOs.CartItemReadDTO;
import com.vagsoft.bookstore.dto.orderDTOs.OrderItemReadDTO;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ActiveProfiles("test")
public class OrderItemsIntegrationTest {

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

        cart1 = Cart.builder()
                .user(user1)
                .cartItems(new ArrayList<>())
                .build();

        cart2 = Cart.builder()
                .user(user2)
                .cartItems(new ArrayList<>())
                .build();

        cartItem1 = CartItem.builder()
                .cart(cart1)
                .book(book1)
                .quantity(2)
                .build();

        cartItem2 = CartItem.builder()
                .cart(cart1)
                .book(book2)
                .quantity(1)
                .build();

        cartItem3 = CartItem.builder()
                .cart(cart2)
                .book(book1)
                .quantity(3)
                .build();


        cart1.getCartItems().add(cartItem1);
        cart1.getCartItems().add(cartItem2);
        cart2.getCartItems().add(cartItem3);

        cartRepository.save(cart1);
        cartRepository.save(cart2);

        order1 = Order.builder()
                .user(user1)
                .orderItems(new ArrayList<>())
                .totalAmount(50.0)
                .orderDate(LocalDate.now())
                .status(Status.PROCESSING)
                .build();

        order2 = Order.builder()
                .user(user2)
                .orderItems(new ArrayList<>())
                .totalAmount(60.0)
                .orderDate(LocalDate.now())
                .status(Status.DELIVERED)
                .build();


        orderItem1 = OrderItem.builder()
                .order(order1)
                .book(book1)
                .quantity(2)
                .build();

        orderItem2 = OrderItem.builder()
                .order(order1)
                .book(book2)
                .quantity(1)
                .build();

        orderItem3 = OrderItem.builder()
                .order(order2)
                .book(book1)
                .quantity(3)
                .build();

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
    @DisplayName("GET /orders/{orderID}/items - Success")
    void getOrderItemsByOrderID() {
        URI uri = UriComponentsBuilder.fromUriString("/orders/" + order1.getId() + "/items")
                .build().encode().toUri();

        ParameterizedTypeReference<CustomPageImpl<OrderItemReadDTO>> classType = new ParameterizedTypeReference<>() {};
        ResponseEntity<CustomPageImpl<OrderItemReadDTO>> response = client.exchange(uri, HttpMethod.GET, null, classType);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        OrderItemReadDTO firstOrderItem = response.getBody().getContent().getFirst();
        assertEquals(orderItemMapper.orderItemToReadDto(orderItem1), firstOrderItem);

        OrderItemReadDTO secondOrderItem = response.getBody().getContent().getLast();
        assertEquals(orderItemMapper.orderItemToReadDto(orderItem2), secondOrderItem);
    }

    @Test
    @DisplayName("GET /orders/999/items - Order Not Found")
    void getOrderItemsByOrderIDNotFound() {
        URI uri = UriComponentsBuilder.fromUriString("/orders/999/items")
                .build().encode().toUri();

        ResponseEntity<CustomPageImpl<OrderItemReadDTO>> response = client.exchange(uri, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /orders/-1/items - Invalid Order ID")
    void getOrderItemsByOrderIDInvalid() {
        URI uri = UriComponentsBuilder.fromUriString("/orders/-1/items")
                .build().encode().toUri();

        ResponseEntity<CustomPageImpl<OrderItemReadDTO>> response = client.exchange(uri, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /orders/{orderID}/items/{bookID} - Success")
    void getOrderItemByOrderIDAndBookID() {
        URI uri = UriComponentsBuilder.fromUriString("/orders/" + order1.getId() + "/items/" + book1.getId())
                .build().encode().toUri();

        ResponseEntity<OrderItemReadDTO> response = client.getForEntity(uri, OrderItemReadDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        OrderItemReadDTO orderItemReadDTO = response.getBody();
        assertEquals(orderItemMapper.orderItemToReadDto(orderItem1), orderItemReadDTO);
    }

    @Test
    @DisplayName("GET /orders/999/items/{bookID} - Order Not Found")
    void getOrderItemByOrderIDAndBookIDNotFoundOrder() {
        URI uri = UriComponentsBuilder.fromUriString("/orders/999/items/" + book1.getId())
                .build().encode().toUri();

        ResponseEntity<OrderItemReadDTO> response = client.getForEntity(uri, OrderItemReadDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    @Test
    @DisplayName("GET /orders/-1/items/{bookID} - Invalid Order ID")
    void getOrderItemByOrderIDAndBookIDInvalidOrder() {
        URI uri = UriComponentsBuilder.fromUriString("/orders/-1/items/" + book1.getId())
                .build().encode().toUri();

        ResponseEntity<OrderItemReadDTO> response = client.getForEntity(uri, OrderItemReadDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /orders/{orderID}/items/999 - Book Not Found")
    void getOrderItemByOrderIDAndBookIDNotFoundBook() {
        URI uri = UriComponentsBuilder.fromUriString("/orders/" + order1.getId() + "/items/999")
                .build().encode().toUri();

        ResponseEntity<OrderItemReadDTO> response = client.getForEntity(uri, OrderItemReadDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /orders/{orderID}/items/-1 - Invalid Book ID")
    void getOrderItemByOrderIDAndBookIDInvalidBook() {
        URI uri = UriComponentsBuilder.fromUriString("/orders/" + order1.getId() + "/items/-1")
                .build().encode().toUri();

        ResponseEntity<OrderItemReadDTO> response = client.getForEntity(uri, OrderItemReadDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /orders/999/items/999 - Order and Book Not Found")
    void getOrderItemByOrderIDAndBookIDBothNotFound() {
        URI uri = UriComponentsBuilder.fromUriString("/orders/999/items/999")
                .build().encode().toUri();

        ResponseEntity<OrderItemReadDTO> response = client.getForEntity(uri, OrderItemReadDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /orders/-1/items/-1 - Invalid User and Book IDs")
    void getOrderItemByOrderIDAndBookIDInvalidIDs() {
        URI uri = UriComponentsBuilder.fromUriString("/orders/-1/items/-1")
                .build().encode().toUri();

        ResponseEntity<OrderItemReadDTO> response = client.getForEntity(uri, OrderItemReadDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /orders/me/{orderID}/items - Success")
    void getOrderItemsMeByOrderID() {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());
        URI uri = UriComponentsBuilder.fromUriString("/orders/me/" + order1.getId() + "/items")
                .build().encode().toUri();

        ParameterizedTypeReference<CustomPageImpl<OrderItemReadDTO>> classType = new ParameterizedTypeReference<>() {};
        ResponseEntity<CustomPageImpl<OrderItemReadDTO>> response = client.exchange(uri, HttpMethod.GET, null, classType);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        OrderItemReadDTO firstOrderItem = response.getBody().getContent().getFirst();
        assertEquals(orderItemMapper.orderItemToReadDto(orderItem1), firstOrderItem);

        OrderItemReadDTO secondOrderItem = response.getBody().getContent().getLast();
        assertEquals(orderItemMapper.orderItemToReadDto(orderItem2), secondOrderItem);
    }

    @Test
    @DisplayName("GET /orders/me/999/items - Order Not Found")
    void getOrderItemsMeByOrderIDNotFound() {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());
        URI uri = UriComponentsBuilder.fromUriString("/orders/me/999/items")
                .build().encode().toUri();

        ResponseEntity<CustomPageImpl<OrderItemReadDTO>> response = client.exchange(uri, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /orders/me/-1/items - Invalid Order ID")
    void getOrderItemsMeByOrderIDInvalid() {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());
        URI uri = UriComponentsBuilder.fromUriString("/orders/me/-1/items")
                .build().encode().toUri();

        ResponseEntity<CustomPageImpl<OrderItemReadDTO>> response = client.exchange(uri, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /orders/me/{orderID}/items/{bookID} - Success")
    void getOrderItemsMeByOrderIDAndBookID() {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());
        URI uri = UriComponentsBuilder.fromUriString("/orders/me/" + order1.getId() + "/items/" + book1.getId())
                .build().encode().toUri();

        ResponseEntity<OrderItemReadDTO> response = client.getForEntity(uri, OrderItemReadDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        OrderItemReadDTO orderItemReadDTO = response.getBody();
        assertEquals(orderItemMapper.orderItemToReadDto(orderItem1), orderItemReadDTO);
    }

    @Test
    @DisplayName("GET /orders/me/{orderID}/items/{bookID} - Error JWT")
    void getOrderItemsMeByOrderIDAndBookIDErrorJWT() {
        when(authUtils.getUserIdFromAuthentication()).thenThrow(new IllegalArgumentException("Invalid JWT"));

        URI uri = UriComponentsBuilder.fromUriString("/orders/me/" + order1.getId() + "/items/" + book1.getId())
                .build().encode().toUri();
        ResponseEntity<OrderItemReadDTO> response = client.getForEntity(uri, OrderItemReadDTO.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /orders/me/999/items/{bookID} - Order Not Found")
    void getOrderItemsMeByOrderIDAndBookIDNotFoundOrder() {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());
        URI uri = UriComponentsBuilder.fromUriString("/orders/me/999/items/" + book1.getId())
                .build().encode().toUri();

        ResponseEntity<OrderItemReadDTO> response = client.getForEntity(uri, OrderItemReadDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /orders/me/-1/items/{bookID} - Invalid Order ID")
    void getOrderItemsMeByOrderIDAndBookIDInvalidOrder() {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());
        URI uri = UriComponentsBuilder.fromUriString("/orders/me/-1/items/" + book1.getId())
                .build().encode().toUri();

        ResponseEntity<OrderItemReadDTO> response = client.getForEntity(uri, OrderItemReadDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /orders/me/{orderID}/items/999 - Book Not Found")
    void getOrderItemsMeByOrderIDAndBookIDNotFoundBook() {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());
        URI uri = UriComponentsBuilder.fromUriString("/orders/me/" + order1.getId() + "/items/999")
                .build().encode().toUri();

        ResponseEntity<OrderItemReadDTO> response = client.getForEntity(uri, OrderItemReadDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /orders/me/{orderID}/items/-1 - Invalid Book ID")
    void getOrderItemsMeByOrderIDAndBookIDInvalidBook() {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());
        URI uri = UriComponentsBuilder.fromUriString("/orders/me/" + order1.getId() + "/items/-1")
                .build().encode().toUri();

        ResponseEntity<OrderItemReadDTO> response = client.getForEntity(uri, OrderItemReadDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}
