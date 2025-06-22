package com.dev.focusshield.utils.contants;

/**
 * Classe utilitaire regroupant les constantes globales utilisées dans l'application.
 */
public class Constant {

    /**
     * Liste des endpoints publics accessibles sans authentification pour Swagger, healthcheck, etc.
     * Utilisée principalement dans la configuration de sécurité (SecurityConfig).
     */
    public static final String[] AUTH_WHITELIST = {
            "/logout",
            "/api-docs/**",          // For Swagger, typically relative to root or special handling
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/health/healthcheck/**",
            "/users",                   // Maps to /focus-shield-service/api/v1/users
            "/users/assign-role",       // Maps to /focus-shield-service/api/v1/users/assign-role
            "/users/login",              // Maps to /focus-shield-service/api/v1/users/login
            "/app-info",                // Maps to /focus-shield-service/api/v1/app-info (we start after the context path)
            "/actuator/**"
    };

}

