package org.example.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.example.oauth.GitHubUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtUtil {

    private SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }

    public String getUsername(String token) {
        return extractAllClaims(token).get("username", String.class);
    }

    public Boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public String createToken(String username) {
        long expiredMs = 1000 * 60 * 60 * 10;
        return Jwts.builder()
                .claim("username", username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public ResponseCookie createCookie(String key, String value) {
        return ResponseCookie.from(key, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(60 * 60 * 60 * 10)
                .path("/")
                .build();
    }

    public static String getUsernameFromContext(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            GitHubUserDetails githubUserDetail = (GitHubUserDetails) authentication.getPrincipal();
            return githubUserDetail.getName();
        } catch (Exception e) {
            return null;
        }
    }
}
