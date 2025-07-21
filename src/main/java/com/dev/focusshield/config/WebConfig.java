package com.dev.focusshield.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final ChromeProperties chromeProperties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String extensionOrigin = "chrome-extension://" + chromeProperties.getExtensionId();

        registry.addMapping("/**")
                .allowedOrigins(extensionOrigin)
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}