package com.vagsoft.bookstore.services;

import java.util.Optional;

import com.vagsoft.bookstore.dto.userDTOs.UserLoginDTO;
import com.vagsoft.bookstore.dto.userDTOs.UserReadDTO;
import com.vagsoft.bookstore.dto.userDTOs.UserWriteDTO;
import com.vagsoft.bookstore.mappers.UserMapper;
import com.vagsoft.bookstore.models.entities.User;
import com.vagsoft.bookstore.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service class for handling authentication-related operations. */
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CartService cartService;

    public AuthService(final UserRepository userRepository, final UserMapper userMapper,
            final PasswordEncoder passwordEncoder, final AuthenticationManager authenticationManager,
            final JwtService jwtService, final CartService cartService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.cartService = cartService;
    }

    /**
     * Registers a new user.
     *
     * @param userWriteDTO
     *            the UserWriteDTO containing user details
     * @return the registered UserReadDTO
     */
    @Transactional
    public Optional<UserReadDTO> registerUser(final UserWriteDTO userWriteDTO) {
        String hashedPassword = passwordEncoder.encode(userWriteDTO.getPassword());
        userWriteDTO.setPassword(hashedPassword);

        User userToSave = userMapper.dtoToUser(userWriteDTO);

        User savedUser = userRepository.save(userToSave);

        // Create an empty cart for the newly registered user
        cartService.createEmptyCart(savedUser);

        return Optional.of(userMapper.userToReadDto(savedUser));
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param userLoginDTO
     *            the UserLoginDTO containing login credentials
     * @return the generated JWT token
     */
    public String authenticate(final UserLoginDTO userLoginDTO) {

        final var authToken = UsernamePasswordAuthenticationToken.unauthenticated(userLoginDTO.getUsername(),
                userLoginDTO.getPassword());

        final var authentication = authenticationManager.authenticate(authToken);

        return jwtService.generateToken(authentication);
    }
}
