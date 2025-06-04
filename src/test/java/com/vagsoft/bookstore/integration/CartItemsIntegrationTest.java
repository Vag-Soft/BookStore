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
import com.vagsoft.bookstore.dto.cartDTOs.CartItemReadDTO;
import com.vagsoft.bookstore.dto.cartDTOs.CartItemUpdateDTO;
import com.vagsoft.bookstore.dto.cartDTOs.CartItemWriteDTO;
import com.vagsoft.bookstore.mappers.CartItemMapper;
import com.vagsoft.bookstore.models.entities.Book;
import com.vagsoft.bookstore.models.entities.Cart;
import com.vagsoft.bookstore.models.entities.CartItem;
import com.vagsoft.bookstore.models.entities.User;
import com.vagsoft.bookstore.models.enums.Role;
import com.vagsoft.bookstore.pagination.CustomPageImpl;
import com.vagsoft.bookstore.repositories.BookRepository;
import com.vagsoft.bookstore.repositories.CartItemsRepository;
import com.vagsoft.bookstore.repositories.CartRepository;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ActiveProfiles("test")
public class CartItemsIntegrationTest {
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
    private CartItemMapper cartItemMapper;
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
    }

    @AfterEach
    public void tearDown() {
        cartItemsRepository.deleteAll();

        cartRepository.deleteAll();

        bookRepository.deleteAll();

        userRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /carts/{userID}/items - Success")
    public void getCartItemsByUserId() {
        URI uri = UriComponentsBuilder.fromUriString("/carts/" + user1.getId() + "/items").build().encode().toUri();

        ParameterizedTypeReference<CustomPageImpl<CartItemReadDTO>> classType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<CustomPageImpl<CartItemReadDTO>> response = client.exchange(uri, HttpMethod.GET, null,
                classType);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        CartItemReadDTO firstCartItem = response.getBody().getContent().getFirst();
        assertEquals(cartItemMapper.cartItemToReadDto(cartItem1), firstCartItem);

        CartItemReadDTO secondCartItem = response.getBody().getContent().getLast();
        assertEquals(cartItemMapper.cartItemToReadDto(cartItem2), secondCartItem);
    }

    @Test
    @DisplayName("GET /carts/999/items - Not Found User")
    public void getCartItemsByUserIdNotFound() {
        URI uri = UriComponentsBuilder.fromUriString("/carts/999/items").build().encode().toUri();

        ParameterizedTypeReference<CustomPageImpl<CartItemReadDTO>> classType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<CustomPageImpl<CartItemReadDTO>> response = client.exchange(uri, HttpMethod.GET, null,
                classType);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /carts/-1/items - Invalid User ID")
    public void getCartItemsByUserIdInvalid() {
        URI uri = UriComponentsBuilder.fromUriString("/carts/-1/items").build().encode().toUri();

        ParameterizedTypeReference<CustomPageImpl<CartItemReadDTO>> classType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<CustomPageImpl<CartItemReadDTO>> response = client.exchange(uri, HttpMethod.GET, null,
                classType);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /carts/{userID}/items/{bookID} - Success")
    public void getCartItemsByUserIdAndBookID() {
        ResponseEntity<CartItemReadDTO> response = client
                .getForEntity("/carts/" + user1.getId() + "/items/" + book1.getId(), CartItemReadDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        CartItemReadDTO firstCartItem = response.getBody();
        assertEquals(cartItemMapper.cartItemToReadDto(cartItem1), firstCartItem);
    }

    @Test
    @DisplayName("GET /carts/999/items/{bookID} - Not Found User")
    public void getCartItemsByUserIdAndBookIDNotFoundUser() {
        ResponseEntity<CartItemReadDTO> response = client.getForEntity("/carts/999/items/" + book1.getId(),
                CartItemReadDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /carts/-1/items/{bookID} - Invalid User ID")
    public void getCartItemsByUserIdAndBookIDInvalidUser() {
        ResponseEntity<CartItemReadDTO> response = client.getForEntity("/carts/-1/items/" + book1.getId(),
                CartItemReadDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /carts/{userID}/items/999 - Not Found Book")
    public void getCartItemsByUserIdAndBookIDNotFoundBook() {
        ResponseEntity<CartItemReadDTO> response = client.getForEntity("/carts/" + user1.getId() + "/items/999",
                CartItemReadDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /carts/-1/items/{bookID} - Invalid Book ID")
    public void getCartItemsByUserIdAndBookIDInvalidBook() {
        ResponseEntity<CartItemReadDTO> response = client.getForEntity("/carts/" + user1.getId() + "/items/-1",
                CartItemReadDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /carts/999/items/999 - User and Book Not Found")
    public void getCartItemsByUserIdAndBookIDNotFoundBoth() {
        ResponseEntity<CartItemReadDTO> response = client.getForEntity("/carts/999/items/999", CartItemReadDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /carts/-1/items/-1 - Invalid User and Book ID")
    public void getCartItemsByUserIdAndBookIDInvalidBoth() {
        ResponseEntity<CartItemReadDTO> response = client.getForEntity("/carts/-1/items/-1", CartItemReadDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("PUT /carts/{userID}/items/{bookID} - Success")
    public void updateCartItemByUserIdAndBookID() throws Exception {
        CartItemUpdateDTO cartItemUpdateDTO = new CartItemUpdateDTO(5);
        URI uri = UriComponentsBuilder.fromUriString("/carts/" + user1.getId() + "/items/" + book1.getId()).build()
                .encode().toUri();

        String updateCartItemString = objectMapper.writeValueAsString(cartItemUpdateDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(updateCartItemString, headers);

        ResponseEntity<CartItemReadDTO> response = client.exchange(uri, HttpMethod.PUT, request, CartItemReadDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        book1.setAvailability(book1.getAvailability() - 5);
        CartItem updatedCartItem = CartItem.builder().cart(cart1).book(book1).quantity(5).build();
        CartItemReadDTO updatedCartItemDto = response.getBody();
        assertEquals(cartItemMapper.cartItemToReadDto(updatedCartItem), updatedCartItemDto);
    }

    @Test
    @DisplayName("PUT /carts/999/items/{bookID} - User Not Found")
    public void updateCartItemByUserIdAndBookIDNotFoundUser() throws Exception {
        CartItemUpdateDTO cartItemUpdateDTO = new CartItemUpdateDTO(5);
        URI uri = UriComponentsBuilder.fromUriString("/carts/" + 999 + "/items/" + book1.getId()).build().encode()
                .toUri();

        String updateCartItemString = objectMapper.writeValueAsString(cartItemUpdateDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(updateCartItemString, headers);

        ResponseEntity<CartItemReadDTO> response = client.exchange(uri, HttpMethod.PUT, request, CartItemReadDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("PUT /carts/999/items/{bookID} - Invalid User ID")
    public void updateCartItemByUserIdAndBookIDInvalidUser() throws Exception {
        CartItemUpdateDTO cartItemUpdateDTO = new CartItemUpdateDTO(5);
        URI uri = UriComponentsBuilder.fromUriString("/carts/" + -1 + "/items/" + book1.getId()).build().encode()
                .toUri();

        String updateCartItemString = objectMapper.writeValueAsString(cartItemUpdateDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(updateCartItemString, headers);

        ResponseEntity<CartItemReadDTO> response = client.exchange(uri, HttpMethod.PUT, request, CartItemReadDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("PUT /carts/{userID}/items/999 - Book Not Found")
    public void updateCartItemByUserIdAndBookIDNotFoundBook() throws Exception {
        CartItemUpdateDTO cartItemUpdateDTO = new CartItemUpdateDTO(5);
        URI uri = UriComponentsBuilder.fromUriString("/carts/" + user1.getId() + "/items/" + 999).build().encode()
                .toUri();

        String updateCartItemString = objectMapper.writeValueAsString(cartItemUpdateDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(updateCartItemString, headers);

        ResponseEntity<CartItemReadDTO> response = client.exchange(uri, HttpMethod.PUT, request, CartItemReadDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("PUT /carts/{userID}/items/-1 - Invalid Book ID")
    public void updateCartItemByUserIdAndBookIDInvalidBook() throws Exception {
        CartItemUpdateDTO cartItemUpdateDTO = new CartItemUpdateDTO(5);
        URI uri = UriComponentsBuilder.fromUriString("/carts/" + user1.getId() + "/items/" + -1).build().encode()
                .toUri();

        String updateCartItemString = objectMapper.writeValueAsString(cartItemUpdateDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(updateCartItemString, headers);

        ResponseEntity<CartItemReadDTO> response = client.exchange(uri, HttpMethod.PUT, request, CartItemReadDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("PUT /carts/999/items/999 - User and Book Not Found")
    public void updateCartItemByUserIdAndBookIDNotFoundBoth() throws Exception {
        CartItemUpdateDTO cartItemUpdateDTO = new CartItemUpdateDTO(5);
        URI uri = UriComponentsBuilder.fromUriString("/carts/" + 999 + "/items/" + 999).build().encode().toUri();

        String updateCartItemString = objectMapper.writeValueAsString(cartItemUpdateDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(updateCartItemString, headers);

        ResponseEntity<CartItemReadDTO> response = client.exchange(uri, HttpMethod.PUT, request, CartItemReadDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("PUT /carts/-1/items/-1 - Invalid User and Book IDs")
    public void updateCartItemByUserIdAndBookIDInvalidBoth() throws Exception {
        CartItemUpdateDTO cartItemUpdateDTO = new CartItemUpdateDTO(5);
        URI uri = UriComponentsBuilder.fromUriString("/carts/" + -1 + "/items/" + -1).build().encode().toUri();

        String updateCartItemString = objectMapper.writeValueAsString(cartItemUpdateDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(updateCartItemString, headers);

        ResponseEntity<CartItemReadDTO> response = client.exchange(uri, HttpMethod.PUT, request, CartItemReadDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /carts/{userID}/items/{bookID} - Success")
    public void deleteCartItemByUserIdAndBookID() {
        URI uri = UriComponentsBuilder.fromUriString("/carts/" + user1.getId() + "/items/" + book1.getId()).build()
                .encode().toUri();

        ResponseEntity<Void> response = client.exchange(uri, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        assertFalse(cartItemsRepository.existsByUserIDAndBookID(user1.getId(), book1.getId()));
    }

    @Test
    @DisplayName("DELETE /carts/999/items/{bookID} - Not Found User")
    public void deleteCartItemByUserIdAndBookIDNotFoundUser() {
        URI uri = UriComponentsBuilder.fromUriString("/carts/999/items/" + book1.getId()).build().encode().toUri();

        ResponseEntity<Void> response = client.exchange(uri, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        assertFalse(cartItemsRepository.existsByUserIDAndBookID(999, book1.getId()));
    }

    @Test
    @DisplayName("DELETE /carts/-1/items/{bookID} - Invalid User ID")
    public void deleteCartItemByUserIdAndBookIDInvalidUser() {
        URI uri = UriComponentsBuilder.fromUriString("/carts/-1/items/" + book1.getId()).build().encode().toUri();

        ResponseEntity<Void> response = client.exchange(uri, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        assertFalse(cartItemsRepository.existsByUserIDAndBookID(-1, book1.getId()));
    }

    @Test
    @DisplayName("DELETE /carts/{userID}/items/999 - Book Not Found")
    public void deleteCartItemByUserIdAndBookIDNotFoundBook() {
        URI uri = UriComponentsBuilder.fromUriString("/carts/" + user1.getId() + "/items/999").build().encode().toUri();

        ResponseEntity<Void> response = client.exchange(uri, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        assertFalse(cartItemsRepository.existsByUserIDAndBookID(user1.getId(), 999));
    }

    @Test
    @DisplayName("DELETE /carts/{userID}/items/-1 - Invalid Book ID")
    public void deleteCartItemByUserIdAndBookIDInvalidBook() {
        URI uri = UriComponentsBuilder.fromUriString("/carts/" + user1.getId() + "/items/-1").build().encode().toUri();

        ResponseEntity<Void> response = client.exchange(uri, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        assertFalse(cartItemsRepository.existsByUserIDAndBookID(user1.getId(), -1));
    }

    @Test
    @DisplayName("DELETE /carts/999/items/999 - User and Book Not Found")
    public void deleteCartItemByUserIdAndBookIDNotFoundBoth() {
        URI uri = UriComponentsBuilder.fromUriString("/carts/999/items/999").build().encode().toUri();

        ResponseEntity<Void> response = client.exchange(uri, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        assertFalse(cartItemsRepository.existsByUserIDAndBookID(999, 999));
    }

    @Test
    @DisplayName("DELETE /carts/-1/items/-1 - Invalid User and Book IDs")
    public void deleteCartItemByUserIdAndBookIDInvalidBoth() {
        URI uri = UriComponentsBuilder.fromUriString("/carts/-1/items/-1").build().encode().toUri();

        ResponseEntity<Void> response = client.exchange(uri, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        assertFalse(cartItemsRepository.existsByUserIDAndBookID(-1, -1));
    }

    @Test
    @DisplayName("GET /carts/me/items - Success")
    public void getCartItemsMe() {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());

        URI uri = UriComponentsBuilder.fromUriString("/carts/me/items")
                .build().encode().toUri();

        ParameterizedTypeReference<CustomPageImpl<CartItemReadDTO>> classType = new ParameterizedTypeReference<>() {};
        ResponseEntity<CustomPageImpl<CartItemReadDTO>> response = client.exchange(uri, HttpMethod.GET, null, classType);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        CartItemReadDTO firstCartItem = response.getBody().getContent().getFirst();
        assertEquals(cartItemMapper.cartItemToReadDto(cartItem1), firstCartItem);

        CartItemReadDTO secondCartItem = response.getBody().getContent().getLast();
        assertEquals(cartItemMapper.cartItemToReadDto(cartItem2), secondCartItem);
    }

    @Test
    @DisplayName("GET /carts/me/items - Error JWT")
    public void getCartItemsMeError() {
        when(authUtils.getUserIdFromAuthentication())
                .thenThrow(new IllegalArgumentException("Invalid Jwt token"));

        URI uri = UriComponentsBuilder.fromUriString("/carts/me/items")
                .build().encode().toUri();

        ParameterizedTypeReference<CustomPageImpl<CartItemReadDTO>> classType = new ParameterizedTypeReference<>() {};
        ResponseEntity<CustomPageImpl<CartItemReadDTO>> response = client.exchange(uri, HttpMethod.GET, null, classType);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("POST /carts/me/items - Success")
    public void addCartItemMe() {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user2.getId());

        CartItemWriteDTO cartItemWriteDTO = new CartItemWriteDTO(book2.getId(), 2);
        ResponseEntity<CartItemReadDTO> response = client.postForEntity("/carts/me/items", cartItemWriteDTO, CartItemReadDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        book2.setAvailability(book2.getAvailability() - 2);
        CartItem cartItem = CartItem.builder()
                .cart(cart2)
                .book(book2)
                .quantity(2)
                .build();
        CartItemReadDTO addedCartItem = response.getBody();
        assertEquals(cartItemMapper.cartItemToReadDto(cartItem), addedCartItem);

        assertTrue(cartItemsRepository.existsByUserIDAndBookID(user2.getId(), book2.getId()));
    }

    @Test
    @DisplayName("POST /carts/me/items - Book Not Found")
    public void addCartItemMeNotFound() {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user2.getId());

        CartItemWriteDTO cartItemWriteDTO = new CartItemWriteDTO(999, 2);
        ResponseEntity<CartItemReadDTO> response = client.postForEntity("/carts/me/items", cartItemWriteDTO, CartItemReadDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        assertFalse(cartItemsRepository.existsByUserIDAndBookID(user2.getId(), 999));
    }

    @Test
    @DisplayName("POST /carts/me/items - Invalid Book ID")
    public void addCartItemMeInvalid() {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user2.getId());

        CartItemWriteDTO cartItemWriteDTO = new CartItemWriteDTO(-1, 2);
        ResponseEntity<CartItemReadDTO> response = client.postForEntity("/carts/me/items", cartItemWriteDTO, CartItemReadDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        assertFalse(cartItemsRepository.existsByUserIDAndBookID(user2.getId(), -1));
    }

    @Test
    @DisplayName("POST /carts/me/items - Error JWT")
    public void addCartItemMeErrorJWT() {
        CartItemWriteDTO cartItemWriteDTO = new CartItemWriteDTO(book2.getId(), 2);

        when(authUtils.getUserIdFromAuthentication()).thenThrow(new IllegalArgumentException("Invalid Jwt token"));
        ResponseEntity<CartItemReadDTO> response = client.postForEntity("/carts/me/items", cartItemWriteDTO,
                CartItemReadDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        assertFalse(cartItemsRepository.existsByUserIDAndBookID(user2.getId(), book2.getId()));
    }

    @Test
    @DisplayName("GET /carts/me/items/{bookID} - Success")
    public void getCartItemsMeAndBookID() {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());
        ResponseEntity<CartItemReadDTO> response = client.getForEntity("/carts/me/items/" + book1.getId(), CartItemReadDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        CartItemReadDTO firstCartItem = response.getBody();
        assertEquals(cartItemMapper.cartItemToReadDto(cartItem1), firstCartItem);
    }

    @Test
    @DisplayName("GET /carts/me/items/{bookID} - Error JWT")
    public void getCartItemsMeAndBookIDErrorJWT() {
        when(authUtils.getUserIdFromAuthentication())
                .thenThrow(new IllegalArgumentException("Invalid Jwt token"));

        ResponseEntity<CartItemReadDTO> response = client.getForEntity("/carts/me/items/" + book1.getId(), CartItemReadDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /carts/me/items/999 - Not Found Book")
    public void getCartItemsMeAndBookIDNotFoundBook() {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());
        ResponseEntity<CartItemReadDTO> response = client.getForEntity("/carts/me/items/999", CartItemReadDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /carts/me/items/{bookID} - Invalid Book ID")
    public void getCartItemsMeAndBookIDInvalidBook() {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());
        ResponseEntity<CartItemReadDTO> response = client.getForEntity("/carts/me/items/-1", CartItemReadDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("PUT /carts/me/items/{bookID} - Success")
    public void updateCartItemMeAndBookID() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());
        CartItemUpdateDTO cartItemUpdateDTO = new CartItemUpdateDTO(5);
        URI uri = UriComponentsBuilder.fromUriString("/carts/" + user1.getId() + "/items/" + book1.getId())
                .build().encode().toUri();

        String updateCartItemString = objectMapper.writeValueAsString(cartItemUpdateDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(updateCartItemString, headers);

        ResponseEntity<CartItemReadDTO> response = client.exchange(uri, HttpMethod.PUT, request, CartItemReadDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        book1.setAvailability(book1.getAvailability() - 5);
        CartItem updatedCartItem = CartItem.builder()
                .cart(cart1)
                .book(book1)
                .quantity(5)
                .build();
        CartItemReadDTO updatedCartItemDto = response.getBody();
        assertEquals(cartItemMapper.cartItemToReadDto(updatedCartItem), updatedCartItemDto);
    }

    @Test
    @DisplayName("PUT /carts/me/items/{bookID} - Error JWT")
    public void updateCartItemMeAndBookIDErrorJWT() throws Exception {
        when(authUtils.getUserIdFromAuthentication())
                .thenThrow(new IllegalArgumentException("Invalid Jwt token"));

        CartItemUpdateDTO cartItemUpdateDTO = new CartItemUpdateDTO(5);
        URI uri = UriComponentsBuilder.fromUriString("/carts/me/items/" + book1.getId())
                .build().encode().toUri();

        String updateCartItemString = objectMapper.writeValueAsString(cartItemUpdateDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(updateCartItemString, headers);

        ResponseEntity<CartItemReadDTO> response = client.exchange(uri, HttpMethod.PUT, request, CartItemReadDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("PUT /carts/me/items/999 - Book Not Found")
    public void updateCartItemMeAndBookIDNotFoundBook() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());

        CartItemUpdateDTO cartItemUpdateDTO = new CartItemUpdateDTO(5);
        URI uri = UriComponentsBuilder.fromUriString("/carts/me/items/999")
                .build().encode().toUri();

        String updateCartItemString = objectMapper.writeValueAsString(cartItemUpdateDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(updateCartItemString, headers);

        ResponseEntity<CartItemReadDTO> response = client.exchange(uri, HttpMethod.PUT, request, CartItemReadDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("PUT /carts/me/items/999 - Invalid Book ID")
    public void updateCartItemMeAndBookIDInvalidBook() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());

        CartItemUpdateDTO cartItemUpdateDTO = new CartItemUpdateDTO(5);
        URI uri = UriComponentsBuilder.fromUriString("/carts/me/items/-1")
                .build().encode().toUri();

        String updateCartItemString = objectMapper.writeValueAsString(cartItemUpdateDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(updateCartItemString, headers);

        ResponseEntity<CartItemReadDTO> response = client.exchange(uri, HttpMethod.PUT, request, CartItemReadDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /carts/me/items/{bookID} - Success")
    public void deleteCartItemMeAndBookID() {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());
        URI uri = UriComponentsBuilder.fromUriString("/carts/me/items/" + book1.getId())
                .build().encode().toUri();

        ResponseEntity<Void> response = client.exchange(uri, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        assertFalse(cartItemsRepository.existsByUserIDAndBookID(user1.getId(), book1.getId()));
    }

    @Test
    @DisplayName("DELETE /carts/me/items/{bookID} - Error JWT")
    public void deleteCartItemMeAndBookIDErrorJWT() {
        when(authUtils.getUserIdFromAuthentication())
                .thenThrow(new IllegalArgumentException("Invalid Jwt token"));

        ResponseEntity<Void> response = client.exchange("/carts/me/items/" + book1.getId(), HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /carts/me/items/999 - Not Found Book")
    public void deleteCartItemMeAndBookIDNotFoundBook() {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());
        ResponseEntity<Void> response = client.exchange("/carts/me/items/999", HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        assertFalse(cartItemsRepository.existsByUserIDAndBookID(user1.getId(), 999));
    }

    @Test
    @DisplayName("DELETE /carts/me/items/-1 - Invalid Book ID")
    public void deleteCartItemMeAndBookIDInvalidBook() {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());
        ResponseEntity<Void> response = client.exchange("/carts/me/items/-1", HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        assertFalse(cartItemsRepository.existsByUserIDAndBookID(user1.getId(), -1));
    }
}
