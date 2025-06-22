package com.dev.focusshield.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtre de sécurité personnalisé qui intercepte chaque requête HTTP pour vérifier la présence d’un token JWT.
 * Si le token est valide, il initialise le contexte de sécurité avec l'utilisateur authentifié.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityProperties securityProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * Constructeur avec injection du fournisseur de token JWT.
     * @param jwtTokenProvider fournisseur de méthodes liées aux JWT
     */
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, SecurityProperties securityProperties) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.securityProperties = securityProperties;
    }

    /**
     * Détermine si une requête ne doit pas être filtrée par ce filtre.
     * @param request la requête HTTP
     * @return true si l'URL est dans les routes publiques
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath(); // This should be /focus-shield-service/api/v1

        // Calculate the path once and store it in a final or effective final variable
        final String pathForMatching = getString(requestUri, contextPath);


        List<String> publicEndpoints = securityProperties.getFullPublicEndpoints();

        boolean isPublic = publicEndpoints.stream()
                .anyMatch(pattern -> {
                    // Use the effectively final 'pathForMatching' variable
                    boolean match = pathMatcher.match(pattern, pathForMatching);
                    if (match) {
                        logger.debug("--- JWT Filter Match DEBUG --- Path '{}' MATCHES public pattern '{}'", pathForMatching, pattern);
                    }
                    return match;
                });

        logger.info("--- JWT Filter Decision --- Request URI: '{}', Context Path: '{}', Path to match: '{}'. Should NOT filter: {}. Public Endpoints: {}",
                requestUri, contextPath, pathForMatching, isPublic, publicEndpoints);
        return isPublic;
    }

    @NotNull
    private static String getString(String requestUri, String contextPath) {
        final String pathForMatching; // Declare it as final or ensure a single assignment

        String tempPath; // Use a temporary variable for intermediate calculations
        if (requestUri.startsWith(contextPath)) {
            tempPath = requestUri.substring(contextPath.length());
        } else {
            tempPath = requestUri;
        }

        if (!tempPath.startsWith("/")) {
            tempPath = "/" + tempPath;
        }
        pathForMatching = tempPath; // Assign to the final variable only once
        return pathForMatching;
    }


    /**
     * Filtrage principal : extrait le token JWT de l'en-tête Authorization,
     * le valide, puis place l'utilisateur dans le contexte de sécurité.
     * @param request  requête HTTP
     * @param response réponse HTTP
     * @param filterChain chaîne de filtres à exécuter
     * @throws ServletException en cas d’erreur Servlet
     * @throws IOException      en cas d’erreur IO
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        logger.debug("🔍 JWT Filter triggered for path: {}", request.getRequestURI());

        String token = getTokenFromRequest(request);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, jwtTokenProvider.getAuthorities(token));
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.debug("✅ User '{}' authenticated via JWT for path: {}", username, request.getRequestURI());
        } else {
            logger.warn("🚫 No valid JWT token found or token validation failed for path: {}", request.getRequestURI());
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Extrait le token JWT de l'en-tête Authorization.
     * Le format attendu est : "Bearer <token>"
     * @param request requête HTTP
     * @return le token JWT ou null si absent
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}