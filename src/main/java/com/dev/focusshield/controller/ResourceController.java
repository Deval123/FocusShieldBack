package com.dev.focusshield.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ResourceController {
    private final BuildProperties buildProperties;

    @GetMapping("/")
    public String getResource(){
        return  "a value...";
    }

    @GetMapping("/app-info")
    public ResponseEntity<?> getInformation() {
        try {
            Map<String, String> information = new HashMap<>();

            // Artifact's name from the pom.xml file
            String name = buildProperties.getName();
            // Artifact version
            String version = buildProperties.getVersion();
            // Date and Time of the build
            Instant time = buildProperties.getTime();
            LocalDate locDate = time.atZone(ZoneId.systemDefault()).toLocalDate();
            String strDate = locDate.toString();
            // Artifact ID from the pom file
            String artifact = buildProperties.getArtifact();
            // Group ID from the pom file
            String group = buildProperties.getGroup();
            // Environment (retrieved from environment variables)
            String environment = System.getenv("APP_ENVIRONMENT");
            // JWT Expiration value from application properties

            information.put("name", name);
            information.put("version", version);
            information.put("buildDate", strDate);
            information.put("artifactId", artifact);
            information.put("groupId", group);
            information.put("environment", environment != null ? environment : "undefined");

            return new ResponseEntity<>(information, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unable to retrieve application information");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
