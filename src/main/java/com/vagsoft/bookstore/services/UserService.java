package com.vagsoft.bookstore.services;

import java.util.Optional;

import com.vagsoft.bookstore.dto.UserReadDTO;
import com.vagsoft.bookstore.dto.UserUpdateDTO;
import com.vagsoft.bookstore.errors.exceptions.UserNotFoundException;
import com.vagsoft.bookstore.mappers.UserMapper;
import com.vagsoft.bookstore.models.entities.User;
import com.vagsoft.bookstore.models.enums.Role;
import com.vagsoft.bookstore.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service class for user operations */
@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieves a list of users filtered by the specified parameters
     *
     * @param username
     *            the username of the users to search for (optional)
     * @param email
     *            the email of the users to search for (optional)
     * @param role
     *            the role of the users to search for (optional)
     * @param firstName
     *            the first name of the users to search for (optional)
     * @param lastName
     *            the last name of the users to search for (optional)
     * @param pageable
     *            the pagination information (optional)
     * @return a page of users
     */
    @Transactional(readOnly = true)
    public Page<UserReadDTO> getUsers(String username, String email, Role role, String firstName, String lastName,
            Pageable pageable) {
        return userMapper
                .pageUserToPageDto(userRepository.findUsers(username, email, role, firstName, lastName, pageable));
    }

    /**
     * Retrieves a user by its ID
     *
     * @param userID
     *            the ID of the user to be retrieved
     * @return the retrieved user
     */
    @Transactional(readOnly = true)
    public Optional<UserReadDTO> getUserByID(Integer userID) {
        Optional<User> foundUser = userRepository.findById(userID);
        return foundUser.map(userMapper::UserToReadDto);
    }

    /**
     * Updates a user by its ID with the given user information
     *
     * @param userID
     *            the ID of the user to be updated
     * @param userUpdateDTO
     *            the new user information
     * @return the updated user
     */
    public Optional<UserReadDTO> updateUserByID(Integer userID, UserUpdateDTO userUpdateDTO) {

        User foundUser = userRepository.findById(userID)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userID + " not found"));

        if (userUpdateDTO.getPassword() != null) {
            String hashedPassword = passwordEncoder.encode(userUpdateDTO.getPassword());
            userUpdateDTO.setPassword(hashedPassword);
        }

        userMapper.updateUserFromDto(userUpdateDTO, foundUser);

        User updatedUser = userRepository.save(foundUser);

        return Optional.of(userMapper.UserToReadDto(updatedUser));
    }

    /**
     * Deletes a user by its ID
     *
     * @param userID
     *            the ID of the user to be deleted
     */
    @Transactional
    public void deleteUserByID(Integer userID) {
        userRepository.deleteById(userID);
    }

    /**
     * Retrieves a user by its username
     *
     * @param username
     *            the username of the user to be retrieved
     * @return the retrieved user
     */
    @Transactional(readOnly = true)
    public Optional<UserReadDTO> getUserByUsername(String username) {
        Optional<User> foundUser = userRepository.findByUsername(username);

        return foundUser.map(userMapper::UserToReadDto);
    }
}
