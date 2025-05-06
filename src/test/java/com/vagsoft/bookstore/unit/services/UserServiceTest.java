package com.vagsoft.bookstore.unit.services;

import com.vagsoft.bookstore.dto.UserReadDTO;
import com.vagsoft.bookstore.mappers.BookMapper;
import com.vagsoft.bookstore.mappers.UserMapper;
import com.vagsoft.bookstore.models.Book;
import com.vagsoft.bookstore.models.Genre;
import com.vagsoft.bookstore.models.User;
import com.vagsoft.bookstore.models.enums.Role;
import com.vagsoft.bookstore.repositories.BookRepository;
import com.vagsoft.bookstore.repositories.UserRepository;
import com.vagsoft.bookstore.services.BookService;
import com.vagsoft.bookstore.services.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ActiveProfiles("test")
class UserServiceTest {
    @MockitoBean
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    private List<User> storedUsers;
    @BeforeEach
    void setUp() {
        storedUsers = new ArrayList<>();
        storedUsers.add(new User(1, "jane.smith@example.com", "janesmith", "hashed_password_value", Role.USER, "Jane", "Smith", LocalDate.parse("2022-01-05")));
        storedUsers.add(new User(2, "bob.johnson@example.com", "bobjohnson", "hashed_password_value", Role.ADMIN, "Bob", "Johnson", LocalDate.parse("2022-01-10")));
    }

    @Test
    @DisplayName("getUsers() - Success No Filters")
    void getUsersNoFilters() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<User> page = new PageImpl<>(storedUsers, pageable, storedUsers.size());

        when(userRepository.findUsers(null, null, null, null, null, pageable)).thenReturn(page);

        Page<UserReadDTO> result = userService.getUsers(null, null, null, null, null, pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(userMapper.UserToReadDto(storedUsers.get(0)), result.getContent().get(0));
        assertEquals(userMapper.UserToReadDto(storedUsers.get(1)), result.getContent().get(1));
    }

    @Test
    @DisplayName("getUsers() - Success With Filters")
    void getUsersWithFilters() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<User> page = new PageImpl<>(storedUsers.subList(0, 1), pageable, storedUsers.size());

        when(userRepository.findUsers(null, null, Role.USER, null, null, pageable)).thenReturn(page);

        Page<UserReadDTO> result = userService.getUsers(null, null, Role.USER, null, null, pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(userMapper.UserToReadDto(storedUsers.getFirst()), result.getContent().getFirst());
    }
}