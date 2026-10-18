package com.example.codearena.utils;

import com.example.codearena.config.ExceptionConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ID {
    @Autowired
    private JwtUtils jwtUtils;

    public Long getId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new ExceptionConfig("User is not authenticated");
        }

        for (Cookie cookie : cookies) {
            if ("jwt".equals(cookie.getName())) {
                try {
                    String userId = jwtUtils.extractID(cookie.getValue());
                    return Long.valueOf(userId);
                } catch (RuntimeException ex) {
                    throw new ExceptionConfig("User is not authenticated");
                }
            }
        }

        throw new ExceptionConfig("User is not authenticated");
    }
}
