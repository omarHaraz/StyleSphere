package com.StyleSphere.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // 1. Create a secure SecretKey object once
    private final SecretKey key = Keys.hmacShaKeyFor(
            "YourSuperSecretKeyThatIsAtLeast32CharactersLong1234567890".getBytes(StandardCharsets.UTF_8)
    );

    private final long jwtExpirationInMs = 3600000;

    public String generateToken(String username) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + jwtExpirationInMs);

        // 2. Use the modern builder syntax
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(key)
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}