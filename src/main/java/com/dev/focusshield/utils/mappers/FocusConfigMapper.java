package com.dev.focusshield.utils.mappers;

import com.dev.focusshield.entities.FocusConfigEntity;
import com.dev.focusshield.entities.UserEntity;
import com.dev.focusshield.exceptions.FocusShieldErrorCode;
import com.dev.focusshield.exceptions.InvalidTimeFormatException;
import com.dev.focusshield.exceptions.JsonConversionException;
import com.dev.focusshield.model.FocusConfig;
import com.dev.focusshield.model.FocusConfigRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Map;

@Mapper(componentModel = "spring", // Use "spring" for Spring integration, or "default" if you prefer
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FocusConfigMapper {

    // IMPORTANT: Explicitly define INSTANCE if componentModel="spring" and you want to use it in tests
    FocusConfigMapper INSTANCE = Mappers.getMapper(FocusConfigMapper.class);

    ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // Mapping from FocusConfigRequest to FocusConfigEntity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "savedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(source = "customSelectors", target = "customSelectorsJson", qualifiedByName = "mapToJsonString")
    @Mapping(source = "pauseStartTime", target = "pauseStartTime", qualifiedByName = "stringToLocalTime")
    @Mapping(source = "pauseEndTime", target = "pauseEndTime", qualifiedByName = "stringToLocalTime")
    FocusConfigEntity toEntity(FocusConfigRequest request);

    // Mapping from FocusConfigEntity to FocusConfig (response DTO)
    @Mapping(source = "user.universalId", target = "userId")
    @Mapping(source = "customSelectorsJson", target = "customSelectors", qualifiedByName = "jsonStringToMap")
    @Mapping(source = "pauseStartTime", target = "pauseStartTime", qualifiedByName = "localTimeToString")
    @Mapping(source = "pauseEndTime", target = "pauseEndTime", qualifiedByName = "localTimeToString")
    // MapStruct will automatically find and use the `OffsetDateTime map(LocalDateTime value)` method
    // because the source and target types match the property types.
    FocusConfig toResponse(FocusConfigEntity entity);

    // --- Custom Mapping Methods (with @Named for qualifiedByName) ---

    // ... (Your existing mapToJsonString, jsonStringToMap, stringToLocalTime, localTimeToString methods) ...

    /**
     * Converts a LocalDateTime to an OffsetDateTime using UTC offset.
     * MapStruct will automatically pick this up for the 'savedAt' mapping.
     *
     * @param value The LocalDateTime to convert.
     * @return The converted OffsetDateTime, or null if the input is null.
     */
    // New method to convert LocalDateTime to OffsetDateTime
    default OffsetDateTime map(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        // Assuming UTC as the default offset for LocalDateTime stored without timezone info.
        // Adjust ZoneOffset.UTC if your application uses a different default timezone.
        return value.atOffset(ZoneOffset.UTC);
    }


    // --- Custom Mapping Methods (with @Named for qualifiedByName) ---

    @Named("mapToJsonString")
    default String mapToJsonString(Map<String, String> customSelectors) {
        if (customSelectors == null || customSelectors.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(customSelectors);
        } catch (JsonProcessingException e) {
            // ✨ CHANGE THIS LINE ✨ to include the specific detail and original cause
            throw new JsonConversionException(
                    "Error converting Map to JSON string: " + e.getMessage(), e // Pass original exception as cause
            );
        }
    }

    @Named("jsonStringToMap")
    default Map<String, String> jsonStringToMap(String customSelectorsJson) {
        if (customSelectorsJson == null || customSelectorsJson.trim().isEmpty()) {
            return Map.of();
        }
        try {
            return OBJECT_MAPPER.readValue(customSelectorsJson, new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
        } catch (JsonProcessingException e) {
            // ✨ CHANGE THIS LINE ✨ to include the specific detail and original cause
            throw new JsonConversionException(
                    "Error converting JSON string to Map: " + e.getMessage(), e // Pass original exception as cause
            );
        }
    }

    @Named("stringToLocalTime")
    default LocalTime stringToLocalTime(String timeString) {
        if (timeString == null || timeString.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalTime.parse(timeString);
        } catch (DateTimeParseException e) { // This is the critical catch block!
            // Throw your custom exception with a detailed message
            throw new InvalidTimeFormatException(
                    "Failed to parse time string '" + timeString + "': " + e.getMessage(), e // Use this for detailed test message
                    // OR: new InvalidTimeFormatException(FocusShieldErrorCode.DATA_ERROR_INVALID_TIME_FORMAT) if you prefer generic message
            );
        }
    }

    @Named("localTimeToString")
    default String localTimeToString(LocalTime localTime) {
        return (localTime != null) ? localTime.toString() : null;
    }

    // --- Static helper method (ensure this also handles exceptions correctly) ---
    // This is the method used in your 'toEntity_static_shouldThrow...' tests
    static FocusConfigEntity toEntity(FocusConfigRequest request, UserEntity user) {
        if (request == null || user == null) {
            throw new IllegalArgumentException("Request and user must not be null");
        }

        String customSelectorsJson = null;
        if (request.getCustomSelectors() != null && !request.getCustomSelectors().isEmpty()) {
            try {
                customSelectorsJson = OBJECT_MAPPER.writeValueAsString(request.getCustomSelectors());
            } catch (JsonProcessingException e) {
                throw new JsonConversionException(FocusShieldErrorCode.DATA_ERROR_JSON_CONVERSION);
            }
        }

        LocalTime pauseStartTime = null;
        if (request.getPauseStartTime() != null && !request.getPauseStartTime().isEmpty()) {
            try {
                pauseStartTime = LocalTime.parse(request.getPauseStartTime());
            } catch (DateTimeParseException e) {
                throw new InvalidTimeFormatException(FocusShieldErrorCode.DATA_ERROR_INVALID_TIME_FORMAT);
            }
        }

        LocalTime pauseEndTime = null;
        if (request.getPauseEndTime() != null && !request.getPauseEndTime().isEmpty()) {
            try {
                pauseEndTime = LocalTime.parse(request.getPauseEndTime());
            } catch (DateTimeParseException e) {
                throw new InvalidTimeFormatException(FocusShieldErrorCode.DATA_ERROR_INVALID_TIME_FORMAT);
            }
        }

        return FocusConfigEntity.builder()
                .blockedSites(request.getBlockedSites())
                .durationMinutes(request.getDurationMinutes())
                .active(Boolean.TRUE.equals(request.getActive()))
                .savedAt(LocalDateTime.now())
                .user(user)
                .customSelectorsJson(customSelectorsJson)
                .pauseStartTime(pauseStartTime)
                .pauseEndTime(pauseEndTime)
                .build();
    }
}