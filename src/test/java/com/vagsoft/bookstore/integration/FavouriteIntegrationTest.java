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
import com.vagsoft.bookstore.dto.FavouriteReadDTO;
import com.vagsoft.bookstore.dto.FavouriteWriteDTO;
import com.vagsoft.bookstore.mappers.FavouriteMapper;
import com.vagsoft.bookstore.models.entities.Book;
import com.vagsoft.bookstore.models.entities.Favourite;
import com.vagsoft.bookstore.models.entities.User;
import com.vagsoft.bookstore.models.enums.Role;
import com.vagsoft.bookstore.pagination.CustomPageImpl;
import com.vagsoft.bookstore.repositories.BookRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ActiveProfiles("test")
public class FavouriteIntegrationTest {
    @Autowired
    private FavouriteRepository favouriteRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FavouriteService favouriteService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FavouriteMapper favouriteMapper;
    @Autowired
    private TestRestTemplate client;
    @MockitoBean
    private AuthUtils authUtils;

    Book book1, book2;
    User user1, user2;
    Favourite favourite1, favourite2, favourite3;

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

        favourite1 = Favourite.builder().book(book1).userID(user1.getId()).build();

        favourite2 = Favourite.builder().book(book2).userID(user1.getId()).build();

        favourite3 = Favourite.builder().book(book1).userID(user2.getId()).build();

