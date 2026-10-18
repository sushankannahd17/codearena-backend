package com.example.codearena.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.codearena.security.AuthenticatedUserService;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticatedUserServiceTest {

    private AuthenticatedUserService service;

    @BeforeEach
    void setUp() {
        service = new AuthenticatedUserService();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void returnsAuthenticatedUserId() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        42L, null, java.util.Collections.emptyList()
                )
        );

        assertEquals(42L, service.getCurrentUserId());
    }

    @Test
    void rejectsMissingAuthentication() {
        assertThrows(
                AuthenticationCredentialsNotFoundException.class,
                () -> service.getCurrentUserId()
        );
    }

    @Test
    void rejectsAnonymousUser() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "anonymousUser", null,
                        java.util.Collections.emptyList()
                )
        );

        assertThrows(
                AuthenticationCredentialsNotFoundException.class,
                () -> service.getCurrentUserId()
        );
    }

    @Test
    void rejectsInvalidPrincipalType() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "42", null,
                        java.util.Collections.emptyList()
                )
        );

        assertThrows(
                AuthenticationCredentialsNotFoundException.class,
                () -> service.getCurrentUserId()
        );
    }

    @Test
    void rejectsNonPositiveUserId() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        0L, null,
                        java.util.Collections.emptyList()
                )
        );

        assertThrows(
                AuthenticationCredentialsNotFoundException.class,
                () -> service.getCurrentUserId()
        );
    }
}