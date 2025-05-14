package com.vagsoft.bookstore.services;

import com.vagsoft.bookstore.repositories.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Service class for handling JWT token generation
 */
@RequiredArgsConstructor
public class JwtService {
    private final String issuer;

    private final Duration ttl;

    private final JwtEncoder jwtEncoder;

    public String generateToken(final String username) {
        final var claimsSet = JwtClaimsSet.builder()
                                .subject(username)
                                .issuer(issuer)
                                .expiresAt(Instant.now().plus(ttl))
                                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet))
                .getTokenValue();
    }

    public String generateToken(Authentication authentication) {
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(ttl))
                .subject(authentication.getPrincipal().toString())
                .claim("scope", scope)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }
}
