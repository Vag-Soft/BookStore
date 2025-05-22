package com.vagsoft.bookstore.unit.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.vagsoft.bookstore.dto.UserReadDTO;
import com.vagsoft.bookstore.dto.UserUpdateDTO;
import com.vagsoft.bookstore.errors.exceptions.UserNotFoundException;
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

  @Test
  @DisplayName("getUserByID(1) - Success")
  void getUserByIDFound() {
    when(userRepository.findById(1)).thenReturn(Optional.of(storedUsers.getFirst()));

    Optional<UserReadDTO> result = userService.getUserByID(1);

    assertFalse(result.isEmpty());
    assertEquals(userMapper.UserToReadDto(storedUsers.getFirst()), result.get());
  }

  @Test
  @DisplayName("getUserByID(999) - Not Found")
  void getUserByIDNotFound() {
    when(userRepository.findById(999)).thenReturn(Optional.empty());

    Optional<UserReadDTO> result = userService.getUserByID(999);

    assertTrue(result.isEmpty());
  }

    @Test
    @DisplayName("getUserByID(-1) - Invalid ID")
    void getUserByIDInvalid() {
        Optional<UserReadDTO> result = userService.getUserByID(-1);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("updateUserByID(1) - Success")
    void updateUserByIDFound() {
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setUsername("jane");

        when(userRepository.findById(1)).thenReturn(Optional.of(storedUsers.getFirst()));

        User updatedUser = storedUsers.getFirst();
        updatedUser.setUsername(userUpdateDTO.getUsername());

        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        Optional<UserReadDTO> result = userService.updateUserByID(1, userUpdateDTO);
        assertFalse(result.isEmpty());
        assertEquals(userMapper.UserToReadDto(updatedUser), result.get());
    }

    @Test
    @DisplayName("updateUserByID(999) - Not Found")
    void updateUserByIDNotFound() {
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setUsername("jane");

        assertThrows(UserNotFoundException.class, () -> userService.updateUserByID(999, userUpdateDTO));
    }

    @Test
    @DisplayName("updateUserByID(-1) - Invalid ID")
    void updateUserByIDInvalid() {
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setUsername("jane");

        assertThrows(UserNotFoundException.class, () -> userService.updateUserByID(-1, userUpdateDTO));
    }

  @Test
  @DisplayName("deleteUserByID(1) - Success")
  void deletedUserByIDFound() {
    when(userRepository.deleteById(1L)).thenReturn(1L);

    Long deletedUsers = userService.deleteUserByID(1L);

    assertEquals(1, deletedUsers);
  }

  @Test
  @DisplayName("deleteUserByID(999) - Not Found")
  void deletedUserByIDNotFound() {
    when(userRepository.deleteById(999L)).thenReturn(0L);

    Long deletedUsers = userService.deleteUserByID(999L);

    assertEquals(0, deletedUsers);
  }

  @Test
  @DisplayName("deleteUserByID(-1) - Invalid ID")
  void deletedUserByIDInvalid() {
    when(userRepository.deleteById(-1L)).thenReturn(0L);

    Long deletedUsers = userService.deleteUserByID(-1L);

    assertEquals(0, deletedUsers);
  }
}
