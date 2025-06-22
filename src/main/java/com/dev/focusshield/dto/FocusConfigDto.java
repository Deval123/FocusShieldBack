package com.dev.focusshield.dto;

import java.time.LocalDateTime;
import java.util.List;

// dto/FocusConfigDto.java
public record FocusConfigDto(
        List<String> blockedSites,
        Integer durationMinutes,
        Boolean isActive,
        LocalDateTime savedAt
) {}
