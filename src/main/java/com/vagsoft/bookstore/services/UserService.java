package com.vagsoft.bookstore.services;

import com.vagsoft.bookstore.annotations.NullOrNotBlank;
import com.vagsoft.bookstore.dto.BookReadDTO;
import com.vagsoft.bookstore.dto.UserReadDTO;
import com.vagsoft.bookstore.mappers.UserMapper;
import com.vagsoft.bookstore.models.Book;
import com.vagsoft.bookstore.models.User;
import com.vagsoft.bookstore.models.enums.Role;
import com.vagsoft.bookstore.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * Service class for user operations
 */
@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRespository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRespository, UserMapper userMapper) {
        this.userRespository = userRespository;
        this.userMapper = userMapper;
    }

    /**
     * Retrieves a list of users filtered by the specified parameters
     *
     * @param username the username of the users to search for (optional)
     * @param email the email of the users to search for (optional)
     * @param role the role of the users to search for (optional)
     * @param firstName the first name of the users to search for (optional)
     * @param lastName the last name of the users to search for (optional)
     * @param pageable the pagination information (optional)
     * @return a page of users
     */
    @Transactional(readOnly = true)
    public Page<UserReadDTO> getUsers(String username, String email, Role role, String firstName, String lastName, Pageable pageable) {
        return userMapper.PageUserToPageDto(userRespository.findUsers(username, email, role, firstName, lastName, pageable));
    }


    /**
     * Retrieves a user by its ID
     *
     * @param userID the ID of the user to be retrieved
     * @return the retrieved user
     */
    @Transactional(readOnly = true)
    public Optional<UserReadDTO> getUserByID(Integer userID) {
        Optional<User> foundUser = userRespository.findById(userID);
        return foundUser.map(userMapper::UserToReadDto);
    }
}
