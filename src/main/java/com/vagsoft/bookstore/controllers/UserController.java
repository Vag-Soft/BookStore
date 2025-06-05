package com.vagsoft.bookstore.controllers;

import java.util.Optional;

import com.vagsoft.bookstore.dto.userDTOs.UserReadDTO;
import com.vagsoft.bookstore.dto.userDTOs.UserUpdateDTO;
import com.vagsoft.bookstore.errors.exceptions.UserUpdateException;
import com.vagsoft.bookstore.models.enums.Role;
import com.vagsoft.bookstore.repositories.UserRepository;
import com.vagsoft.bookstore.services.UserService;
import com.vagsoft.bookstore.utils.AuthUtils;
import com.vagsoft.bookstore.validations.annotations.ExistsResource;
import com.vagsoft.bookstore.validations.annotations.IsAdmin;
import com.vagsoft.bookstore.validations.annotations.NullOrNotBlank;
import com.vagsoft.bookstore.validations.groups.BasicValidation;
import com.vagsoft.bookstore.validations.groups.ExtendedValidation;
import com.vagsoft.bookstore.validations.groups.OrderedValidation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/** REST controller for endpoints related to users */
@RestController
@RequestMapping(path = "/users")
@Validated(OrderedValidation.class)
public class UserController {
    private final UserService userService;
    private final AuthUtils authUtils;

    public UserController(UserService userService, AuthUtils authUtils) {
        this.userService = userService;
        this.authUtils = authUtils;
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
    @IsAdmin()
    @GetMapping
    public ResponseEntity<Page<UserReadDTO>> getUsers(
            @RequestParam(name = "username", required = false) @Size(max = 31, message = "username must be less than 32 characters", groups = BasicValidation.class) @NullOrNotBlank(groups = BasicValidation.class) String username,
            @RequestParam(name = "email", required = false) @Size(max = 320, message = "email must be less than 321 characters", groups = BasicValidation.class) @NullOrNotBlank(groups = BasicValidation.class) String email,
            @RequestParam(name = "role", required = false) Role role,
            @RequestParam(name = "firstName", required = false) @Size(max = 31, message = "firstName must be less than 32 characters", groups = BasicValidation.class) @NullOrNotBlank(groups = BasicValidation.class) String firstName,
            @RequestParam(name = "lastName", required = false) @Size(max = 31, message = "lastName must be less than 32 characters", groups = BasicValidation.class) @NullOrNotBlank(groups = BasicValidation.class) String lastName,
            Pageable pageable) {
        return ResponseEntity.ok(userService.getUsers(username, email, role, firstName, lastName, pageable));
    }

    /**
     * Retrieves a user by its ID
     *
     * @param userID
     *            the ID of the user to be retrieved
     * @return the retrieved user
     */
    @IsAdmin()
    @GetMapping(path = "/{userID}")
    public ResponseEntity<UserReadDTO> getUserByID(
            @PathVariable @Positive(groups = BasicValidation.class) @ExistsResource(repository = UserRepository.class, message = "User with given ID does not exist", groups = ExtendedValidation.class) Integer userID) {
        UserReadDTO foundUser = userService.getUserByID(userID);
        return ResponseEntity.ok(foundUser);
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
    @IsAdmin()
    @PutMapping(path = "/{userID}")
    public ResponseEntity<UserReadDTO> updateUserByID(
            @PathVariable @Positive(groups = BasicValidation.class) @ExistsResource(repository = UserRepository.class, message = "User with given ID does not exist", groups = ExtendedValidation.class) Integer userID,
            @RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        Optional<UserReadDTO> updatedUser = userService.updateUserByID(userID, userUpdateDTO);
        return ResponseEntity.ok(
                updatedUser.orElseThrow(() -> new UserUpdateException("User with ID:" + userID + " update failed")));
    }

    /**
     * Deletes a user by its ID
     *
     * @param userID
     *            the ID of the user to be deleted
     * @return a ResponseEntity with no content
     */
    @ApiResponse(responseCode = "204")
    @IsAdmin()
    @DeleteMapping(path = "/{userID}")
    public ResponseEntity<Void> deleteUserByID(
            @PathVariable @Positive(groups = BasicValidation.class) @ExistsResource(repository = UserRepository.class, message = "User with given ID does not exist", groups = ExtendedValidation.class) Integer userID) {
        userService.deleteUserByID(userID);

        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves the currently authenticated user
     *
     * @return the currently authenticated user
     */
    @GetMapping(path = "/me")
    public ResponseEntity<UserReadDTO> getPrincipalUser() {
        Integer userID = authUtils.getUserIdFromAuthentication();

        UserReadDTO foundUser = userService.getUserByID(userID);

        return ResponseEntity.ok(foundUser);
    }

    /**
     * Updates the currently authenticated user with the given user information
     *
     * @param userUpdateDTO
     *            the new user information
     * @return the updated user
     */
    @PutMapping(path = "/me")
    public ResponseEntity<UserReadDTO> updatePrincipalUser(@RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        Integer userID = authUtils.getUserIdFromAuthentication();

        Optional<UserReadDTO> updatedUser = userService.updateUserByID(userID, userUpdateDTO);

        return ResponseEntity.ok(
                updatedUser.orElseThrow(() -> new UserUpdateException("User with ID:" + userID + " update failed")));
    }

    /**
     * Deletes the currently authenticated user
     *
     * @return a ResponseEntity with no content
     */
    @ApiResponse(responseCode = "204")
    @DeleteMapping(path = "/me")
    public ResponseEntity<Void> deleteUserByID() {
        Integer userID = authUtils.getUserIdFromAuthentication();

        userService.deleteUserByID(userID);

        return ResponseEntity.noContent().build();
    }
}
