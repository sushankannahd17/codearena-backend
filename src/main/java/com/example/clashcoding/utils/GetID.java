package com.example.clashcoding.utils;

import com.example.clashcoding.config.ExceptionConfig;
import com.example.clashcoding.utils.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class GetID {
    @Autowired
    private JwtUtils jwtUtils;

    public Long getId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        Long id = null;
        for (Cookie cookie : cookies) {
            if ("jwt".equalsIgnoreCase(cookie.getName())) {
                id = Long.parseLong(jwtUtils.extractID(cookie.getValue()));
            }
        }

        if (id == null) {
            throw new ExceptionConfig("User is not authenticated");
        }

        return id;
    }
}
