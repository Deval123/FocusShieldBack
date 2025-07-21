package com.dev.focusshield.service.focus_config;

import com.dev.focusshield.entities.FocusConfigEntity;
import com.dev.focusshield.entities.UserEntity;
import com.dev.focusshield.model.FocusConfig;
import com.dev.focusshield.model.FocusConfigRequest;
import com.dev.focusshield.repositories.FocusConfigRepository;
import com.dev.focusshield.repositories.UserRepository;
import com.dev.focusshield.utils.mappers.FocusConfigMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FocusConfigServiceImpl implements FocusConfigService {

    private final FocusConfigRepository focusConfigRepository;
    private final UserRepository userRepository;
    private final FocusConfigMapper focusConfigMapper;

    @Override
    public void saveFocusConfiguration(FocusConfigRequest configRequest) {

        String universalIdString =  getUniversalIdString();

        if (universalIdString == null) {
            // This should ideally not happen if authentication is mandatory for this endpoint
            throw new IllegalStateException("Authenticated user's universal ID not found in security context.");
        }
        UUID universalId = UUID.fromString(universalIdString);


        UserEntity user = userRepository.findByUniversalId(universalId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + universalId));

        FocusConfigEntity entity = focusConfigMapper.toEntity(configRequest);
        entity.setUser(user);
        entity.setActive(true);

        focusConfigRepository.save(entity);
    }

    private static String getUniversalIdString(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    @Override
    public FocusConfig getLatestFocusConfiguration() {
        // This method should also get the universalId from the SecurityContext
        String universalIdString =  getUniversalIdString();
        if (universalIdString == null) {
            throw new IllegalStateException("Authenticated user's universal ID not found in security context.");
        }
        UUID universalId = UUID.fromString(universalIdString);

        UserEntity user = userRepository.findByUniversalId(universalId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + universalId));

        FocusConfigEntity latest = focusConfigRepository
                .findTopByUserAndActiveOrderBySavedAtDesc(user, true) // ⭐ ADD FILTER BY USER ⭐
                .orElseThrow(() -> new EntityNotFoundException("No active focus config found for user " + universalId));

        return focusConfigMapper.toResponse(latest);
    }

    @Override
    public List<FocusConfig> getAllConfigsForUser() {
        // ⭐ MODIFIED: Get universalId from SecurityContext instead of HttpServletRequest ⭐
        String universalIdString =  getUniversalIdString();
        if (universalIdString == null) {
            throw new IllegalStateException("Authenticated user's universal ID not found in security context.");
        }
        UUID universalId = UUID.fromString(universalIdString);

        UserEntity user = userRepository.findByUniversalId(universalId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + universalId));

        return focusConfigRepository.findByUser(user).stream()
                .map(focusConfigMapper::toResponse)
                .toList();
    }

    @Override
    public void deleteFocusConfig(UUID id) {
        FocusConfigEntity config = focusConfigRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("FocusConfig not found"));

        focusConfigRepository.delete(config);
    }
}
