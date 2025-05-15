package com.vagsoft.bookstore.controllers;

import com.vagsoft.bookstore.annotations.ValidAdminRegistration;
import com.vagsoft.bookstore.dto.BookReadDTO;
import com.vagsoft.bookstore.dto.UserLoginDTO;
import com.vagsoft.bookstore.dto.UserReadDTO;
import com.vagsoft.bookstore.dto.UserWriteDTO;
import com.vagsoft.bookstore.errors.exceptions.BookCreationException;
import com.vagsoft.bookstore.errors.exceptions.UserCreationException;
import com.vagsoft.bookstore.services.AuthService;
import com.vagsoft.bookstore.services.JwtService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Controller class for handling authentication-related requests
 */
@RestController
@RequestMapping(path = "/auth")
@Validated
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    /**
     * Registers a new user and returns a JWT token
     *
     * @param userWriteDTO the UserWriteDTO containing user details
     * @return ResponseEntity containing the registered UserReadDTO and JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<UserReadDTO> registerUser(@RequestBody @ValidAdminRegistration @Valid UserWriteDTO userWriteDTO) {
        log.info("POST /auth/register: userWriteDTO={}", userWriteDTO);

        String jwtToken = jwtService.generateToken(userWriteDTO.getUsername());

        Optional<UserReadDTO> registeredUser = authService.registerUser(userWriteDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .body(registeredUser.orElseThrow(() -> new UserCreationException("User registration failed")));
    }

    /**
     * Logins a user and returns a JWT token
     *
     * @param userLoginDTO the UserLoginDTO containing login credentials
     * @return ResponseEntity containing the JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody @Valid UserLoginDTO userLoginDTO) {
        log.info("POST /auth/login: userWriteDTO={}", userLoginDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.authenticate(userLoginDTO));
    }

}
