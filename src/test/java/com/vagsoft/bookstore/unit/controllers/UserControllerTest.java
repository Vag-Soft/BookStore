package com.vagsoft.bookstore.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vagsoft.bookstore.controllers.BookController;
import com.vagsoft.bookstore.controllers.UserController;
import com.vagsoft.bookstore.dto.BookReadDTO;
import com.vagsoft.bookstore.dto.GenreDTO;
import com.vagsoft.bookstore.dto.UserReadDTO;
import com.vagsoft.bookstore.models.enums.Role;
import com.vagsoft.bookstore.services.BookService;
import com.vagsoft.bookstore.services.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ActiveProfiles("test")
class UserControllerTest {
    @MockitoBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private List<UserReadDTO> storedUsers;
    @BeforeEach
    void setUp() {
        storedUsers = new ArrayList<>();
        storedUsers.add(new UserReadDTO(1, "jane.smith@example.com", "janesmith", "hashed_password_value", Role.USER, "Jane", "Smith", LocalDate.parse("2022-01-05")));
        storedUsers.add(new UserReadDTO(2, "bob.johnson@example.com", "bobjohnson", "hashed_password_value", Role.ADMIN, "Bob", "Johnson", LocalDate.parse("2022-01-10")));
    }

    @Test
    @DisplayName("GET /users - Success No Filters")
    void getUsersNoFilters() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        Page<UserReadDTO> page = new PageImpl<>(storedUsers, pageable, storedUsers.size());

        when(userService.getUsers(null, null, null, null, null, pageable)).thenReturn(page);

        mockMvc.perform(get("/users")
                .param("page", "0")
                .param("size", "20")
                .accept("application/json"))

                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))

                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(1))
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

        mockMvc.perform(get("/users")
                        .param("username", "janesmi")
                        .param("email", "jane.smith@")
                        .param("role", "USER")
                        .param("page", "0")
                        .param("size", "20")
                        .accept("application/json"))

                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))

                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].email").value("jane.smith@example.com"))
                .andExpect(jsonPath("$.content[0].username").value("janesmith"))
                .andExpect(jsonPath("$.content[0].role").value("USER"))
                .andExpect(jsonPath("$.content[0].firstName").value("Jane"))
                .andExpect(jsonPath("$.content[0].lastName").value("Smith"))
                .andExpect(jsonPath("$.content[0].signupDate").value("2022-01-05"));
    }
}