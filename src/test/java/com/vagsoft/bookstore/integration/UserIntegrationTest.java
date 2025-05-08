package com.vagsoft.bookstore.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vagsoft.bookstore.dto.BookReadDTO;
import com.vagsoft.bookstore.dto.GenreDTO;
import com.vagsoft.bookstore.dto.UserReadDTO;
import com.vagsoft.bookstore.mappers.UserMapper;
import com.vagsoft.bookstore.models.User;
import com.vagsoft.bookstore.models.enums.Role;
import com.vagsoft.bookstore.pagination.CustomPageImpl;
import com.vagsoft.bookstore.repositories.BookRepository;
import com.vagsoft.bookstore.repositories.UserRepository;
import com.vagsoft.bookstore.services.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ActiveProfiles("test")
public class UserIntegrationTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TestRestTemplate client;

    User user1, user2;
    @BeforeEach
    public void setUp() {
        user1 = new User("jane.smith@example.com", "janesmith", "hashed_password_value", Role.USER, "Jane", "Smith", LocalDate.parse("2022-01-05"));
        user2 = new User("bob.johnson@example.com", "bobjohnson", "hashed_password_value", Role.ADMIN, "Bob", "Johnson", LocalDate.parse("2022-01-10"));

        userRepository.save(user1);
        userRepository.save(user2);
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /users - Success No Filters")
    void getUsersNoFilters() throws Exception {
        URI uri = UriComponentsBuilder.fromUriString("/users")
                .build().encode().toUri();

        ParameterizedTypeReference<CustomPageImpl<UserReadDTO>> classType = new ParameterizedTypeReference<CustomPageImpl<UserReadDTO>>() {};
        ResponseEntity<CustomPageImpl<UserReadDTO>> response = client.exchange(uri, HttpMethod.GET, null, classType);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getContent().size());

        UserReadDTO firstUser = response.getBody().getContent().get(0);
        assertEquals(userMapper.UserToReadDto(user1), firstUser);

        UserReadDTO secondUser = response.getBody().getContent().get(1);
        assertEquals(userMapper.UserToReadDto(user2), secondUser);
    }


    @Test
    @DisplayName("GET /users - Success With Filters")
    void getUsersWithFilters() throws Exception {
        URI uri = UriComponentsBuilder.fromUriString("/users")
                .queryParam("email", "johnson@")
                .queryParam("role", "ADMIN")
                .queryParam("page", 0)
                .queryParam("size", 20)
                .build().encode().toUri();

        ParameterizedTypeReference<CustomPageImpl<UserReadDTO>> classType = new ParameterizedTypeReference<CustomPageImpl<UserReadDTO>>() {};
        ResponseEntity<CustomPageImpl<UserReadDTO>> response = client.exchange(uri, HttpMethod.GET, null, classType);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());

        UserReadDTO secondUser = response.getBody().getContent().getFirst();
        assertEquals(userMapper.UserToReadDto(user2), secondUser);
    }

    @Test
    @DisplayName("GET /users/{userID} - Success")
    void getUserByIDFound() throws Exception {
        ResponseEntity<UserReadDTO> response = client.getForEntity("/users/" + user1.getId(), UserReadDTO.class);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userMapper.UserToReadDto(user1), response.getBody());
    }

    @Test
    @DisplayName("GET /users/999 - Not Found")
    void getUserByIDNotFound() throws Exception {
        ResponseEntity<UserReadDTO> response = client.getForEntity("/users/999", UserReadDTO.class);

        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }

    @Test
    @DisplayName("GET /users/-1 - Invalid ID")
    void getUserByIDInvalid() throws Exception {
        ResponseEntity<UserReadDTO> response = client.getForEntity("/users/-1", UserReadDTO.class);

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }
}
