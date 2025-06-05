package com.vagsoft.bookstore.unit.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vagsoft.bookstore.controllers.CartItemsController;
import com.vagsoft.bookstore.dto.bookDTOs.BookReadDTO;
import com.vagsoft.bookstore.dto.cartDTOs.CartItemReadDTO;
import com.vagsoft.bookstore.dto.cartDTOs.CartItemUpdateDTO;
import com.vagsoft.bookstore.dto.cartDTOs.CartItemWriteDTO;
import com.vagsoft.bookstore.dto.cartDTOs.CartReadDTO;
import com.vagsoft.bookstore.dto.genreDTOs.GenreDTO;
import com.vagsoft.bookstore.repositories.BookRepository;
import com.vagsoft.bookstore.repositories.CartItemsRepository;
import com.vagsoft.bookstore.repositories.UserRepository;
import com.vagsoft.bookstore.services.CartItemsService;
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

@WebMvcTest(CartItemsController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ActiveProfiles("test")
public class CartItemsControllerTest {
    @MockitoBean
    private CartItemsService cartItemsService;
    @MockitoBean
    private CartItemsRepository cartItemsRepository;
    @MockitoBean
    private BookRepository bookRepository;
    @MockitoBean
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private AuthUtils authUtils;

    BookReadDTO book1, book2;
    CartItemReadDTO cartItem1, cartItem2, cartItem3;
    CartReadDTO cart1, cart2;

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
    }

    @Test
    @DisplayName("GET /carts/1/items - Success")
    void getCartItemsByUserID() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        Page<CartItemReadDTO> page = new PageImpl<>(List.of(cartItem1, cartItem2), pageable, 0);

        when(userRepository.existsById(1)).thenReturn(true);
        when(cartItemsService.getAllCartItems(1, pageable)).thenReturn(page);

        mockMvc.perform(get("/carts/1/items")).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].cartID").value(cartItem1.getCartID()))
                .andExpect(jsonPath("$.content[0].book.id").value(cartItem1.getBook().getId()))
                .andExpect(jsonPath("$.content[0].quantity").value(cartItem1.getQuantity()))
                .andExpect(jsonPath("$.content[1].cartID").value(cartItem2.getCartID()))
                .andExpect(jsonPath("$.content[1].book.id").value(cartItem2.getBook().getId()))
                .andExpect(jsonPath("$.content[1].quantity").value(cartItem2.getQuantity()));
    }

    @Test
    @DisplayName("GET /carts/999/items - User Not Found")
    void getCartItemsByUserIDNotFound() throws Exception {
        when(userRepository.existsById(999)).thenReturn(false);

        mockMvc.perform(get("/carts/999/items"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /carts/-1/items - Invalid User ID")
    void getCartItemsByUserIDInvalid() throws Exception {
        when(userRepository.existsById(-1)).thenReturn(false);

        mockMvc.perform(get("/carts/-1/items"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /carts/1/items/1 - Success")
    void getCartItemByUserIDAndBookID() throws Exception {
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(1)).thenReturn(true);
        when(cartItemsRepository.existsByUserIDAndBookID(1, 1)).thenReturn(true);
        when(cartItemsService.getCartItem(1, 1)).thenReturn(cartItem1);

        mockMvc.perform(get("/carts/1/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartID").value(cartItem1.getCartID()))
                .andExpect(jsonPath("$.book.id").value(cartItem1.getBook().getId()))
                .andExpect(jsonPath("$.quantity").value(cartItem1.getQuantity()));
    }

    @Test
    @DisplayName("GET /carts/999/items/1 - User Not Found")
    void getCartItemByUserIDAndBookIDNotFoundUser() throws Exception {
        when(userRepository.existsById(999)).thenReturn(false);
        when(bookRepository.existsById(1)).thenReturn(true);
        when(cartItemsRepository.existsByUserIDAndBookID(999, 1)).thenReturn(false);

        mockMvc.perform(get("/carts/999/items/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /carts/-1/items/1 - Invalid User ID")
    void getCartItemByUserIDAndBookIDInvalidUser() throws Exception {
        when(userRepository.existsById(-1)).thenReturn(false);
        when(bookRepository.existsById(1)).thenReturn(true);
        when(cartItemsRepository.existsByUserIDAndBookID(-1, 1)).thenReturn(false);

        mockMvc.perform(get("/carts/-1/items/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /carts/1/items/999 - Book Not Found")
    void getCartItemByUserIDAndBookIDNotFoundBook() throws Exception {
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(999)).thenReturn(false);
        when(cartItemsRepository.existsByUserIDAndBookID(1, 999)).thenReturn(false);

        mockMvc.perform(get("/carts/1/items/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /carts/1/items/-1 - Invalid Book ID")
    void getCartItemByUserIDAndBookIDInvalidBook() throws Exception {
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(-1)).thenReturn(false);
        when(cartItemsRepository.existsByUserIDAndBookID(1, -1)).thenReturn(false);

        mockMvc.perform(get("/carts/1/items/-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /carts/999/items/999 - User and Book Not Found")
    void getCartItemByUserIDAndBookIDNotFoundBoth() throws Exception {
        when(userRepository.existsById(999)).thenReturn(false);
        when(bookRepository.existsById(999)).thenReturn(false);
        when(cartItemsRepository.existsByUserIDAndBookID(999, 999)).thenReturn(false);

        mockMvc.perform(get("/carts/999/items/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /carts/-1/items/-1 - Invalid User and Book ID")
    void getCartItemByUserIDAndBookIDInvalidBoth() throws Exception {
        when(userRepository.existsById(-1)).thenReturn(false);
        when(bookRepository.existsById(-1)).thenReturn(false);
        when(cartItemsRepository.existsByUserIDAndBookID(-1, -1)).thenReturn(false);

        mockMvc.perform(get("/carts/-1/items/-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /carts/1/items/1 - Success")
    void updateCartItemByUserIDAndBookID() throws Exception {
        CartItemUpdateDTO cartItemUpdateDTO = new CartItemUpdateDTO(3);
        CartItemReadDTO updatedCartItem = new CartItemReadDTO(1, book1, 3);

        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(1)).thenReturn(true);
        when(cartItemsRepository.existsByUserIDAndBookID(1, 1)).thenReturn(true);
        when(cartItemsService.updateCartItem(1, 1, cartItemUpdateDTO)).thenReturn(Optional.of(updatedCartItem));

        String cartItemUpdateDTOString = objectMapper.writeValueAsString(cartItemUpdateDTO);
        mockMvc.perform(put("/carts/1/items/1").contentType("application/json").content(cartItemUpdateDTOString))
                .andExpect(status().isOk()).andExpect(jsonPath("$.cartID").value(updatedCartItem.getCartID()))
                .andExpect(jsonPath("$.book.id").value(updatedCartItem.getBook().getId()))
                .andExpect(jsonPath("$.quantity").value(updatedCartItem.getQuantity()));
    }

    @Test
    @DisplayName("PUT /carts/999/items/1 - User Not Found")
    void updateCartItemByUserIDAndBookIDNotFoundUser() throws Exception {
        CartItemUpdateDTO cartItemUpdateDTO = new CartItemUpdateDTO(3);

        when(userRepository.existsById(999)).thenReturn(false);
        when(bookRepository.existsById(1)).thenReturn(true);
        when(cartItemsRepository.existsByUserIDAndBookID(999, 1)).thenReturn(false);

        String cartItemUpdateDTOString = objectMapper.writeValueAsString(cartItemUpdateDTO);
        mockMvc.perform(put("/carts/999/items/1").contentType("application/json").content(cartItemUpdateDTOString))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /carts/-1/items/1 - Invalid User ID")
    void updateCartItemByUserIDAndBookIDInvalidUser() throws Exception {
        CartItemUpdateDTO cartItemUpdateDTO = new CartItemUpdateDTO(3);

        when(userRepository.existsById(-1)).thenReturn(false);
        when(bookRepository.existsById(1)).thenReturn(true);
        when(cartItemsRepository.existsByUserIDAndBookID(-1, 1)).thenReturn(false);

        String cartItemUpdateDTOString = objectMapper.writeValueAsString(cartItemUpdateDTO);
        mockMvc.perform(put("/carts/-1/items/1").contentType("application/json").content(cartItemUpdateDTOString))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /carts/1/items/999 - Book Not Found")
    void updateCartItemByUserIDAndBookIDNotFoundBook() throws Exception {
        CartItemUpdateDTO cartItemUpdateDTO = new CartItemUpdateDTO(3);

        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(999)).thenReturn(false);
        when(cartItemsRepository.existsByUserIDAndBookID(1, 999)).thenReturn(false);

        String cartItemUpdateDTOString = objectMapper.writeValueAsString(cartItemUpdateDTO);
        mockMvc.perform(put("/carts/1/items/999").contentType("application/json").content(cartItemUpdateDTOString))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /carts/1/items/-1 - Invalid Book ID")
    void updateCartItemByUserIDAndBookIDInvalidBook() throws Exception {
        CartItemUpdateDTO cartItemUpdateDTO = new CartItemUpdateDTO(3);

        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(-1)).thenReturn(false);
        when(cartItemsRepository.existsByUserIDAndBookID(1, -1)).thenReturn(false);

        String cartItemUpdateDTOString = objectMapper.writeValueAsString(cartItemUpdateDTO);
        mockMvc.perform(put("/carts/1/items/-1").contentType("application/json").content(cartItemUpdateDTOString))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /carts/999/items/999 - User and Book Not Found")
    void updateCartItemByUserIDAndBookIDNotFoundBoth() throws Exception {
        CartItemUpdateDTO cartItemUpdateDTO = new CartItemUpdateDTO(3);

        when(userRepository.existsById(999)).thenReturn(false);
        when(bookRepository.existsById(999)).thenReturn(false);
        when(cartItemsRepository.existsByUserIDAndBookID(999, 999)).thenReturn(false);

        String cartItemUpdateDTOString = objectMapper.writeValueAsString(cartItemUpdateDTO);
        mockMvc.perform(put("/carts/999/items/999").contentType("application/json").content(cartItemUpdateDTOString))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /carts/-1/items/-1 - Invalid User and Book ID")
    void updateCartItemByUserIDAndBookIDInvalidBoth() throws Exception {
        CartItemUpdateDTO cartItemUpdateDTO = new CartItemUpdateDTO(3);

        when(userRepository.existsById(-1)).thenReturn(false);
        when(bookRepository.existsById(-1)).thenReturn(false);
        when(cartItemsRepository.existsByUserIDAndBookID(-1, -1)).thenReturn(false);

        String cartItemUpdateDTOString = objectMapper.writeValueAsString(cartItemUpdateDTO);
        mockMvc.perform(put("/carts/-1/items/-1").contentType("application/json").content(cartItemUpdateDTOString))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /carts/1/items/1 - Success")
    void deleteCartItemByUserIDAndBookID() throws Exception {
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(1)).thenReturn(true);
        when(cartItemsRepository.existsByUserIDAndBookID(1, 1)).thenReturn(true);
        doNothing().when(cartItemsService).deleteCartItem(1, 1);

        mockMvc.perform(delete("/carts/1/items/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /carts/999/items/1 - User Not Found")
    void deleteCartItemByUserIDAndBookIDNotFoundUser() throws Exception {
        when(userRepository.existsById(999)).thenReturn(false);
        when(bookRepository.existsById(1)).thenReturn(true);
        when(cartItemsRepository.existsByUserIDAndBookID(999, 1)).thenReturn(false);

        mockMvc.perform(delete("/carts/999/items/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /carts/-1/items/1 - Invalid User ID")
    void deleteCartItemByUserIDAndBookIDInvalidUser() throws Exception {
        when(userRepository.existsById(-1)).thenReturn(false);
        when(bookRepository.existsById(1)).thenReturn(true);
        when(cartItemsRepository.existsByUserIDAndBookID(-1, 1)).thenReturn(false);

        mockMvc.perform(delete("/carts/-1/items/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /carts/1/items/999 - Book Not Found")
    void deleteCartItemByUserIDAndBookIDNotFoundBook() throws Exception {
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(999)).thenReturn(false);
        when(cartItemsRepository.existsByUserIDAndBookID(1, 999)).thenReturn(false);

        mockMvc.perform(delete("/carts/1/items/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /carts/1/items/-1 - Invalid Book ID")
    void deleteCartItemByUserIDAndBookIDInvalidBook() throws Exception {
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(-1)).thenReturn(false);
        when(cartItemsRepository.existsByUserIDAndBookID(1, -1)).thenReturn(false);

        mockMvc.perform(delete("/carts/1/items/-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /carts/999/items/999 - User and Book Not Found")
    void deleteCartItemByUserIDAndBookIDNotFoundBoth() throws Exception {
        when(userRepository.existsById(999)).thenReturn(false);
        when(bookRepository.existsById(999)).thenReturn(false);
        when(cartItemsRepository.existsByUserIDAndBookID(999, 999)).thenReturn(false);

        mockMvc.perform(delete("/carts/999/items/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /carts/-1/items/-1 - Invalid User and Book ID")
    void deleteCartItemByUserIDAndBookIDInvalidBoth() throws Exception {
        when(userRepository.existsById(-1)).thenReturn(false);
        when(bookRepository.existsById(-1)).thenReturn(false);
        when(cartItemsRepository.existsByUserIDAndBookID(-1, -1)).thenReturn(false);

        mockMvc.perform(delete("/carts/-1/items/-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /carts/me/items - Success")
    void getCartItemsMe() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        Page<CartItemReadDTO> page = new PageImpl<>(List.of(cartItem1, cartItem2), pageable, 0);

        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(cartItemsService.getAllCartItems(1, pageable)).thenReturn(page);

        mockMvc.perform(get("/carts/me/items")).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].cartID").value(cartItem1.getCartID()))
                .andExpect(jsonPath("$.content[0].book.id").value(cartItem1.getBook().getId()))
                .andExpect(jsonPath("$.content[0].quantity").value(cartItem1.getQuantity()))
                .andExpect(jsonPath("$.content[1].cartID").value(cartItem2.getCartID()))
                .andExpect(jsonPath("$.content[1].book.id").value(cartItem2.getBook().getId()))
                .andExpect(jsonPath("$.content[1].quantity").value(cartItem2.getQuantity()));
    }

    @Test
    @DisplayName("GET /carts/me/items - Error JWT")
    void getCartItemsMeErrorJWT() throws Exception {

        when(authUtils.getUserIdFromAuthentication()).thenThrow(new IllegalArgumentException("Invalid JWT token"));

        mockMvc.perform(get("/carts/me/items"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /carts/me/items - Success")
    void addCartItemMe() throws Exception {
        CartItemWriteDTO cartItemWriteDTO = new CartItemWriteDTO(1, 2);
        CartItemReadDTO newCartItem = new CartItemReadDTO(1, book1, 2);

        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(cartItemsRepository.existsByUserIDAndBookID(1, 1)).thenReturn(false);
        when(bookRepository.existsById(1)).thenReturn(true);
        when(cartItemsService.addCartItem(1, cartItemWriteDTO)).thenReturn(java.util.Optional.of(newCartItem));

        String cartItemWriteDTOString = objectMapper.writeValueAsString(cartItemWriteDTO);
        mockMvc.perform(post("/carts/me/items").contentType("application/json").content(cartItemWriteDTOString))
                .andExpect(status().isOk()).andExpect(jsonPath("$.cartID").value(newCartItem.getCartID()))
                .andExpect(jsonPath("$.book.id").value(newCartItem.getBook().getId()))
                .andExpect(jsonPath("$.quantity").value(newCartItem.getQuantity()));
    }

    @Test
    @DisplayName("POST /carts/me/items - Error JWT")
    void addCartItemMeErrorJWT() throws Exception {
        CartItemWriteDTO cartItemWriteDTO = new CartItemWriteDTO(1, 2);

        when(authUtils.getUserIdFromAuthentication()).thenThrow(new IllegalArgumentException("Invalid JWT token"));

        String cartItemWriteDTOString = objectMapper.writeValueAsString(cartItemWriteDTO);
        mockMvc.perform(post("/carts/me/items").contentType("application/json").content(cartItemWriteDTOString))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /carts/me/items/1 - Success")
    void getCartItemMeByBookID() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(1)).thenReturn(true);
        when(cartItemsRepository.existsByUserIDAndBookID(1, 1)).thenReturn(true);
        when(cartItemsService.getCartItem(1, 1)).thenReturn(cartItem1);

        mockMvc.perform(get("/carts/me/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartID").value(cartItem1.getCartID()))
                .andExpect(jsonPath("$.book.id").value(cartItem1.getBook().getId()))
                .andExpect(jsonPath("$.quantity").value(cartItem1.getQuantity()));
    }

    @Test
    @DisplayName("GET /carts/me/items/1 - Error JWT")
    void getCartItemMeByBookIDErrorJWT() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenThrow(new IllegalArgumentException("Invalid JWT token"));
        when(bookRepository.existsById(1)).thenReturn(true);

        mockMvc.perform(get("/carts/me/items/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /carts/me/items/999 - Book Not Found")
    void getCartItemMeByBookIDNotFoundBook() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(999)).thenReturn(false);
        when(cartItemsRepository.existsByUserIDAndBookID(1, 999)).thenReturn(false);

        mockMvc.perform(get("/carts/me/items/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /carts/me/items/-1 - Invalid Book ID")
    void getCartItemMeByBookIDInvalidBook() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(-1)).thenReturn(false);
        when(cartItemsRepository.existsByUserIDAndBookID(1, -1)).thenReturn(false);

        mockMvc.perform(get("/carts/me/items/-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /carts/me/items/1 - Success")
    void updateCartItemMeByBookID() throws Exception {
        CartItemUpdateDTO cartItemUpdateDTO = new CartItemUpdateDTO(3);
        CartItemReadDTO updatedCartItem = new CartItemReadDTO(1, book1, 3);

        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(1)).thenReturn(true);
        when(cartItemsRepository.existsByUserIDAndBookID(1, 1)).thenReturn(true);
        when(cartItemsService.updateCartItem(1, 1, cartItemUpdateDTO)).thenReturn(Optional.of(updatedCartItem));

        String cartItemUpdateDTOString = objectMapper.writeValueAsString(cartItemUpdateDTO);
        mockMvc.perform(put("/carts/me/items/1").contentType("application/json").content(cartItemUpdateDTOString))
                .andExpect(status().isOk()).andExpect(jsonPath("$.cartID").value(updatedCartItem.getCartID()))
                .andExpect(jsonPath("$.book.id").value(updatedCartItem.getBook().getId()))
                .andExpect(jsonPath("$.quantity").value(updatedCartItem.getQuantity()));
    }

    @Test
    @DisplayName("PUT /carts/me/items/1 - Error JWT")
    void updateCartItemMeByBookIDErrorJWT() throws Exception {
        CartItemUpdateDTO cartItemUpdateDTO = new CartItemUpdateDTO(3);

        when(authUtils.getUserIdFromAuthentication()).thenThrow(new IllegalArgumentException("Invalid JWT token"));
        when(bookRepository.existsById(1)).thenReturn(true);

        String cartItemUpdateDTOString = objectMapper.writeValueAsString(cartItemUpdateDTO);
        mockMvc.perform(put("/carts/me/items/1").contentType("application/json").content(cartItemUpdateDTOString))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /carts/me/items/999 - Book Not Found")
    void updateCartItemMeByBookIDNotFoundBook() throws Exception {
        CartItemUpdateDTO cartItemUpdateDTO = new CartItemUpdateDTO(3);

        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(999)).thenReturn(false);
        when(cartItemsRepository.existsByUserIDAndBookID(1, 999)).thenReturn(false);

        String cartItemUpdateDTOString = objectMapper.writeValueAsString(cartItemUpdateDTO);
        mockMvc.perform(put("/carts/me/items/999").contentType("application/json").content(cartItemUpdateDTOString))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /carts/me/items/-1 - Invalid Book ID")
    void updateCartItemMeByBookIDInvalidBook() throws Exception {
        CartItemUpdateDTO cartItemUpdateDTO = new CartItemUpdateDTO(3);

        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(-1)).thenReturn(false);
        when(cartItemsRepository.existsByUserIDAndBookID(1, -1)).thenReturn(false);

        String cartItemUpdateDTOString = objectMapper.writeValueAsString(cartItemUpdateDTO);
        mockMvc.perform(put("/carts/me/items/-1").contentType("application/json").content(cartItemUpdateDTOString))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /carts/me/items/1 - Success")
    void deleteCartItemMeByBookID() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(1)).thenReturn(true);
        when(cartItemsRepository.existsByUserIDAndBookID(1, 1)).thenReturn(true);
        doNothing().when(cartItemsService).deleteCartItem(1, 1);

        mockMvc.perform(delete("/carts/me/items/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /carts/me/items/1 - Error JWT")
    void deleteCartItemMeByBookIDErrorJWT() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenThrow(new IllegalArgumentException("Invalid JWT token"));
        when(bookRepository.existsById(1)).thenReturn(true);

        mockMvc.perform(delete("/carts/me/items/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /carts/me/items/999 - Book Not Found")
    void deleteCartItemMeByBookIDNotFoundBook() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(999)).thenReturn(false);
        when(cartItemsRepository.existsByUserIDAndBookID(1, 999)).thenReturn(false);

        mockMvc.perform(delete("/carts/me/items/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /carts/me/items/-1 - Invalid Book ID")
    void deleteCartItemMeByBookIDInvalidBook() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(-1)).thenReturn(false);
        when(cartItemsRepository.existsByUserIDAndBookID(1, -1)).thenReturn(false);

        mockMvc.perform(delete("/carts/me/items/-1"))
                .andExpect(status().isBadRequest());
    }

}
