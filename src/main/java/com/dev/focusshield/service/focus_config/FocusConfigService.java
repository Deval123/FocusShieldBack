package com.dev.focusshield.service.focus_config;


import com.dev.focusshield.model.FocusConfig;
import com.dev.focusshield.model.FocusConfigRequest;

import java.util.List;
import java.util.UUID;

public interface FocusConfigService {

    /**
     * Save the user's focus configuration.
     *
     * @param configRequest the focus configuration to save
     */
    void saveFocusConfiguration(FocusConfigRequest configRequest);

    /**
     * Retrieve the user's latest saved focus configuration.
     *
     * @return the latest focus configuration
     */
    FocusConfig getLatestFocusConfiguration();


    List<FocusConfig> getAllConfigsForUser();

    void deleteFocusConfig(UUID id);
}
