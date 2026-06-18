package com.example.clashcoding.utils;

import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
    @Value("${security.jwt-secretKey}")
    private String secretKey;

    @Value("${security.expiryTime}")
    private long expiryTime;

    public String generateToken(Long userID) {
        return Jwts.builder().subject(String.valueOf(userID)).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + expiryTime)).signWith(Keys.hmacShaKeyFor(secretKey.getBytes())).compact();
    }

    public String extractID(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }
}
