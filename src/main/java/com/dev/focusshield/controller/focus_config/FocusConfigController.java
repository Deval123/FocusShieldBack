package com.dev.focusshield.controller.focus_config;

import com.dev.focusshield.model.FocusConfig;
import com.dev.focusshield.model.FocusConfigRequest;
import com.dev.focusshield.service.focus_config.FocusConfigService;
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
     * @return HTTP 201 CREATED
     */
    @PostMapping
    public ResponseEntity<Void> saveFocusConfiguration(
            @RequestBody FocusConfigRequest configRequest
    ) {
        focusConfigService.saveFocusConfiguration(configRequest);
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
     * @return list of FocusConfig
     */
    @GetMapping
    public ResponseEntity<List<FocusConfig>> getAllConfigsForUser( ) {
        List<FocusConfig> configs = focusConfigService.getAllConfigsForUser();
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