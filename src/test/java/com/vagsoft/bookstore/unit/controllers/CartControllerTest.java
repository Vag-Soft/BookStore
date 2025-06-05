package com.vagsoft.bookstore.unit.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import com.vagsoft.bookstore.controllers.CartController;
import com.vagsoft.bookstore.dto.bookDTOs.BookReadDTO;
import com.vagsoft.bookstore.dto.cartDTOs.CartItemReadDTO;
import com.vagsoft.bookstore.dto.cartDTOs.CartReadDTO;
import com.vagsoft.bookstore.dto.genreDTOs.GenreDTO;
import com.vagsoft.bookstore.repositories.BookRepository;
import com.vagsoft.bookstore.repositories.CartRepository;
import com.vagsoft.bookstore.repositories.UserRepository;
import com.vagsoft.bookstore.services.CartService;
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

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ActiveProfiles("test")
public class CartControllerTest {
    @MockitoBean
    private CartService cartService;
    @MockitoBean
    private CartRepository cartRepository;
    @MockitoBean
    private BookRepository bookRepository;
    @MockitoBean
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AuthUtils authUtils;

    BookReadDTO book1, book2;
    CartItemReadDTO cartItem1, cartItem2, cartItem3;
    CartReadDTO cart1, cart2;
    List<CartReadDTO> storedCarts;
    @BeforeEach
    void setUp() {
        book1 = new BookReadDTO(1, "The Lord of the Rings", "J. R. R. Tolkien",
                "The Lord of the Rings is a series of three fantasy novels written by English author and scholar J. R. R. Tolkien.",
                1178, 15.0, 5, "978-0-395-36381-0", new ArrayList<>());

        book2 = new BookReadDTO(2, "Harry Potter and the Philosopher's Stone", "J. K. Rowling",
                "Harry Potter and the Philosopher's Stone is a fantasy novel written by British author J. K. Rowling.",
                223, 20.0, 10, "978-0-7-152-20664-5", List.of(new GenreDTO(1, "Fantasy")));

        cartItem1 = new CartItemReadDTO(1, book1, 2);
        cartItem2 = new CartItemReadDTO(2, book2, 1);
        cartItem3 = new CartItemReadDTO(3, book1, 3);

        cart1 = new CartReadDTO(1, 1, List.of(cartItem1, cartItem2));
        cart2 = new CartReadDTO(2, 2, List.of(cartItem3));

        storedCarts = new ArrayList<>();
        storedCarts.add(cart1);
        storedCarts.add(cart2);
    }

    @Test
    @DisplayName("GET /carts - Success")
    void getAllCarts() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        Page<CartReadDTO> page = new PageImpl<>(storedCarts, pageable, 2);

        when(cartService.getAllCarts(pageable)).thenReturn(page);

        mockMvc.perform(get("/carts").param("page", "0").param("size", "20").accept("application/json"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(cart1.getId()))
                .andExpect(jsonPath("$.content[0].userID").value(cart1.getUserID()))
                .andExpect(jsonPath("$.content[0].cartItems", hasSize(2)))
                .andExpect(jsonPath("$.content[0].cartItems[0].cartID").value(cartItem1.getCartID()))
                .andExpect(jsonPath("$.content[0].cartItems[0].book.id").value(book1.getId()))
                .andExpect(jsonPath("$.content[0].cartItems[0].quantity").value(cartItem1.getQuantity()))
                .andExpect(jsonPath("$.content[0].cartItems[1].cartID").value(cartItem2.getCartID()))
                .andExpect(jsonPath("$.content[0].cartItems[1].book.id").value(book2.getId()))
                .andExpect(jsonPath("$.content[0].cartItems[1].quantity").value(cartItem2.getQuantity()))
                .andExpect(jsonPath("$.content[1].id").value(cart2.getId()))
                .andExpect(jsonPath("$.content[1].userID").value(cart2.getUserID()))
                .andExpect(jsonPath("$.content[1].cartItems", hasSize(1)))
                .andExpect(jsonPath("$.content[1].cartItems[0].cartID").value(cartItem3.getCartID()))
                .andExpect(jsonPath("$.content[1].cartItems[0].book.id").value(book1.getId()))
                .andExpect(jsonPath("$.content[1].cartItems[0].quantity").value(cartItem3.getQuantity()));
    }

    @Test
    @DisplayName("GET /carts/1 - Success")
    void getCartByUserId() throws Exception {
        when(userRepository.existsById(1)).thenReturn(true);
        when(cartService.getCartByUserId(1)).thenReturn(cart1);

        mockMvc.perform(get("/carts/{userID}", 1).accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cart1.getId()))
                .andExpect(jsonPath("$.userID").value(cart1.getUserID()))
                .andExpect(jsonPath("$.cartItems", hasSize(2)))
                .andExpect(jsonPath("$.cartItems[0].cartID").value(cartItem1.getCartID()))
                .andExpect(jsonPath("$.cartItems[0].book.id").value(book1.getId()))
                .andExpect(jsonPath("$.cartItems[0].quantity").value(cartItem1.getQuantity()))
                .andExpect(jsonPath("$.cartItems[1].cartID").value(cartItem2.getCartID()))
                .andExpect(jsonPath("$.cartItems[1].book.id").value(book2.getId()))
                .andExpect(jsonPath("$.cartItems[1].quantity").value(cartItem2.getQuantity()));
    }

    @Test
    @DisplayName("GET /carts/999 - User Not Found")
    void getCartByUserIdNotFound() throws Exception {
        when(userRepository.existsById(1)).thenReturn(false);

        mockMvc.perform(get("/carts/{userID}", 999).accept("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /carts/-1 - Invalid User ID")
    void getCartByUserIdInvalid() throws Exception {
        when(userRepository.existsById(-1)).thenReturn(false);

        mockMvc.perform(get("/carts/{userID}", -1).accept("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /carts/me - Success")
    void getCartMe() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(cartService.getCartByUserId(1)).thenReturn(cart1);

        mockMvc.perform(get("/carts/me").accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cart1.getId()))
                .andExpect(jsonPath("$.userID").value(cart1.getUserID()))
                .andExpect(jsonPath("$.cartItems", hasSize(2)))
                .andExpect(jsonPath("$.cartItems[0].cartID").value(cartItem1.getCartID()))
                .andExpect(jsonPath("$.cartItems[0].book.id").value(book1.getId()))
                .andExpect(jsonPath("$.cartItems[0].quantity").value(cartItem1.getQuantity()))
                .andExpect(jsonPath("$.cartItems[1].cartID").value(cartItem2.getCartID()))
                .andExpect(jsonPath("$.cartItems[1].book.id").value(book2.getId()))
                .andExpect(jsonPath("$.cartItems[1].quantity").value(cartItem2.getQuantity()));
    }

    @Test
    @DisplayName("GET /carts/me - Error JWT")
    void getCartMeErrorJWT() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenThrow(new IllegalArgumentException("JWT Error"));

        mockMvc.perform(get("/carts/me").accept("application/json"))
                .andExpect(status().isBadRequest());
    }
}
