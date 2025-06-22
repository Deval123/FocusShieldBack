package com.dev.focusshield.config;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.dev.focusshield.utils.contants.Constant.AUTH_WHITELIST;

/**
 * Classe contenant les propriétés de sécurité, injectées depuis application.properties.
 */
@Getter
@Component
public class SecurityProperties {
    private static final Logger logger = LoggerFactory.getLogger(SecurityProperties.class);

    // Add a getter for contextPath for later debugging if needed
    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    /**
     * Retourne les chemins complets (préfixés du contextPath) à ignorer pour les filtres JWT.
     */
    public List<String> getFullPublicEndpoints() {
        // Étant donné que Spring Security semble évaluer les chemins "après" la suppression du chemin contextuel,
        // nous devrions renvoyer les chemins AUTH_WHITELIST bruts pour requestMatchers.
        // La variable contextPath elle-même est trompeuse pour ce cas d'utilisation spécifique.
        List<String> rawPublicPaths = Stream.of(AUTH_WHITELIST).collect(Collectors.toList());
        logger.info("Points de terminaison publics (tels que vus par Spring Security): {}", rawPublicPaths);
        return rawPublicPaths;
    }

}