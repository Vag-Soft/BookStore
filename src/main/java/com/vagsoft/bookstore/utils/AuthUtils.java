package com.vagsoft.bookstore.utils;

import com.vagsoft.bookstore.models.CustomUserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {
    public Integer getUserIdFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {

            return Integer.valueOf(jwt.getClaimAsString("id"));
        } else {
            throw new IllegalArgumentException("Invalid Jwt token");
        }
    }
}
