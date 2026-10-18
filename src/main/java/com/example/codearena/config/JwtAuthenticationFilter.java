
package com.example.codearena.config;

import com.example.codearena.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.codearena.models.User;
import com.example.codearena.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserRepository userRepository) {
        this.jwtUtils = jwtUtils; this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = null;

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null
                && !token.isBlank()
                && SecurityContextHolder.getContext()
                .getAuthentication() == null) {

            try {
                String subject = jwtUtils.extractID(token);
                Long userId = Long.valueOf(subject);

                if (userId <= 0) {
                    throw new IllegalArgumentException("Invalid JWT subject");
                }

                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("Unknown user"));

                if (user.getRole() == null) {
                    throw new IllegalArgumentException("User role is missing");
                }

                var authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                );

                var authentication = new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        authorities
                );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (RuntimeException ex) {
                // Never authenticate a request with an invalid token.
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
