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
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    /**
     * Clé secrète pour la signature du JWT, générée avec un algorithme HS512 sécurisé.
     */
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    /**
     * Durée de validité du token en millisecondes, injectée depuis la configuration.
     */
    @Value("${jwt.expiration.ms}")
    private long expirationTime;

    /**
     * Valide un token JWT en vérifiant sa signature et sa structure.
     *
     * @param token le token JWT à valider
     * @return true si le token est valide, false sinon
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            logger.error(" ❌❌ Échec de validation du token : {}", token);
            return false;
        }
    }

    /**
     * Extrait le nom d'utilisateur (subject) contenu dans un token JWT.
     *
     * @param token le token JWT
     * @return le nom d'utilisateur (username) contenu dans le token
     */
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * Récupère la liste des rôles (authorities) contenus dans un token JWT.
     *
     * @param token le token JWT
     * @return une liste de SimpleGrantedAuthority correspondant aux rôles de l'utilisateur,
     *         ou une liste vide si le claim 'roles' est absent ou mal formé
     */
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
            return List.of();  // retourne une liste vide pour éviter NullPointerException
        }
    }

    /**
     * Extrait tous les claims (données) contenus dans un token JWT.
     *
     * @param token le token JWT
     * @return l'objet Claims contenant les données du token
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Génère un token JWT pour un utilisateur donné, incluant email, username et rôles.
     * Le token est signé avec une clé sécurisée et expirera après la durée configurée.
     *
     * @param user l'entité utilisateur pour laquelle générer le token
     * @return la chaîne JWT signée et compacte
     */
    public String generateToken(UserEntity user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("username", user.getUsername());

        List<String> roleNames = user.getRoles().stream()
                .map(RoleEntity::getRoleName)
                .collect(Collectors.toList());
        claims.put("roles", roleNames);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * Valide un token JWT et retourne directement la liste des rôles sous forme d'authorities.
     * En cas de token invalide ou expiré, retourne une liste vide.
     *
     * @param token le token JWT à valider et analyser
     * @return la liste des SimpleGrantedAuthority extraites du token, ou une liste vide si invalide
     */
    public List<SimpleGrantedAuthority> extractAuthoritiesFromToken(String token) {
        if (!validateToken(token)) {
            logger.warn("Token JWT invalide ou expiré");
            return List.of();
        }
        return getAuthorities(token);
    }
}