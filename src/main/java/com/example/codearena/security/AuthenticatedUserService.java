package com.example.codearena.security;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserService {

    public Long getCurrentUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getPrincipal() == null
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AuthenticationCredentialsNotFoundException(
                    "User is not authenticated"
            );
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof Number)) {
            throw new AuthenticationCredentialsNotFoundException(
                    "Authenticated user ID is invalid"
            );
        }

        long userId = ((Number) principal).longValue();

        if (userId <= 0) {
            throw new AuthenticationCredentialsNotFoundException(
                    "Authenticated user ID is invalid"
            );
        }

        return userId;
    }
}