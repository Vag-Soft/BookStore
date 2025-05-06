package com.vagsoft.bookstore.controllers;

import com.vagsoft.bookstore.annotations.NullOrNotBlank;
import com.vagsoft.bookstore.dto.BookReadDTO;
import com.vagsoft.bookstore.dto.UserReadDTO;
import com.vagsoft.bookstore.models.enums.Role;
import com.vagsoft.bookstore.services.BookService;
import com.vagsoft.bookstore.services.UserService;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for endpoints related to users
 */
@RestController
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
    @GetMapping
    public ResponseEntity<Page<UserReadDTO>> getUsers(
            @RequestParam(name="username", required=false) @NullOrNotBlank String username,
            @RequestParam(name="email", required=false) @NullOrNotBlank String email,
            @RequestParam(name="role", required=false) Role role,
            @RequestParam(name="firstName", required=false) @NullOrNotBlank String firstName,
            @RequestParam(name="lastName", required=false) @NullOrNotBlank String lastName,
            Pageable pageable) {
        log.info("GET /users: username={}, email={}, role={}, firstName={}, lastName={}, pageable={}", username, email, role, firstName, lastName, pageable);

        return ResponseEntity.ok(userService.getUsers(username, email, role, firstName, lastName, pageable));
    }
}
