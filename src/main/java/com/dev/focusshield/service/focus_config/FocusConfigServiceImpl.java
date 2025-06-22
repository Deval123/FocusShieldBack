package com.dev.focusshield.service.focus_config;

import com.dev.focusshield.entities.FocusConfigEntity;
import com.dev.focusshield.entities.UserEntity;
import com.dev.focusshield.model.FocusConfig;
import com.dev.focusshield.model.FocusConfigRequest;
import com.dev.focusshield.repositories.FocusConfigRepository;
import com.dev.focusshield.repositories.UserRepository;
import com.dev.focusshield.utils.mappers.FocusConfigMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FocusConfigServiceImpl implements FocusConfigService {

    private final FocusConfigRepository focusConfigRepository;
    private final UserRepository userRepository;
    private final FocusConfigMapper focusConfigMapper;

    @Override
    public void saveFocusConfiguration(FocusConfigRequest configRequest, HttpServletRequest httpServletRequest) {

        UUID findByUniversalId = UUID.fromString(httpServletRequest.getParameter("universalId"));
        UserEntity user = userRepository.findByUniversalId(findByUniversalId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        FocusConfigEntity entity = FocusConfigEntity.builder()
                .blockedSites(configRequest.getBlockedSites())
                .durationMinutes(configRequest.getDurationMinutes())
                .isActive(true)
                .savedAt(LocalDateTime.now())
                .user(user)
                .build();

        focusConfigRepository.save(entity);
    }

    @Override
    public FocusConfig getLatestFocusConfiguration() {
        // Assuming you want to get the latest active config (you may want to filter by user)
        FocusConfigEntity latest = focusConfigRepository
                .findTopByIsActiveOrderBySavedAtDesc(true)
                .orElseThrow(() -> new EntityNotFoundException("No active focus config found"));

        return focusConfigMapper.toResponse(latest);
    }

    @Override
    public List<FocusConfig> getAllConfigsForUser(HttpServletRequest request) {
        UUID userId = UUID.fromString(request.getParameter("universalId"));
        UserEntity user = userRepository.findByUniversalId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

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
