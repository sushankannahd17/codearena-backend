package com.example.codearena.config;

import com.example.codearena.models.User;
import com.example.codearena.models.UserRole;
import com.example.codearena.repository.UserRepository;
import com.example.codearena.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtUtils jwtUtils;
    private UserRepository userRepository;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        jwtUtils = mock(JwtUtils.class);
        userRepository = mock(UserRepository.class);

        filter = new JwtAuthenticationFilter(jwtUtils, userRepository);

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void validJwtAuthenticatesUserWithDatabaseRole()
            throws ServletException, IOException {

        when(jwtUtils.extractID("valid-token")).thenReturn("123");

        User user = new User();
        user.setId(123L);
        user.setRole(UserRole.CANDIDATE);

        when(userRepository.findById(123L))
                .thenReturn(Optional.of(user));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("jwt", "valid-token"));

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertTrue(authentication.isAuthenticated());
        assertEquals(123L, authentication.getPrincipal());
        assertTrue(authentication.getAuthorities().contains(
                new SimpleGrantedAuthority("ROLE_CANDIDATE")
        ));

        verify(jwtUtils).extractID("valid-token");
        verify(userRepository).findById(123L);
    }

    @Test
    void missingCookieLeavesRequestUnauthenticated()
            throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        verifyNoInteractions(jwtUtils, userRepository);
    }

    @Test
    void invalidJwtDoesNotAuthenticateUser()
            throws ServletException, IOException {

        when(jwtUtils.extractID("tampered-token"))
                .thenThrow(new JwtException("Invalid JWT"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("jwt", "tampered-token"));

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        verifyNoInteractions(userRepository);
    }

    @Test
    void expiredJwtDoesNotAuthenticateUser()
            throws ServletException, IOException {

        when(jwtUtils.extractID("expired-token"))
                .thenThrow(new ExpiredJwtException(
                        null, null, "Token expired"
                ));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("jwt", "expired-token"));

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        verifyNoInteractions(userRepository);
    }

    @Test
    void nonNumericSubjectDoesNotAuthenticateUser()
            throws ServletException, IOException {

        when(jwtUtils.extractID("bad-subject-token"))
                .thenReturn("not-a-number");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("jwt", "bad-subject-token"));

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        verifyNoInteractions(userRepository);
    }

    @Test
    void nonPositiveUserIdDoesNotAuthenticateUser()
            throws ServletException, IOException {

        when(jwtUtils.extractID("zero-id-token")).thenReturn("0");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("jwt", "zero-id-token"));

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        verifyNoInteractions(userRepository);
    }

    @Test
    void unknownUserDoesNotAuthenticateUser()
            throws ServletException, IOException {

        when(jwtUtils.extractID("unknown-user-token"))
                .thenReturn("999");

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("jwt", "unknown-user-token"));

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        verify(userRepository).findById(999L);
    }

    @Test
    void organizerJwtReceivesOrganizerAuthority()
            throws ServletException, IOException {

        when(jwtUtils.extractID("organizer-token")).thenReturn("456");

        User user = new User();
        user.setId(456L);
        user.setRole(UserRole.ORGANIZER);

        when(userRepository.findById(456L))
                .thenReturn(Optional.of(user));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("jwt", "organizer-token"));

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals(456L, authentication.getPrincipal());
        assertTrue(authentication.getAuthorities().contains(
                new SimpleGrantedAuthority("ROLE_ORGANIZER")
        ));
        assertFalse(authentication.getAuthorities().contains(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        ));
    }
}
