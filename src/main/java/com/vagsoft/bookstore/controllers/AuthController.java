package com.vagsoft.bookstore.controllers;

import java.util.Optional;

import com.vagsoft.bookstore.dto.userDTOs.UserLoginDTO;
import com.vagsoft.bookstore.dto.userDTOs.UserReadDTO;
import com.vagsoft.bookstore.dto.userDTOs.UserWriteDTO;
import com.vagsoft.bookstore.errors.exceptions.UserCreationException;
import com.vagsoft.bookstore.mappers.UserMapper;
import com.vagsoft.bookstore.services.AuthService;
import com.vagsoft.bookstore.validations.annotations.ValidAdminRegistration;
import com.vagsoft.bookstore.validations.groups.ExtendedValidation;
import com.vagsoft.bookstore.validations.groups.OrderedValidation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Controller class for handling authentication-related requests */
@RestController
@RequestMapping(path = "/auth")
@Validated(OrderedValidation.class)
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final UserMapper userMapper;

    public AuthController(AuthService authService, UserMapper userMapper) {
        this.authService = authService;
        this.userMapper = userMapper;
    }

    /**
     * Registers a new user and returns a JWT token
     *
     * @param userWriteDTO
     *            the UserWriteDTO containing user details
     * @return ResponseEntity containing the registered UserReadDTO and JWT token
     */
    @ApiResponse(responseCode = "201")
    @PostMapping("/register")
    public ResponseEntity<UserReadDTO> registerUser(
            @RequestBody @ValidAdminRegistration(groups = ExtendedValidation.class) @Valid UserWriteDTO userWriteDTO) {
        log.info("POST /auth/register: userWriteDTO={}", userWriteDTO);

        UserLoginDTO userLoginDTO = userMapper.UserWriteToLoginDto(userWriteDTO);

        Optional<UserReadDTO> registeredUser = authService.registerUser(userWriteDTO);

        String jwtToken = authService.authenticate(userLoginDTO);

        return ResponseEntity.status(HttpStatus.CREATED).header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .body(registeredUser.orElseThrow(() -> new UserCreationException("User registration failed")));
    }

    /**
     * Logins a user and returns a JWT token
     *
     * @param userLoginDTO
     *            the UserLoginDTO containing login credentials
     * @return ResponseEntity containing the JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody @Valid UserLoginDTO userLoginDTO) {
        log.info("POST /auth/login: userWriteDTO={}", userLoginDTO);

        return ResponseEntity.status(HttpStatus.OK).body(authService.authenticate(userLoginDTO));
    }
}
