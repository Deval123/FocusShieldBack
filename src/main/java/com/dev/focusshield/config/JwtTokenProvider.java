package com.dev.focusshield.config;

import com.dev.focusshield.entities.RoleEntity;
import com.dev.focusshield.entities.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID; // Import UUID
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    @Value("${jwt.expiration.ms}")
    private long expirationTime;

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            logger.error(" ❌❌ Échec de validation du token : {}", token, e); // Log exception for debugging
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    // ⭐ Extract universalId from token ⭐
    public UUID getUniversalIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        String universalIdString = claims.get("universalId", String.class); // Get universalId as String
        if (universalIdString != null) {
            try {
                return UUID.fromString(universalIdString);
            } catch (IllegalArgumentException e) {
                logger.error("Invalid universalId format in token: {}", universalIdString, e);
                return null; // Or throw a specific exception if universalId is mandatory
            }
        }
        logger.warn("Claim 'universalId' not found in token or is null.");
        return null;
    }

    public List<SimpleGrantedAuthority> getAuthorities(String token) {
        Claims claims = getClaimsFromToken(token);
        Object rolesObject = claims.get("roles");

        if (rolesObject instanceof List<?>) {
            List<String> roles = ((List<?>) rolesObject).stream()
                    .filter(obj -> obj instanceof String)
                    .map(obj -> (String) obj)
                    .toList();

            return roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        } else {
            logger.warn("Le claim 'roles' est absent ou mal formé dans le token JWT");
            return List.of();
        }
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ⭐ Generate Token METHOD: Add universalId to claims ⭐
    public String generateToken(UserEntity user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("username", user.getUsername());
        claims.put("universalId", user.getUniversalId().toString()); // Add universalId as String

        List<String> roleNames = user.getRoles().stream()
                .map(RoleEntity::getRoleName)
                .collect(Collectors.toList());
        claims.put("roles", roleNames);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        // Set subject to universalId or username, depending on your user principal strategy
        // For simplicity, let's keep subject as username for now if that's what you use in SecurityContext
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername()) // Typically the unique identifier for the subject
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SECRET_KEY)
                .compact();
    }

    public List<SimpleGrantedAuthority> extractAuthoritiesFromToken(String token) {
        if (!validateToken(token)) {
            logger.warn("Token JWT invalide ou expiré");
            return List.of();
        }
        return getAuthorities(token);
    }
}