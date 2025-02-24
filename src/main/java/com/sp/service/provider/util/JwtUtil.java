package com.sp.service.provider.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private final Key SECRET_KEY;
    private final long ACCESS_TOKEN_EXPIRATION = 15 * 60 * 1000; // 15 minutes
    private final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000; // 7 days

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Generate Access Token
    public String generateAccessToken(String username) {
        return createToken(new HashMap<>(), username, ACCESS_TOKEN_EXPIRATION);
    }


    // Generate Refresh Token
    public String generateRefreshToken(String username) {
        return createToken(new HashMap<>(), username, REFRESH_TOKEN_EXPIRATION);
    }

    // Create JWT Token
    private String createToken(Map<String, Object> claims, String subject, long expirationTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
                .compact();
    }

    // Validate Access Token
    public Boolean validateAccessToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Validate Refresh Token (Doesn't require UserDetails)
    public Boolean validateRefreshToken(String token) {
        return !isTokenExpired(token);
    }

    // Extract Username from Token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        System.out.println("🔍 Received Token: " + token);
        if (token == null || token.trim().isEmpty() || token.equals("undefined")) {
            logger.error("JWT token is null, empty, or undefined");
            throw new MalformedJwtException("JWT token is null, empty, or undefined");
        }
        validateTokenStructure(token); // Validate token structure
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (MalformedJwtException e) {
            logger.error("Malformed JWT: {}", token, e);
            throw e;
        }
    }


    private void validateTokenStructure(String token) {
        if (token.chars().filter(ch -> ch == '.').count() != 2) {
            throw new MalformedJwtException("JWT token must contain exactly 2 period characters");
        }
    }

    /**
     * ✅ Extract user ID from JWT token
     */
    public Long extractUserId(String token) {
        if (token == null || token.trim().isEmpty()) {
            logger.error("JWT token is null or empty");
            throw new MalformedJwtException("JWT token is null or empty");
        }
        validateTokenStructure(token); // Validate token structure
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("userId", Long.class);
    }
}