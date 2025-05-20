package com.vagsoft.bookstore.services;

import com.vagsoft.bookstore.dto.UserLoginDTO;
import com.vagsoft.bookstore.dto.UserReadDTO;
import com.vagsoft.bookstore.dto.UserWriteDTO;
import com.vagsoft.bookstore.mappers.UserMapper;
import com.vagsoft.bookstore.models.entities.User;
import com.vagsoft.bookstore.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for handling authentication-related operations
 */
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, AuthenticationManager authenticationManager1, JwtService jwtService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager1;
        this.jwtService = jwtService;
    }


    /**
     * Registers a new user
     *
     * @param userWriteDTO the UserWriteDTO containing user details
     * @return the registered UserReadDTO
     */
    public Optional<UserReadDTO> registerUser(UserWriteDTO userWriteDTO) {
        String hashedPassword = passwordEncoder.encode(userWriteDTO.getPassword());
        userWriteDTO.setPassword(hashedPassword);

        User user = userMapper.DtoToUser(userWriteDTO);

        User savedUser = userRepository.save(user);
        return Optional.of(userMapper.UserToReadDto(savedUser));
    }

    /**
     * Authenticates a user and returns a JWT token
     *
     * @param userLoginDTO the UserLoginDTO containing login credentials
     * @return the generated JWT token
     */
    public String authenticate(UserLoginDTO userLoginDTO) {

        final var authToken = UsernamePasswordAuthenticationToken
                .unauthenticated(userLoginDTO.getUsername(), userLoginDTO.getPassword());

        final var authentication = authenticationManager
                .authenticate(authToken);

        return jwtService.generateToken(authentication);
    }
}
