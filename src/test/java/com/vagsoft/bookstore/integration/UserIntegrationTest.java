package com.vagsoft.bookstore.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vagsoft.bookstore.dto.BookReadDTO;
import com.vagsoft.bookstore.dto.GenreDTO;
import com.vagsoft.bookstore.dto.UserReadDTO;
import com.vagsoft.bookstore.dto.UserUpdateDTO;
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
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    @DisplayName("PUT /users/{userID} - Success")
    void updateUserByIDFound() throws Exception {
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setUsername("jane");
        String updateUserString = objectMapper.writeValueAsString(userUpdateDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(updateUserString, headers);

        ResponseEntity<UserReadDTO> response = client.exchange("/users/" + user1.getId(), HttpMethod.PUT, request, UserReadDTO.class);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        UserReadDTO userReadDTO = response.getBody();
        assertNotNull(userReadDTO);
        assertEquals(user1.getId(), userReadDTO.getId());
        assertEquals(userUpdateDTO.getUsername(), userReadDTO.getUsername());
        assertEquals(user1.getEmail(), userReadDTO.getEmail());
        assertEquals(user1.getFirstName(), userReadDTO.getFirstName());
        assertEquals(user1.getLastName(), userReadDTO.getLastName());
        assertEquals(user1.getSignupDate(), userReadDTO.getSignupDate());


        Optional<UserReadDTO> updatedUser = userService.getUserByID(user1.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals(user1.getId(), updatedUser.get().getId());
        assertEquals(userUpdateDTO.getUsername(), updatedUser.get().getUsername());
        assertEquals(user1.getEmail(), updatedUser.get().getEmail());
        assertEquals(user1.getFirstName(), updatedUser.get().getFirstName());
        assertEquals(user1.getLastName(), updatedUser.get().getLastName());
        assertEquals(user1.getSignupDate(), updatedUser.get().getSignupDate());
    }

    @Test
    @DisplayName("PUT /users/999 - Not Found")
    void updateUserByIDNotFound() throws Exception {
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setUsername("jane");
        String updateUserString = objectMapper.writeValueAsString(userUpdateDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(updateUserString, headers);

        ResponseEntity<UserReadDTO> response = client.exchange("/users/999", HttpMethod.PUT, request, UserReadDTO.class);

        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());

        Optional<UserReadDTO> foundUser = userService.getUserByID(999);
        assertTrue(foundUser.isEmpty());
    }

    @Test
    @DisplayName("PUT /users/-1 - Invalid ID")
    void updateUserByIDInvalid() throws Exception {
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setUsername("jane");
        String updateUserString = objectMapper.writeValueAsString(userUpdateDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(updateUserString, headers);

        ResponseEntity<UserReadDTO> response = client.exchange("/users/-1", HttpMethod.PUT, request, UserReadDTO.class);

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());

        Optional<UserReadDTO> foundUser = userService.getUserByID(-1);
        assertTrue(foundUser.isEmpty());
    }

    @Test
    @DisplayName("DELETE /users/{userID} - Success")
    void deleteUserByIDFound() throws Exception {
        ResponseEntity<Void> response = client.exchange("/users/" + user1.getId(), HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());

        Optional<UserReadDTO> foundUser = userService.getUserByID(user1.getId());
        assertTrue(foundUser.isEmpty());
    }

    @Test
    @DisplayName("DELETE /users/999 - Not Found")
    void deleteUserByIDNotFound() throws Exception {
        ResponseEntity<Void> response = client.exchange("/users/999", HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());

        Optional<UserReadDTO> foundUser = userService.getUserByID(999);
        assertTrue(foundUser.isEmpty());
    }

    @Test
    @DisplayName("DELETE /users/-1 - Invalid ID")
    void deleteUserByIDInvalid() throws Exception {
        ResponseEntity<Void> response = client.exchange("/users/-1", HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());

        Optional<UserReadDTO> foundUser = userService.getUserByID(-1);
        assertTrue(foundUser.isEmpty());
    }

}
