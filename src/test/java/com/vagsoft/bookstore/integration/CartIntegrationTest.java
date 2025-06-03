package com.vagsoft.bookstore.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vagsoft.bookstore.dto.cartDTOs.CartItemReadDTO;
import com.vagsoft.bookstore.dto.cartDTOs.CartReadDTO;
import com.vagsoft.bookstore.dto.favouriteDTOs.FavouriteReadDTO;
import com.vagsoft.bookstore.dto.userDTOs.UserReadDTO;
import com.vagsoft.bookstore.mappers.CartMapper;
import com.vagsoft.bookstore.mappers.FavouriteMapper;
import com.vagsoft.bookstore.models.entities.Book;
import com.vagsoft.bookstore.models.entities.Cart;
import com.vagsoft.bookstore.models.entities.CartItem;
import com.vagsoft.bookstore.models.entities.User;
import com.vagsoft.bookstore.models.enums.Role;
import com.vagsoft.bookstore.pagination.CustomPageImpl;
import com.vagsoft.bookstore.repositories.BookRepository;
import com.vagsoft.bookstore.repositories.CartItemsRepository;
import com.vagsoft.bookstore.repositories.CartRepository;
import com.vagsoft.bookstore.repositories.FavouriteRepository;
import com.vagsoft.bookstore.repositories.UserRepository;
import com.vagsoft.bookstore.services.FavouriteService;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ActiveProfiles("test")
public class CartIntegrationTest {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemsRepository cartItemsRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private TestRestTemplate client;
    @MockitoBean
    private AuthUtils authUtils;


    Book book1, book2;
    User user1, user2;
    Cart cart1, cart2;
    CartItem cartItem1, cartItem2, cartItem3;

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
    }

    @AfterEach
    public void tearDown() {
        cartItemsRepository.deleteAll();

        cartRepository.deleteAll();

        bookRepository.deleteAll();

        userRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /carts - Success")
    void getAllCarts() {
        URI uri = UriComponentsBuilder.fromUriString("/carts")
                .build().encode().toUri();

        ParameterizedTypeReference<CustomPageImpl<CartReadDTO>> classType = new ParameterizedTypeReference<>() {};
        ResponseEntity<CustomPageImpl<CartReadDTO>> response = client.exchange(uri, HttpMethod.GET, null, classType);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        CartReadDTO firstCart = response.getBody().getContent().getFirst();
        assertEquals(cartMapper.cartToReadDto(cart1), firstCart);

        CartReadDTO secondCart = response.getBody().getContent().getLast();
        assertEquals(cartMapper.cartToReadDto(cart2), secondCart);
    }

    @Test
    @DisplayName("GET /carts/{userID} - Success")
    void getCartByUserID() {
        ResponseEntity<CartReadDTO> response = client.getForEntity("/carts/" + user1.getId(), CartReadDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        CartReadDTO firstCart = response.getBody();
        assertEquals(cartMapper.cartToReadDto(cart1), firstCart);
    }

    @Test
    @DisplayName("GET /carts/999 - Not Found")
    void getCartByUserIDNotFound() {
        ResponseEntity<CartReadDTO> response = client.getForEntity("/carts/999", CartReadDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /carts/-1 - Invalid ID")
    void getCartByUserIDInvalid() {
        ResponseEntity<CartReadDTO> response = client.getForEntity("/carts/-1", CartReadDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /carts/me - Success")
    void getCartMe() {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());
        ResponseEntity<CartReadDTO> response = client.getForEntity("/carts/me", CartReadDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        CartReadDTO firstCart = response.getBody();
        assertEquals(cartMapper.cartToReadDto(cart1), firstCart);
    }

    @Test
    @DisplayName("GET /carts/me - Error JWT")
    void getCartMeError() {
        when(authUtils.getUserIdFromAuthentication())
                .thenThrow(new IllegalArgumentException("Invalid Jwt token"));
        ResponseEntity<CartReadDTO> response = client.getForEntity("/carts/me", CartReadDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
