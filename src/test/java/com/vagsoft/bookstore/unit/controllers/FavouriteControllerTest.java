package com.vagsoft.bookstore.unit.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vagsoft.bookstore.controllers.FavouriteController;
import com.vagsoft.bookstore.dto.bookDTOs.BookReadDTO;
import com.vagsoft.bookstore.dto.favouriteDTOs.FavouriteReadDTO;
import com.vagsoft.bookstore.dto.genreDTOs.GenreDTO;
import com.vagsoft.bookstore.repositories.FavouriteRepository;
import com.vagsoft.bookstore.repositories.UserRepository;
import com.vagsoft.bookstore.services.FavouriteService;
import com.vagsoft.bookstore.utils.AuthUtils;
import org.junit.jupiter.api.*;
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

@WebMvcTest(FavouriteController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ActiveProfiles("test")
public class FavouriteControllerTest {
    @MockitoBean
    private FavouriteService favouriteService;
    @MockitoBean
    private FavouriteRepository favouriteRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private AuthUtils authUtils;
    @MockitoBean
    private UserRepository userRepository;

    private List<BookReadDTO> storedBooks;
    private List<FavouriteReadDTO> storedFavourites;

    @BeforeEach
    void setUp() {
        storedBooks = new ArrayList<>();
        storedBooks.add(new BookReadDTO(1, "title", "author", "description", 1, 1.0, 1, "isbn", new ArrayList<>()));
        storedBooks.add(new BookReadDTO(2, "title2", "author2", "description2", 2, 2.0, 2, "isbn2",
                List.of(new GenreDTO(1, "genre1"), new GenreDTO(2, "genre2"))));

        storedFavourites = new ArrayList<>();
        storedFavourites.add(new FavouriteReadDTO(storedBooks.getFirst()));
        storedFavourites.add(new FavouriteReadDTO(storedBooks.get(1)));
    }

    @Test
    @DisplayName("GET /users/1/favourites - Success")
    public void getUserFavouritesSuccess() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        Page<FavouriteReadDTO> page = new PageImpl<>(storedFavourites, pageable, 2);

        when(userRepository.existsById(1)).thenReturn(true);
        when(favouriteService.getFavouritesByUserID(1, pageable)).thenReturn(page);

        mockMvc.perform(get("/users/1/favourites").param("page", "0").param("size", "20").accept("application/json"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].book.id").value(storedFavourites.get(0).getBook().getId()))
                .andExpect(jsonPath("$.content[1].book.id").value(storedFavourites.get(1).getBook().getId()));
    }

    @Test
    @DisplayName("GET /users/999/favourites - User Not Found")
    public void getUserFavouritesUserNotFound() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        Page<FavouriteReadDTO> page = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(favouriteService.getFavouritesByUserID(999, pageable)).thenReturn(page);

        mockMvc.perform(get("/users/999/favourites").param("page", "0").param("size", "20").accept("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /users/-1/favourites - Invalid User ID")
    public void getUserFavouritesUser() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        Page<FavouriteReadDTO> page = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(favouriteService.getFavouritesByUserID(-1, pageable)).thenReturn(page);

        mockMvc.perform(get("/users/-1/favourites").param("page", "0").param("size", "20").accept("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /users/me/favourites - Success")
    public void getMeFavouritesSuccess() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        Page<FavouriteReadDTO> page = new PageImpl<>(storedFavourites, pageable, 2);

        lenient().when(authUtils.getUserIdFromAuthentication()).thenReturn(1);

        when(favouriteService.getFavouritesByUserID(1, pageable)).thenReturn(page);

        mockMvc.perform(get("/users/me/favourites").param("page", "0").param("size", "20").accept("application/json"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].book.id").value(storedFavourites.get(0).getBook().getId()))
                .andExpect(jsonPath("$.content[1].book.id").value(storedFavourites.get(1).getBook().getId()));
    }

    @Test
    @DisplayName("GET /users/me/favourites - Error JWT")
    public void getMeFavouritesErrorJwtNotFound() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenThrow(new IllegalArgumentException("Invalid JWT"));

        Pageable pageable = PageRequest.of(0, 20);
        Page<FavouriteReadDTO> page = new PageImpl<>(storedFavourites, pageable, 2);

        when(favouriteService.getFavouritesByUserID(1, pageable)).thenReturn(page);

        mockMvc.perform(get("/users/me/favourites").param("page", "0").param("size", "20").accept("application/json"))
                .andExpect(status().isBadRequest());
    }

}
