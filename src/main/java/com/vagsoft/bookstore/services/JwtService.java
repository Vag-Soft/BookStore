package com.vagsoft.bookstore.services;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Collectors;

import com.vagsoft.bookstore.models.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

/** Service class for handling JWT token generation. */
@RequiredArgsConstructor
public class JwtService {
    private final String issuer;

    private final Duration ttl;

    private final JwtEncoder jwtEncoder;

    /**
     * Generates a JWT token for the given authentication.
     *
     * @param authentication
     *            the authentication object containing user details
     * @return the generated JWT token
     */
    public String generateToken(final Authentication authentication) {
        String scope = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        JwtClaimsSet claimsSet = JwtClaimsSet.builder().issuer(issuer).issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(ttl)).subject(userDetails.getUsername()).claim("id", userDetails.getId())
                .claim("scope", scope).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }
}
