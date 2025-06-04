package com.vagsoft.bookstore.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.time.LocalDate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vagsoft.bookstore.dto.userDTOs.UserReadDTO;
import com.vagsoft.bookstore.dto.userDTOs.UserUpdateDTO;
import com.vagsoft.bookstore.mappers.UserMapper;
import com.vagsoft.bookstore.models.entities.User;
import com.vagsoft.bookstore.models.enums.Role;
import com.vagsoft.bookstore.pagination.CustomPageImpl;
import com.vagsoft.bookstore.repositories.UserRepository;
import com.vagsoft.bookstore.services.UserService;
import com.vagsoft.bookstore.utils.AuthUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
    @MockitoBean
    private AuthUtils authUtils;

    User user1, user2;

    @BeforeEach
    public void setUp() {
        user1 = new User("jane.smith@example.com", "janesmith", "hashed_password_value", Role.USER, "Jane", "Smith",
                LocalDate.parse("2022-01-05"));
        user2 = new User("bob.johnson@example.com", "bobjohnson", "hashed_password_value", Role.ADMIN, "Bob", "Johnson",
                LocalDate.parse("2022-01-10"));

        userRepository.save(user1);
        userRepository.save(user2);
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /users - Success No Filters")
    void getUsersNoFilters() {
        URI uri = UriComponentsBuilder.fromUriString("/users").build().encode().toUri();

        ParameterizedTypeReference<CustomPageImpl<UserReadDTO>> classType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<CustomPageImpl<UserReadDTO>> response = client.exchange(uri, HttpMethod.GET, null, classType);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getContent().size());

        UserReadDTO firstUser = response.getBody().getContent().get(0);
        assertEquals(userMapper.userToReadDto(user1), firstUser);

        UserReadDTO secondUser = response.getBody().getContent().get(1);
        assertEquals(userMapper.userToReadDto(user2), secondUser);
    }

    @Test
    @DisplayName("GET /users - Success With Filters")
    void getUsersWithFilters() {
        URI uri = UriComponentsBuilder.fromUriString("/users").queryParam("email", "johnson@")
                .queryParam("role", "ADMIN").queryParam("page", 0).queryParam("size", 20).build().encode().toUri();

        ParameterizedTypeReference<CustomPageImpl<UserReadDTO>> classType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<CustomPageImpl<UserReadDTO>> response = client.exchange(uri, HttpMethod.GET, null, classType);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());

        UserReadDTO secondUser = response.getBody().getContent().getFirst();
        assertEquals(userMapper.userToReadDto(user2), secondUser);
    }

    @Test
    @DisplayName("GET /users/{userID} - Success")
    void getUserByIDFound() {
        ResponseEntity<UserReadDTO> response = client.getForEntity("/users/" + user1.getId(), UserReadDTO.class);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userMapper.userToReadDto(user1), response.getBody());
    }

    @Test
    @DisplayName("GET /users/999 - Not Found")
    void getUserByIDNotFound() {
        ResponseEntity<UserReadDTO> response = client.getForEntity("/users/999", UserReadDTO.class);

        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }

    @Test
    @DisplayName("GET /users/-1 - Invalid ID")
    void getUserByIDInvalid() {
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

        ResponseEntity<UserReadDTO> response = client.exchange("/users/" + user1.getId(), HttpMethod.PUT, request,
                UserReadDTO.class);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        UserReadDTO userReadDTO = response.getBody();
        assertNotNull(userReadDTO);
        assertEquals(user1.getId(), userReadDTO.getId());
        assertEquals(userUpdateDTO.getUsername(), userReadDTO.getUsername());
        assertEquals(user1.getEmail(), userReadDTO.getEmail());
        assertEquals(user1.getFirstName(), userReadDTO.getFirstName());
        assertEquals(user1.getLastName(), userReadDTO.getLastName());
        assertEquals(user1.getSignupDate(), userReadDTO.getSignupDate());

        UserReadDTO updatedUser = userService.getUserByID(user1.getId());
        assertNotNull(updatedUser);
        assertEquals(user1.getId(), updatedUser.getId());
        assertEquals(userUpdateDTO.getUsername(), updatedUser.getUsername());
        assertEquals(user1.getEmail(), updatedUser.getEmail());
        assertEquals(user1.getFirstName(), updatedUser.getFirstName());
        assertEquals(user1.getLastName(), updatedUser.getLastName());
        assertEquals(user1.getSignupDate(), updatedUser.getSignupDate());
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

        ResponseEntity<UserReadDTO> response = client.exchange("/users/999", HttpMethod.PUT, request,
                UserReadDTO.class);

        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());

        assertFalse(userRepository.existsById(999));
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

        assertFalse(userRepository.existsById(-1));
    }

    @Test
    @DisplayName("DELETE /users/{userID} - Success")
    void deleteUserByIDFound() {
        ResponseEntity<Void> response = client.exchange("/users/" + user1.getId(), HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());

        assertFalse(userRepository.existsById(user1.getId()));
    }

    @Test
    @DisplayName("DELETE /users/999 - Not Found")
    void deleteUserByIDNotFound() {
        ResponseEntity<Void> response = client.exchange("/users/999", HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());

        assertFalse(userRepository.existsById(999));
    }

    @Test
    @DisplayName("DELETE /users/-1 - Invalid ID")
    void deleteUserByIDInvalid() {
        ResponseEntity<Void> response = client.exchange("/users/-1", HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());

        assertFalse(userRepository.existsById(-1));
    }

    @Test
    @DisplayName("GET /users/me - Success")
    void getUsersMe() {
        when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());
        ResponseEntity<UserReadDTO> response = client.getForEntity("/users/me", UserReadDTO.class);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userMapper.userToReadDto(user1), response.getBody());
    }

    @Test
    @DisplayName("GET /users/me - Error JWT")
    void getUsersMeError() {
        when(authUtils.getUserIdFromAuthentication())
                .thenThrow(new IllegalArgumentException("Invalid Jwt token"));
        ResponseEntity<UserReadDTO> response = client.getForEntity("/users/me", UserReadDTO.class);

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }

    @Test
    @DisplayName("PUT /users/me - Success")
    void updateUserMe() throws Exception {
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setUsername("jane");
        String updateUserString = objectMapper.writeValueAsString(userUpdateDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(updateUserString, headers);

        lenient().when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());

        ResponseEntity<UserReadDTO> response = client.exchange("/users/me", HttpMethod.PUT, request, UserReadDTO.class);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        UserReadDTO userReadDTO = response.getBody();
        assertNotNull(userReadDTO);
        assertEquals(user1.getId(), userReadDTO.getId());
        assertEquals(userUpdateDTO.getUsername(), userReadDTO.getUsername());
        assertEquals(user1.getEmail(), userReadDTO.getEmail());
        assertEquals(user1.getFirstName(), userReadDTO.getFirstName());
        assertEquals(user1.getLastName(), userReadDTO.getLastName());
        assertEquals(user1.getSignupDate(), userReadDTO.getSignupDate());

        UserReadDTO updatedUser = userService.getUserByID(user1.getId());
        assertNotNull(updatedUser);
        assertEquals(user1.getId(), updatedUser.getId());
        assertEquals(userUpdateDTO.getUsername(), updatedUser.getUsername());
        assertEquals(user1.getEmail(), updatedUser.getEmail());
        assertEquals(user1.getFirstName(), updatedUser.getFirstName());
        assertEquals(user1.getLastName(), updatedUser.getLastName());
        assertEquals(user1.getSignupDate(), updatedUser.getSignupDate());
    }

    @Test
    @DisplayName("PUT /users/me - Error JWT")
    void updateUserMeError() throws Exception {
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setUsername("jane");
        String updateUserString = objectMapper.writeValueAsString(userUpdateDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(updateUserString, headers);

        lenient().when(authUtils.getUserIdFromAuthentication())
                .thenThrow(new IllegalArgumentException("Invalid Jwt token"));

        ResponseEntity<UserReadDTO> response = client.exchange("/users/me", HttpMethod.PUT, request, UserReadDTO.class);

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /users/me - Success")
    void deleteUserMe() {
        lenient().when(authUtils.getUserIdFromAuthentication()).thenReturn(user1.getId());

        ResponseEntity<Void> response = client.exchange("/users/me", HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());

        assertFalse(userRepository.existsById(user1.getId()));
    }

    @Test
    @DisplayName("DELETE /users/me - Error JWT")
    void deleteUserMeError() {
        lenient().when(authUtils.getUserIdFromAuthentication())
                .thenThrow(new IllegalArgumentException("Invalid Jwt token"));

        ResponseEntity<Void> response = client.exchange("/users/me", HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }
}
