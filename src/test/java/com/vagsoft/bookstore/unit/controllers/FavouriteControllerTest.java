package com.vagsoft.bookstore.unit.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vagsoft.bookstore.controllers.FavouriteController;
import com.vagsoft.bookstore.dto.bookDTOs.BookReadDTO;
import com.vagsoft.bookstore.dto.favouriteDTOs.FavouriteReadDTO;
import com.vagsoft.bookstore.dto.favouriteDTOs.FavouriteWriteDTO;
import com.vagsoft.bookstore.dto.genreDTOs.GenreDTO;
import com.vagsoft.bookstore.repositories.BookRepository;
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
    @DisplayName("POST /users/1/favourites - Success")
    public void addFavouriteSuccess() throws Exception {
        FavouriteWriteDTO favouriteWriteDTO = new FavouriteWriteDTO(2);
        FavouriteReadDTO favouriteReadDTO = new FavouriteReadDTO(storedBooks.getLast());

        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(2)).thenReturn(true);
        when(favouriteRepository.existsByUser_IdAndBook_Id(1, 2)).thenReturn(false);
        when(favouriteService.addFavourite(1, favouriteWriteDTO)).thenReturn(Optional.of(favouriteReadDTO));

        String newFavouriteString = objectMapper.writeValueAsString(favouriteWriteDTO);
        mockMvc.perform(post("/users/1/favourites").content(newFavouriteString).contentType("application/json"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.book.id").value(storedBooks.getLast().getId()))
                .andExpect(jsonPath("$.book.title").value(storedBooks.getLast().getTitle()))
                .andExpect(jsonPath("$.book.author").value(storedBooks.getLast().getAuthor()))
                .andExpect(jsonPath("$.book.price").value(storedBooks.getLast().getPrice()))
                .andExpect(jsonPath("$.book.isbn").value(storedBooks.getLast().getIsbn()))
                .andExpect(jsonPath("$.book.genres", hasSize(2)));
    }

    @Test
    @DisplayName("DELETE /users/1/favourites/1 - Success")
    public void deleteFavouriteSuccess() throws Exception {
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(1)).thenReturn(true);
        when(favouriteRepository.existsByUser_IdAndBook_Id(1, 1)).thenReturn(true);
        doNothing().when(favouriteService).deleteFavourite(1, 1);

        mockMvc.perform(delete("/users/1/favourites/1").accept("application/json"))
                .andExpect(status().isNoContent());
    }
    @Test
    @DisplayName("DELETE /users/999/favourites/1 - User Not Found")
    public void deleteFavouriteNotFoundUser() throws Exception {
        when(userRepository.existsById(1)).thenReturn(false);
        when(bookRepository.existsById(1)).thenReturn(true);
        when(favouriteRepository.existsByUser_IdAndBook_Id(1, 1)).thenReturn(false);
        doNothing().when(favouriteService).deleteFavourite(999, 1);

        mockMvc.perform(delete("/users/999/favourites/1").accept("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /users/-1/favourites/1 - Invalid User ID")
    public void deleteFavouriteInvalidUser() throws Exception {
        when(userRepository.existsById(-1)).thenReturn(false);
        when(bookRepository.existsById(1)).thenReturn(true);
        when(favouriteRepository.existsByUser_IdAndBook_Id(-1, 1)).thenReturn(false);
        doNothing().when(favouriteService).deleteFavourite(-1, 1);

        mockMvc.perform(delete("/users/-1/favourites/1").accept("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /users/1/favourites/999 - Book Not Found")
    public void deleteFavouriteNotFoundBook() throws Exception {
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(999)).thenReturn(false);
        when(favouriteRepository.existsByUser_IdAndBook_Id(1, 999)).thenReturn(false);
        doNothing().when(favouriteService).deleteFavourite(1, 999);

        mockMvc.perform(delete("/users/1/favourites/999").accept("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /users/1/favourites/-1 - Invalid Book ID")
    public void deleteFavouriteInvalidBook() throws Exception {
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(-1)).thenReturn(false);
        when(favouriteRepository.existsByUser_IdAndBook_Id(1, -1)).thenReturn(false);
        doNothing().when(favouriteService).deleteFavourite(1, -1);

        mockMvc.perform(delete("/users/1/favourites/-1").accept("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /users/999/favourites/999 - User and Book Not Found")
    public void deleteFavouriteNotFoundBoth() throws Exception {
        when(userRepository.existsById(999)).thenReturn(false);
        when(bookRepository.existsById(999)).thenReturn(false);
        when(favouriteRepository.existsByUser_IdAndBook_Id(999, 999)).thenReturn(false);
        doNothing().when(favouriteService).deleteFavourite(999, 999);

        mockMvc.perform(delete("/users/999/favourites/999").accept("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /users/-1/favourites/-1 - Invalid User and Book IDs")
    public void deleteFavouriteInvalidBoth() throws Exception {
        when(userRepository.existsById(-1)).thenReturn(false);
        when(bookRepository.existsById(-1)).thenReturn(false);
        when(favouriteRepository.existsByUser_IdAndBook_Id(-1, -1)).thenReturn(false);
        doNothing().when(favouriteService).deleteFavourite(-1, -1);

        mockMvc.perform(delete("/users/-1/favourites/-1").accept("application/json"))
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

    @Test
    @DisplayName("POST /users/me/favourites - Success")
    public void addMeFavouriteSuccess() throws Exception {
        FavouriteWriteDTO favouriteWriteDTO = new FavouriteWriteDTO(2);
        FavouriteReadDTO favouriteReadDTO = new FavouriteReadDTO(storedBooks.getLast());

        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(2)).thenReturn(true);
        when(favouriteRepository.existsByUser_IdAndBook_Id(1, 2)).thenReturn(false);
        when(favouriteService.addFavourite(1, favouriteWriteDTO)).thenReturn(Optional.of(favouriteReadDTO));

        String newFavouriteString = objectMapper.writeValueAsString(favouriteWriteDTO);
        mockMvc.perform(post("/users/1/favourites").content(newFavouriteString).contentType("application/json"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.book.id").value(storedBooks.getLast().getId()))
                .andExpect(jsonPath("$.book.title").value(storedBooks.getLast().getTitle()))
                .andExpect(jsonPath("$.book.author").value(storedBooks.getLast().getAuthor()))
                .andExpect(jsonPath("$.book.price").value(storedBooks.getLast().getPrice()))
                .andExpect(jsonPath("$.book.isbn").value(storedBooks.getLast().getIsbn()))
                .andExpect(jsonPath("$.book.genres", hasSize(2)));
    }

    @Test
    @DisplayName("DELETE /users/me/favourites/1 - Success")
    public void deleteMeFavouriteSuccess() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(1)).thenReturn(true);
        when(favouriteRepository.existsByUser_IdAndBook_Id(1, 1)).thenReturn(true);
        doNothing().when(favouriteService).deleteFavourite(1, 1);

        mockMvc.perform(delete("/users/me/favourites/1").accept("application/json"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /users/me/favourites/1 - Error JWT")
    public void deleteMeFavouriteErrorJwt() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenThrow(new IllegalArgumentException("Invalid JWT"));

        when(bookRepository.existsById(1)).thenReturn(true);

        mockMvc.perform(delete("/users/me/favourites/1").accept("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /users/me/favourites/999 - Book Not Found")
    public void deleteMeFavouriteNotFoundBook() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(999)).thenReturn(false);
        when(favouriteRepository.existsByUser_IdAndBook_Id(1, 999)).thenReturn(false);
        doNothing().when(favouriteService).deleteFavourite(1, 999);

        mockMvc.perform(delete("/users/me/favourites/999").accept("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /users/me/favourites/-1 - Invalid Book ID")
    public void deleteMeFavouriteInvalidBook() throws Exception {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(1);
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById(-1)).thenReturn(false);
        when(favouriteRepository.existsByUser_IdAndBook_Id(1, -1)).thenReturn(false);
        doNothing().when(favouriteService).deleteFavourite(1, -1);

        mockMvc.perform(delete("/users/me/favourites/-1").accept("application/json"))
                .andExpect(status().isBadRequest());
    }
}
