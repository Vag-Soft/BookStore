package com.vagsoft.bookstore.services;

import java.util.Optional;

import com.vagsoft.bookstore.dto.userDTOs.UserReadDTO;
import com.vagsoft.bookstore.dto.userDTOs.UserUpdateDTO;
import com.vagsoft.bookstore.mappers.UserMapper;
import com.vagsoft.bookstore.models.entities.User;
import com.vagsoft.bookstore.models.enums.Role;
import com.vagsoft.bookstore.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service class for user operations. */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(final UserRepository userRepository, final UserMapper userMapper,
            final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieves a list of users filtered by the specified parameters.
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
    public Page<UserReadDTO> getUsers(final String username, final String email, final Role role,
            final String firstName, final String lastName, Pageable pageable) {
        return userMapper
                .pageUserToPageDto(userRepository.findUsers(username, email, role, firstName, lastName, pageable));
    }

    /**
     * Retrieves a user by its ID.
     *
     * @param userID
     *            the ID of the user to be retrieved
     * @return the retrieved user
     */
    @Transactional(readOnly = true)
    public UserReadDTO getUserByID(final Integer userID) {
        User foundUser = userRepository.getReferenceById(userID);
        return userMapper.userToReadDto(foundUser);
    }

    /**
     * Updates a user by its ID with the given user information.
     *
     * @param userID
     *            the ID of the user to be updated
     * @param userUpdateDTO
     *            the new user information
     * @return the updated user
     */
    public Optional<UserReadDTO> updateUserByID(final Integer userID, final UserUpdateDTO userUpdateDTO) {

        User foundUser = userRepository.getReferenceById(userID);

        if (userUpdateDTO.getPassword() != null) {
            String hashedPassword = passwordEncoder.encode(userUpdateDTO.getPassword());
            userUpdateDTO.setPassword(hashedPassword);
        }

        userMapper.updateUserFromDto(userUpdateDTO, foundUser);

        User updatedUser = userRepository.save(foundUser);

        return Optional.of(userMapper.userToReadDto(updatedUser));
    }

    /**
     * Deletes a user by its ID.
     *
     * @param userID
     *            the ID of the user to be deleted
     */
    @Transactional
    public void deleteUserByID(final Integer userID) {
        userRepository.deleteById(userID);
    }

    /**
     * Retrieves a user by its username.
     *
     * @param username
     *            the username of the user to be retrieved
     * @return the retrieved user
     */
    @Transactional(readOnly = true)
    public Optional<UserReadDTO> getUserByUsername(final String username) {
        Optional<User> foundUser = userRepository.findByUsername(username);

        return foundUser.map(userMapper::userToReadDto);
    }
}
