package com.vagsoft.bookstore.unit.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vagsoft.bookstore.controllers.UserController;
import com.vagsoft.bookstore.dto.userDTOs.UserReadDTO;
import com.vagsoft.bookstore.dto.userDTOs.UserUpdateDTO;
import com.vagsoft.bookstore.models.enums.Role;
import com.vagsoft.bookstore.repositories.UserRepository;
import com.vagsoft.bookstore.services.UserService;
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

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ActiveProfiles("test")
class UserControllerTest {
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private AuthUtils authUtils;

    private List<UserReadDTO> storedUsers;

    @BeforeEach
    void setUp() {
        storedUsers = new ArrayList<>();
        storedUsers.add(new UserReadDTO(1, "jane.smith@example.com", "janesmith", Role.USER, "Jane", "Smith",
                LocalDate.parse("2022-01-05")));
        storedUsers.add(new UserReadDTO(2, "bob.johnson@example.com", "bobjohnson", Role.ADMIN, "Bob", "Johnson",
                LocalDate.parse("2022-01-10")));
    }

    @Test
    @DisplayName("GET /users - Success No Filters")
    void getUsersNoFilters() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        Page<UserReadDTO> page = new PageImpl<>(storedUsers, pageable, storedUsers.size());

        when(userService.getUsers(null, null, null, null, null, pageable)).thenReturn(page);

        mockMvc.perform(get("/users").param("page", "0").param("size", "20").accept("application/json"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.content", hasSize(2))).andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].email").value("jane.smith@example.com"))
                .andExpect(jsonPath("$.content[0].username").value("janesmith"))
                .andExpect(jsonPath("$.content[0].role").value("USER"))
                .andExpect(jsonPath("$.content[0].firstName").value("Jane"))
                .andExpect(jsonPath("$.content[0].lastName").value("Smith"))
                .andExpect(jsonPath("$.content[0].signupDate").value("2022-01-05"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].email").value("bob.johnson@example.com"))
                .andExpect(jsonPath("$.content[1].username").value("bobjohnson"))
                .andExpect(jsonPath("$.content[1].role").value("ADMIN"))
                .andExpect(jsonPath("$.content[1].firstName").value("Bob"))
                .andExpect(jsonPath("$.content[1].lastName").value("Johnson"))
                .andExpect(jsonPath("$.content[1].signupDate").value("2022-01-10"));
    }

    @Test
    @DisplayName("GET /users - Success With Filters")
    void getUsersWithFilters() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        Page<UserReadDTO> page = new PageImpl<>(storedUsers.subList(0, 1), pageable, storedUsers.size());

        when(userService.getUsers("janesmi", "jane.smith@", Role.USER, null, null, pageable)).thenReturn(page);

        mockMvc.perform(get("/users").param("username", "janesmi").param("email", "jane.smith@").param("role", "USER")
                .param("page", "0").param("size", "20").accept("application/json")).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.content", hasSize(1))).andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].email").value("jane.smith@example.com"))
                .andExpect(jsonPath("$.content[0].username").value("janesmith"))
                .andExpect(jsonPath("$.content[0].role").value("USER"))
                .andExpect(jsonPath("$.content[0].firstName").value("Jane"))
                .andExpect(jsonPath("$.content[0].lastName").value("Smith"))
                .andExpect(jsonPath("$.content[0].signupDate").value("2022-01-05"));
    }

    @Test
    @DisplayName("GET /users/1 - Success")
    void getUserByIDFound() throws Exception {
        UserReadDTO userOutput = storedUsers.getFirst();

        when(userRepository.existsById(1)).thenReturn(true);
        when(userService.getUserByID(1)).thenReturn(userOutput);

        assertNotNull(userOutput);
        mockMvc.perform(get("/users/{userID}", 1)).andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(userOutput.getId()))
                .andExpect(jsonPath("$.username").value(userOutput.getUsername()))
                .andExpect(jsonPath("$.email").value(userOutput.getEmail()))
                .andExpect(jsonPath("$.role").value(userOutput.getRole().toString()))
                .andExpect(jsonPath("$.firstName").value(userOutput.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(userOutput.getLastName()))
                .andExpect(jsonPath("$.signupDate").value(userOutput.getSignupDate().toString()));
    }

    @Test
    @DisplayName("GET /users/999} - Not Found")
    void getUserByIDNotFound() throws Exception {
        when(userRepository.existsById(999)).thenReturn(false);
        mockMvc.perform(get("/users/{userID}", 999)).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /users/-1} - Invalid ID")
    void getUserByIDInvalid() throws Exception {
        when(userRepository.existsById(-1)).thenReturn(false);
        mockMvc.perform(get("/users/{userID}", -1)).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /users/1 - Success")
    void updateUserByIDFound() throws Exception {

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setUsername("jane");

        UserReadDTO userOutput = storedUsers.getFirst();
        userOutput.setUsername(userUpdateDTO.getUsername());

        when(userService.updateUserByID(1, userUpdateDTO)).thenReturn(Optional.of(userOutput));
        when(userRepository.existsById(1)).thenReturn(true);
        when(userRepository.existsByUsernameAndIdNot("jane", 1)).thenReturn(false);

        mockMvc.perform(put("/users/{userID}", 1).contentType("application/json")
                .content(objectMapper.writeValueAsString(userUpdateDTO))).andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(userOutput.getId()))
                .andExpect(jsonPath("$.username").value(userUpdateDTO.getUsername()))
                .andExpect(jsonPath("$.email").value(userOutput.getEmail()))
                .andExpect(jsonPath("$.firstName").value(userOutput.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(userOutput.getLastName()))
                .andExpect(jsonPath("$.signupDate").value(userOutput.getSignupDate().toString()));
    }

    @Test
    @DisplayName("PUT /users/999 - Not Found")
    void updateUserByIDNotFound() throws Exception {
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setUsername("jane");

        when(userService.updateUserByID(999, userUpdateDTO)).thenReturn(Optional.empty());
        when(userRepository.existsById(999)).thenReturn(false);
        when(userRepository.existsByUsernameAndIdNot("jane", 999)).thenReturn(false);

        mockMvc.perform(put("/users/{userID}", 999).contentType("application/json")
                .content(objectMapper.writeValueAsString(userUpdateDTO))).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /users/-1 - Invalid ID")
    void updateUserByIDInvalid() throws Exception {
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setUsername("jane");

        when(userRepository.existsById(-1)).thenReturn(false);

        mockMvc.perform(put("/users/{userID}", -1).contentType("application/json")
                .content(objectMapper.writeValueAsString(userUpdateDTO))).andExpect(status().isBadRequest());
    }

  @Test
  @DisplayName("DELETE /users/1 - Success")
  void deleteUserByIDFound() throws Exception {
    when(userRepository.existsById(1)).thenReturn(true);
    doNothing().when(userService).deleteUserByID(1);

    mockMvc.perform(delete("/users/{userID}", 1)).andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /users/999 - Not Found")
  void deleteUserByIDNotFound() throws Exception {
    when(userRepository.existsById(999)).thenReturn(false);
    doNothing().when(userService).deleteUserByID( 999);

    mockMvc.perform(delete("/users/{userID}", 999)).andExpect(status().isNotFound());
  }

    @Test
    @DisplayName("DELETE /users/-1 - Invalid ID")
    void deleteUserByIDInvalid() throws Exception {
        when(userRepository.existsById(-1)).thenReturn(false);
        mockMvc.perform(delete("/users/{userID}", -1)).andExpect(status().isBadRequest());
    }
}
