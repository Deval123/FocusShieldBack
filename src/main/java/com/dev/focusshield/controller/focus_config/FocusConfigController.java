package com.dev.focusshield.controller.focus_config;

import com.dev.focusshield.model.FocusConfig;
import com.dev.focusshield.model.FocusConfigRequest;
import com.dev.focusshield.service.focus_config.FocusConfigService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.dev.focusshield.utils.contants.ApiRoutes.FOCUS_CONFIG;

@RestController
@RequestMapping(FOCUS_CONFIG)
@RequiredArgsConstructor
public class FocusConfigController {

    private final FocusConfigService focusConfigService;

    /**
     * Save a new focus configuration for the authenticated user.
     *
     * @param configRequest the focus config request
     * @param request       the HTTP request containing the universalId
     * @return HTTP 201 CREATED
     */
    @PostMapping
    public ResponseEntity<Void> saveFocusConfiguration(
            @RequestBody FocusConfigRequest configRequest,
            HttpServletRequest request
    ) {
        focusConfigService.saveFocusConfiguration(configRequest, request);
        return ResponseEntity.status(201).build();
    }

    /**
     * Get the latest active focus configuration.
     *
     * @return the latest FocusConfig
     */
    @GetMapping("/latest")
    public ResponseEntity<FocusConfig> getLatestFocusConfiguration() {
        FocusConfig config = focusConfigService.getLatestFocusConfiguration();
        return ResponseEntity.ok(config);
    }

    /**
     * Get all focus configurations for the current user.
     *
     * @param request the HTTP request containing the universalId
     * @return list of FocusConfig
     */
    @GetMapping("/all")
    public ResponseEntity<List<FocusConfig>> getAllConfigsForUser(HttpServletRequest request) {
        List<FocusConfig> configs = focusConfigService.getAllConfigsForUser(request);
        return ResponseEntity.ok(configs);
    }

    /**
     * Delete a focus configuration by ID.
     *
     * @param configId the UUID of the config to delete
     * @return HTTP 204 NO CONTENT
     */
    @DeleteMapping("/{configId}")
    public ResponseEntity<Void> deleteFocusConfig(@PathVariable UUID configId) {
        focusConfigService.deleteFocusConfig(configId);
        return ResponseEntity.noContent().build();
    }
}