        favouriteRepository.save(favourite1);
        favouriteRepository.save(favourite2);
        favouriteRepository.save(favourite3);
    }

    @AfterEach
    public void tearDown() {
        favouriteRepository.deleteAll();

        bookRepository.deleteAll();

        userRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /users/{userID}/favourites - Success")
    void getUsersIDFavourites() {
        URI uri = UriComponentsBuilder.fromUriString("/users/" + user1.getId() + "/favourites").build().encode()
                .toUri();

        ParameterizedTypeReference<CustomPageImpl<FavouriteReadDTO>> classType = new ParameterizedTypeReference<CustomPageImpl<FavouriteReadDTO>>() {
        };
        ResponseEntity<CustomPageImpl<FavouriteReadDTO>> response = client.exchange(uri, HttpMethod.GET, null,
                classType);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        System.out.println(response.getBody());
        assertNotNull(response.getBody());

        FavouriteReadDTO firstFavourite = response.getBody().getContent().getFirst();
        assertEquals(favouriteMapper.favouriteToReadDto(favourite1), firstFavourite);

        FavouriteReadDTO secondFavourite = response.getBody().getContent().getLast();
        assertEquals(favouriteMapper.favouriteToReadDto(favourite2), secondFavourite);
    }

    @Test
    @DisplayName("GET /users/99/favourites - User Not Found")
    void getUsersIDFavouritesNotFound() {
        URI uri = UriComponentsBuilder.fromUriString("/users/99/favourites").build().encode().toUri();

        ParameterizedTypeReference<CustomPageImpl<FavouriteReadDTO>> classType = new ParameterizedTypeReference<CustomPageImpl<FavouriteReadDTO>>() {
        };
        ResponseEntity<CustomPageImpl<FavouriteReadDTO>> response = client.exchange(uri, HttpMethod.GET, null,
                classType);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Page<FavouriteReadDTO> foundFavourites = favouriteService.getFavouritesByUserID(-1, null);
        assertTrue(foundFavourites.isEmpty());
    }

    @Test
    @DisplayName("GET /users/-1/favourites - Invalid ID")
    void getUsersIDFavouritesInvalid() {
        URI uri = UriComponentsBuilder.fromUriString("/users/-1/favourites").build().encode().toUri();

        ParameterizedTypeReference<CustomPageImpl<FavouriteReadDTO>> classType = new ParameterizedTypeReference<CustomPageImpl<FavouriteReadDTO>>() {
        };
        ResponseEntity<CustomPageImpl<FavouriteReadDTO>> response = client.exchange(uri, HttpMethod.GET, null,
                classType);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Page<FavouriteReadDTO> foundFavourites = favouriteService.getFavouritesByUserID(-1, null);
        assertTrue(foundFavourites.isEmpty());
    }

    @Test
    @DisplayName("POST /users/{userID}/favourites - Success")
    void addUsersIDFavourites() {
        FavouriteWriteDTO favouriteWriteDTO = new FavouriteWriteDTO(book2.getId());

        ResponseEntity<FavouriteReadDTO> response = client.postForEntity("/users/" + user2.getId() + "/favourites",
                favouriteWriteDTO, FavouriteReadDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        assertNotNull(response.getBody());

        Favourite favourite4 = new Favourite();
        favourite4.setBook(book2);
        favourite4.setUserID(user2.getId());

        FavouriteReadDTO createdFavouriteDTO = response.getBody();
        assertEquals(favouriteMapper.favouriteToReadDto(favourite4), createdFavouriteDTO);
    }

    @Test
    @DisplayName("POST /users/99/favourites - User Not Found")
    void addUsersIDFavouritesNotFound() {
        FavouriteWriteDTO favouriteWriteDTO = new FavouriteWriteDTO(book2.getId());

        ResponseEntity<FavouriteReadDTO> response = client.postForEntity("/users/99/favourites", favouriteWriteDTO,
                FavouriteReadDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        assertFalse(favouriteRepository.existsByUserIDAndBook_Id(99, book2.getId()));
    }

    @Test
    @DisplayName("POST /users/-1/favourites - Invalid ID")
    void addUsersIDFavouritesInvalid() {
        FavouriteWriteDTO favouriteWriteDTO = new FavouriteWriteDTO(book2.getId());

        ResponseEntity<FavouriteReadDTO> response = client.postForEntity("/users/-1/favourites", favouriteWriteDTO,
                FavouriteReadDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        assertFalse(favouriteRepository.existsByUserIDAndBook_Id(-1, book2.getId()));
    }

    @Test
    @DisplayName("DELETE /users/{userID}/favourites/{bookID} - Success")
    void deleteUsersIDMeFavourite() {
        ResponseEntity<Void> response = client.exchange("/users/" + user1.getId() + "/favourites/" + book1.getId(),
                HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        assertFalse(favouriteRepository.existsByUserIDAndBook_Id(user1.getId(), book1.getId()));
    }

    @Test
    @DisplayName("DELETE /users/99/favourites/{bookID} - User Not Found")
    void deleteUsersIDFavouriteNotFound() {
        ResponseEntity<Void> response = client.exchange("/users/99/favourites/" + book1.getId(), HttpMethod.DELETE,
                null, Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        assertFalse(favouriteRepository.existsByUserIDAndBook_Id(99, book1.getId()));
    }

    @Test
    @DisplayName("DELETE /users/-1/favourites/{bookID} - Invalid ID")
    void deleteUsersIDFavouriteInvalid() {
        ResponseEntity<Void> response = client.exchange("/users/-1/favourites/" + book1.getId(), HttpMethod.DELETE,
                null, Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

  @Test
  @DisplayName("GET /users/me/favourites - Success")
  void getUsersMeFavourites() {
    when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());

    URI uri = UriComponentsBuilder.fromUriString("/users/me/favourites").build().encode().toUri();

    ParameterizedTypeReference<CustomPageImpl<FavouriteReadDTO>> classType =
        new ParameterizedTypeReference<CustomPageImpl<FavouriteReadDTO>>() {};
    ResponseEntity<CustomPageImpl<FavouriteReadDTO>> response =
        client.exchange(uri, HttpMethod.GET, null, classType);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    assertNotNull(response.getBody());

    FavouriteReadDTO firstFavourite = response.getBody().getContent().getFirst();
    assertEquals(favouriteMapper.favouriteToReadDto(favourite1), firstFavourite);

    FavouriteReadDTO secondFavourite = response.getBody().getContent().getLast();
    assertEquals(favouriteMapper.favouriteToReadDto(favourite2), secondFavourite);
  }

  @Test
  @DisplayName("GET /users/me/favourites - Error")
  void getUsersMeFavouritesError() {
    when(authUtils.getUserIdFromAuthentication())
        .thenThrow(new IllegalArgumentException("Invalid Jwt token"));

    URI uri = UriComponentsBuilder.fromUriString("/users/me/favourites").build().encode().toUri();

    ParameterizedTypeReference<CustomPageImpl<FavouriteReadDTO>> classType =
        new ParameterizedTypeReference<CustomPageImpl<FavouriteReadDTO>>() {};
    ResponseEntity<CustomPageImpl<FavouriteReadDTO>> response =
        client.exchange(uri, HttpMethod.GET, null, classType);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  @DisplayName("POST /users/me/favourites - Success")
  void addUsersMeFavourites() {
    when(authUtils.getUserIdFromAuthentication()).thenReturn(user2.getId());

    FavouriteWriteDTO favouriteWriteDTO = new FavouriteWriteDTO(book2.getId());

    ResponseEntity<FavouriteReadDTO> response =
        client.postForEntity("/users/me/favourites", favouriteWriteDTO, FavouriteReadDTO.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());

    assertNotNull(response.getBody());

    Favourite favourite4 = new Favourite();
    favourite4.setBook(book2);
    favourite4.setUserID(user2.getId());

    FavouriteReadDTO createdFavouriteDTO = response.getBody();
    assertEquals(favouriteMapper.favouriteToReadDto(favourite4), createdFavouriteDTO);
  }

  @Test
  @DisplayName("POST /users/me/favourites - Error")
  void addUsersMeFavouritesError() {
    when(authUtils.getUserIdFromAuthentication())
        .thenThrow(new IllegalArgumentException("Invalid Jwt token"));

    FavouriteWriteDTO favouriteWriteDTO = new FavouriteWriteDTO(book2.getId());

    ResponseEntity<FavouriteReadDTO> response =
        client.postForEntity("/users/me/favourites", favouriteWriteDTO, FavouriteReadDTO.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  @DisplayName("DELETE /users/me/favourites/{bookID} - Success")
  void deleteUsersMeFavourite() {
    when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());

    ResponseEntity<Void> response =
        client.exchange(
            "/users/me/favourites/" + book1.getId(), HttpMethod.DELETE, null, Void.class);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

    assertFalse(favouriteRepository.existsByUserIDAndBook_Id(user1.getId(), book1.getId()));
  }

  @Test
  @DisplayName("DELETE /users/me/favourites/{bookID} - Error")
  void deleteUsersMeFavouriteError() {
    when(authUtils.getUserIdFromAuthentication())
        .thenThrow(new IllegalArgumentException("Invalid Jwt token"));

    ResponseEntity<Void> response =
        client.exchange(
            "/users/me/favourites/" + book1.getId(), HttpMethod.DELETE, null, Void.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }
}
