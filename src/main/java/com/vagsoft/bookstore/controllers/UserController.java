package com.vagsoft.bookstore.controllers;

import com.vagsoft.bookstore.annotations.IsAdmin;
import com.vagsoft.bookstore.annotations.NullOrNotBlank;
import com.vagsoft.bookstore.dto.UserReadDTO;
import com.vagsoft.bookstore.dto.UserUpdateDTO;
import com.vagsoft.bookstore.errors.exceptions.UserNotFoundException;
import com.vagsoft.bookstore.models.enums.Role;
import com.vagsoft.bookstore.services.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
    @IsAdmin()
    @GetMapping
    public ResponseEntity<Page<UserReadDTO>> getUsers(
            @RequestParam(name="username", required=false) @Size(max = 31, message = "username must be less than 32 characters") @NullOrNotBlank String username,
            @RequestParam(name="email", required=false) @Size(max = 320, message = "email must be less than 321 characters") @NullOrNotBlank String email,
            @RequestParam(name="role", required=false) Role role,
            @RequestParam(name="firstName", required=false) @Size(max = 31, message = "firstName must be less than 32 characters") @NullOrNotBlank String firstName,
            @RequestParam(name="lastName", required=false) @Size(max = 31, message = "lastName must be less than 32 characters") @NullOrNotBlank String lastName,
            Pageable pageable) {
        log.info("GET /users: username={}, email={}, role={}, firstName={}, lastName={}, pageable={}", username, email, role, firstName, lastName, pageable);

        return ResponseEntity.ok(userService.getUsers(username, email, role, firstName, lastName, pageable));
    }

    /**
     * Retrieves a user by its ID
     *
     * @param userID the ID of the user to be retrieved
     * @return the retrieved user
     */
    @IsAdmin()
    @GetMapping(path = "/{userID}")
    public ResponseEntity<UserReadDTO> getUserByID(@PathVariable @Positive Integer userID) {
        log.info("GET /users/{userID}: userID={}", userID);

        Optional<UserReadDTO> foundUser = userService.getUserByID(userID);
        return ResponseEntity.ok(foundUser.orElseThrow(() -> new UserNotFoundException("No user found with the given ID")));
    }

    /**
     * Updates a user by its ID with the given user information
     *
     * @param userID the ID of the user to be updated
     * @param userUpdateDTO the new user information
     * @return the updated user
     */
    @IsAdmin()
    @PutMapping(path = "/{userID}")
    public ResponseEntity<UserReadDTO> updateUserByID(@PathVariable @Positive Integer userID, @RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        log.info("PUT /users/{userID}: userID={}, userUpdateDTO={}", userID, userUpdateDTO);

        Optional<UserReadDTO> updatedUser = userService.updateUserByID(userID, userUpdateDTO);
        return ResponseEntity.ok(updatedUser.orElseThrow(() -> new UserNotFoundException("No user found with the given ID")));
    }

    /**
     * Deletes a user by its ID
     *
     * @param userID the ID of the user to be deleted
     * @return  a ResponseEntity with no content
     */
    @ApiResponse(responseCode = "204")
    @IsAdmin()
    @DeleteMapping(path = "/{userID}")
    public ResponseEntity<Void> deleteUserByID(@PathVariable @Positive Long userID) {
        log.info("DELETE /users/{userID}: userID={}", userID);

        Long deletedUsers = userService.deleteUserByID(userID);

        if (deletedUsers == 0) {
            throw new UserNotFoundException("No user found with the given ID");
        }

        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves the currently authenticated user
     *
     * @param jwt the JWT token of the authenticated user
     * @return the currently authenticated user
     */
    @GetMapping(path = "/me")
    public ResponseEntity<UserReadDTO> getPrincipalUser(@AuthenticationPrincipal Jwt jwt) {
        log.info("GET /users/me: id={}, username={}, role={}", jwt.getClaimAsString("id"), jwt.getClaimAsString("username"), jwt.getClaimAsString("scope"));

        Optional<UserReadDTO> foundUser = userService.getUserByID(Integer.valueOf(jwt.getClaimAsString("id")));

        return ResponseEntity.ok(foundUser.orElseThrow(() -> new UserNotFoundException("No user found with the given JWT token")));
    }

    /**
     * Updates the currently authenticated user with the given user information
     *
     * @param userUpdateDTO the new user information
     * @param jwt the JWT token of the authenticated user
     * @return the updated user
     */
    @PutMapping(path = "/me")
    public ResponseEntity<UserReadDTO> updatePrincipalUser(@RequestBody @Valid UserUpdateDTO userUpdateDTO, @AuthenticationPrincipal Jwt jwt) {
        log.info("GET /users/me: userUpdateDTO={}, id={}, username={}, role={}", userUpdateDTO, jwt.getClaimAsString("id"), jwt.getClaimAsString("username"), jwt.getClaimAsString("scope"));

        Optional<UserReadDTO> updatedUser = userService.updateUserByID(Integer.valueOf(jwt.getClaimAsString("id")), userUpdateDTO);

        return ResponseEntity.ok(updatedUser.orElseThrow(() -> new UserNotFoundException("No user found with the given JWT token")));
    }

    /**
     * Deletes the currently authenticated user
     *
     * @param jwt the JWT token of the authenticated user
     * @return a ResponseEntity with no content
     */
    @ApiResponse(responseCode = "204")
    @DeleteMapping(path = "/me")
    public ResponseEntity<Void> deleteUserByID(@AuthenticationPrincipal Jwt jwt) {
        log.info("DELETE /users/me: id={}, username={}, role={}", jwt.getClaimAsString("id"), jwt.getClaimAsString("username"), jwt.getClaimAsString("scope"));

        Long deletedUsers = userService.deleteUserByID(Long.valueOf(jwt.getClaimAsString("id")));

        if (deletedUsers == 0) {
            throw new UserNotFoundException("No user found with the given JWT");
        }

        return ResponseEntity.noContent().build();
    }
}