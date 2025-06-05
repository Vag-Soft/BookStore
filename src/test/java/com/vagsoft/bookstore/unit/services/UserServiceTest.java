package com.vagsoft.bookstore.unit.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.vagsoft.bookstore.dto.userDTOs.UserReadDTO;
import com.vagsoft.bookstore.dto.userDTOs.UserUpdateDTO;
import com.vagsoft.bookstore.mappers.UserMapper;
import com.vagsoft.bookstore.models.entities.User;
import com.vagsoft.bookstore.models.enums.Role;
import com.vagsoft.bookstore.repositories.UserRepository;
import com.vagsoft.bookstore.services.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

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
        storedUsers.add(new User(1, "jane.smith@example.com", "janesmith", "hashed_password_value", Role.USER, "Jane",
                "Smith", LocalDate.parse("2022-01-05")));
        storedUsers.add(new User(2, "bob.johnson@example.com", "bobjohnson", "hashed_password_value", Role.ADMIN, "Bob",
                "Johnson", LocalDate.parse("2022-01-10")));
    }

    @Test
    @DisplayName("getUsers() - Success No Filters")
    void getUsersNoFilters() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<User> page = new PageImpl<>(storedUsers, pageable, storedUsers.size());

        when(userRepository.findUsers(null, null, null, null, null, pageable)).thenReturn(page);

        Page<UserReadDTO> result = userService.getUsers(null, null, null, null, null, pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(userMapper.userToReadDto(storedUsers.get(0)), result.getContent().get(0));
        assertEquals(userMapper.userToReadDto(storedUsers.get(1)), result.getContent().get(1));
    }

    @Test
    @DisplayName("getUsers() - Success With Filters")
    void getUsersWithFilters() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<User> page = new PageImpl<>(storedUsers.subList(0, 1), pageable, storedUsers.size());

        when(userRepository.findUsers(null, null, Role.USER, null, null, pageable)).thenReturn(page);

        Page<UserReadDTO> result = userService.getUsers(null, null, Role.USER, null, null, pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(userMapper.userToReadDto(storedUsers.getFirst()), result.getContent().getFirst());
    }

    @Test
    @DisplayName("getUserByID(1) - Success")
    void getUserByIDFound() {
        when(userRepository.existsById(1)).thenReturn(true);
        when(userRepository.getReferenceById(1)).thenReturn(storedUsers.getFirst());

        UserReadDTO result = userService.getUserByID(1);
        assertNotNull(result);
        assertEquals(userMapper.userToReadDto(storedUsers.getFirst()), result);
    }

    @Test
    @DisplayName("updateUserByID(1) - Success")
    void updateUserByIDFound() {
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setUsername("jane");

        when(userRepository.existsById(1)).thenReturn(true);
        when(userRepository.getReferenceById(1)).thenReturn(storedUsers.getFirst());

        User updatedUser = storedUsers.getFirst();
        updatedUser.setUsername(userUpdateDTO.getUsername());

        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        Optional<UserReadDTO> result = userService.updateUserByID(1, userUpdateDTO);
        assertFalse(result.isEmpty());
        assertEquals(userMapper.userToReadDto(updatedUser), result.get());
    }

    @Test
    @DisplayName("deleteUserByID(1) - Success")
    void deletedUserByIDFound() {
        when(userRepository.existsById(1)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1);

        userService.deleteUserByID(1);

        verify(userRepository).deleteById(1);
    }
}
