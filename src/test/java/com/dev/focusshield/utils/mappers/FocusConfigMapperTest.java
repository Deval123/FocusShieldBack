package com.dev.focusshield.utils.mappers;

import com.dev.focusshield.entities.FocusConfigEntity;
import com.dev.focusshield.entities.UserEntity;
import com.dev.focusshield.exceptions.InvalidTimeFormatException;
import com.dev.focusshield.exceptions.JsonConversionException;
import com.dev.focusshield.model.FocusConfig;
import com.dev.focusshield.model.FocusConfigRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FocusConfigMapperTest {

    // Instantiate the mapper directly using MapStruct's factory
    private FocusConfigMapper mapper; // Changed to non-final to allow setup in @BeforeEach

    @BeforeEach
    void setUp() {
        mapper = FocusConfigMapper.INSTANCE;
    }

    // --- Tests for toResponse (Entity to DTO) ---


    void shouldMapFocusConfigEntityToResponse() {
        // Given
        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID())
                .universalId(UUID.randomUUID()) // Assuming universalId is used for userId mapping
                .email("test@example.com")
                .build();

        LocalDateTime savedAt = LocalDateTime.of(2024, 6, 20, 10, 30);
        LocalTime pauseStart = LocalTime.of(9, 0);
        LocalTime pauseEnd = LocalTime.of(17, 0);

        // Prepare JSON string for customSelectorsJson
        Map<String, String> customSelectorsMap = new HashMap<>();
        customSelectorsMap.put("youtube", "#video-player");
        customSelectorsMap.put("linkedin", ".feed-container");
        String customSelectorsJson;
        try {
            customSelectorsJson = FocusConfigMapper.OBJECT_MAPPER.writeValueAsString(customSelectorsMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize map for test setup", e);
        }


        FocusConfigEntity entity = FocusConfigEntity.builder()
                .id(UUID.randomUUID())
                .blockedSites(List.of("facebook.com", "instagram.com"))
                .durationMinutes(45)
                .active(true)
                .savedAt(savedAt)
                .user(user)
                .customSelectorsJson(customSelectorsJson) // Set the JSON string
                .pauseStartTime(pauseStart)
                .pauseEndTime(pauseEnd)
                .build();

        // When
        FocusConfig response = mapper.toResponse(entity);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(entity.getId());
        assertThat(response.getBlockedSites()).containsExactly("facebook.com", "instagram.com");
        assertThat(response.getDurationMinutes()).isEqualTo(45);
        assertThat(response.getActive()).isTrue();

        OffsetDateTime expectedSavedAt = savedAt.atOffset(ZoneOffset.UTC);
        assertThat(response.getSavedAt()).isEqualTo(expectedSavedAt);

        assertThat(response.getUserId()).isEqualTo(user.getUniversalId()); // Check userId mapping

        // Check customSelectors (should be mapped back to Map<String, String>)
        assertThat(response.getCustomSelectors()).isEqualTo(customSelectorsMap);
        assertThat(response.getCustomSelectors()).containsKey("youtube");
        assertThat(response.getCustomSelectors()).containsValue("#video-player");

        // Check pause times (should be mapped to String in DTO)
        assertThat(response.getPauseStartTime()).isEqualTo(pauseStart.toString()); // LocalTime.toString()
        assertThat(response.getPauseEndTime()).isEqualTo(pauseEnd.toString());     // LocalTime.toString()
    }

    @Test
    void shouldHandleNullCustomSelectorsInEntityToResponse() {
        // Given
        FocusConfigEntity entity = FocusConfigEntity.builder()
                .id(UUID.randomUUID())
                .blockedSites(List.of())
                .durationMinutes(10)
                .active(false)
                .savedAt(LocalDateTime.now())
                .user(UserEntity.builder().id(UUID.randomUUID()).universalId(UUID.randomUUID()).build())
                .customSelectorsJson(null) // Null JSON string
                .pauseStartTime(null)
                .pauseEndTime(null)
                .build();

        // When
        FocusConfig response = mapper.toResponse(entity);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCustomSelectors()).isNotNull().isEmpty(); // Should map to an empty map
        assertThat(response.getPauseStartTime()).isNull();
        assertThat(response.getPauseEndTime()).isNull();
    }

    @Test
    void shouldHandleEmptyCustomSelectorsJsonInEntityToResponse() {
        // Given
        FocusConfigEntity entity = FocusConfigEntity.builder()
                .id(UUID.randomUUID())
                .blockedSites(List.of())
                .durationMinutes(10)
                .active(false)
                .savedAt(LocalDateTime.now())
                .user(UserEntity.builder().id(UUID.randomUUID()).universalId(UUID.randomUUID()).build())
                .customSelectorsJson("") // Empty JSON string
                .pauseStartTime(null)
                .pauseEndTime(null)
                .build();

        // When
        FocusConfig response = mapper.toResponse(entity);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCustomSelectors()).isNotNull().isEmpty(); // Should map to an empty map
    }

    @Test
    void shouldThrowJsonConversionExceptionWhenInvalidJsonInEntityToResponse() {
        // Given
        FocusConfigEntity entity = FocusConfigEntity.builder()
                .id(UUID.randomUUID())
                .blockedSites(List.of())
                .durationMinutes(10)
                .active(false)
                .savedAt(LocalDateTime.now())
                .user(UserEntity.builder().id(UUID.randomUUID()).universalId(UUID.randomUUID()).build())
                .customSelectorsJson("{invalid json") // Invalid JSON string
                .build();

        // When/Then
        assertThatThrownBy(() -> mapper.toResponse(entity))
                .isInstanceOf(JsonConversionException.class)
                .hasMessageContaining("Error converting JSON string to Map");
    }

    // --- Tests for toEntity (DTO to Entity, using MapStruct generated method) ---

    @Test
    void shouldMapFocusConfigRequestToEntityUsingMapperMethod() {
        // Given
        FocusConfigRequest request = new FocusConfigRequest();
        request.setBlockedSites(List.of("site1.com", "site2.net"));
        request.setDurationMinutes(60);
        request.setActive(true);

        Map<String, String> customSelectorsRequest = new HashMap<>();
        customSelectorsRequest.put("facebook", ".ad-block");
        request.setCustomSelectors(customSelectorsRequest);

        request.setPauseStartTime(LocalTime.of(10, 0).toString()); // String representation
        request.setPauseEndTime(LocalTime.of(18, 30).toString());   // String representation

        // When
        FocusConfigEntity entity = mapper.toEntity(request); // Use the MapStruct generated method

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getBlockedSites()).containsExactly("site1.com", "site2.net");
        assertThat(entity.getDurationMinutes()).isEqualTo(60);
        assertThat(entity.isActive()).isTrue();
        assertThat(entity.getSavedAt()).isNull(); // @CreationTimestamp handles this on persist
        assertThat(entity.getUser()).isNull(); // User is ignored in mapper, set in service layer

        // Check customSelectorsJson
        String expectedCustomSelectorsJson;
        try {
            expectedCustomSelectorsJson = FocusConfigMapper.OBJECT_MAPPER.writeValueAsString(customSelectorsRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize map for test assertion", e);
        }
        assertThat(entity.getCustomSelectorsJson()).isEqualTo(expectedCustomSelectorsJson);

        // Check pause times (should be mapped to LocalTime)
        assertThat(entity.getPauseStartTime()).isEqualTo(LocalTime.of(10, 0));
        assertThat(entity.getPauseEndTime()).isEqualTo(LocalTime.of(18, 30));
    }

    @Test
    void shouldHandleNullAndEmptyFieldsInFocusConfigRequestToEntity() {
        // Given
        FocusConfigRequest request = new FocusConfigRequest();
        request.setBlockedSites(null); // Test null list
        request.setDurationMinutes(null); // Test null Integer
        request.setActive(null); // Test null Boolean
        request.setCustomSelectors(Collections.emptyMap()); // Test empty map
        request.setPauseStartTime(null); // Test null time string
        request.setPauseEndTime(""); // Test empty time string

        // When
        FocusConfigEntity entity = mapper.toEntity(request);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getBlockedSites()).isNull(); // Or empty list if your DTO handles it that way
        assertThat(entity.getDurationMinutes()).isNull();
        assertThat(entity.isActive()).isFalse(); // Active defaults to false if source is null
        assertThat(entity.getCustomSelectorsJson()).isNull(); // Empty map converted to null JSON
        assertThat(entity.getPauseStartTime()).isNull();
        assertThat(entity.getPauseEndTime()).isNull();
    }


    @Test
    void shouldThrowInvalidTimeFormatExceptionForInvalidPauseStartTimeInRequest() {
        // Given
        FocusConfigRequest request = new FocusConfigRequest();
        request.setPauseStartTime("invalid-time"); // Invalid time format
        request.setPauseEndTime("10:00"); // Valid

        // When/Then
        assertThatThrownBy(() -> mapper.toEntity(request))
                .isInstanceOf(InvalidTimeFormatException.class)
                .hasMessageContaining("Failed to parse time string 'invalid-time'");
    }

    @Test
    void shouldThrowInvalidTimeFormatExceptionForInvalidPauseEndTimeInRequest() {
        // Given
        FocusConfigRequest request = new FocusConfigRequest();
        request.setPauseStartTime("09:00"); // Valid
        request.setPauseEndTime("not-a-time"); // Invalid time format

        // When/Then
        assertThatThrownBy(() -> mapper.toEntity(request))
                .isInstanceOf(InvalidTimeFormatException.class)
                .hasMessageContaining("Failed to parse time string 'not-a-time'");
    }

    @Test
    void shouldThrowJsonConversionExceptionForInvalidCustomSelectorsMapToJson() {
        // Given
        FocusConfigRequest request = new FocusConfigRequest();
        // Create a map that Jackson cannot serialize (e.g., a self-referencing map)
        Map<String, Object> problematicMap = new HashMap<>();
        problematicMap.put("self", problematicMap); // This will cause a JsonProcessingException
        // Cast is necessary if your DTO expects Map<String, String>,
        // but this demonstrates a scenario where the conversion fails.
        // For actual usage, your DTO should strictly be Map<String, String> as per OpenAPI.
        // This test simulates a scenario where an unexpected type might sneak in or a configuration issue.
        request.setCustomSelectors((Map<String, String>) (Map) problematicMap);


        // When/Then
        assertThatThrownBy(() -> mapper.toEntity(request))
                .isInstanceOf(JsonConversionException.class)
                .hasMessageContaining("Error converting Map to JSON string");
    }


    // --- Tests for the static toEntity helper method ---

    @Test
    void shouldMapFocusConfigRequestToEntityUsingStaticMethod() {
        // Given
        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .universalId(UUID.randomUUID()) // Universal ID might be needed for user
                .build();

        FocusConfigRequest request = new FocusConfigRequest();
        request.setBlockedSites(List.of("site3.com"));
        request.setDurationMinutes(90);
        request.setActive(false);

        Map<String, String> customSelectorsRequest = new HashMap<>();
        customSelectorsRequest.put("twitter", ".tweet");
        request.setCustomSelectors(customSelectorsRequest);

        request.setPauseStartTime("12:00");
        request.setPauseEndTime("13:00");

        // When
        FocusConfigEntity entity = FocusConfigMapper.toEntity(request, user); // Use the static method

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getBlockedSites()).containsExactly("site3.com");
        assertThat(entity.getDurationMinutes()).isEqualTo(90);
        assertThat(entity.isActive()).isFalse();
        assertThat(entity.getSavedAt()).isNotNull(); // Static method explicitly sets LocalDateTime.now()
        assertThat(entity.getUser()).isEqualTo(user);

        // Check customSelectorsJson
        String expectedCustomSelectorsJson;
        try {
            expectedCustomSelectorsJson = FocusConfigMapper.OBJECT_MAPPER.writeValueAsString(customSelectorsRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize map for test assertion", e);
        }
        assertThat(entity.getCustomSelectorsJson()).isEqualTo(expectedCustomSelectorsJson);

        // Check pause times (should be mapped to LocalTime)
        assertThat(entity.getPauseStartTime()).isEqualTo(LocalTime.of(12, 0));
        assertThat(entity.getPauseEndTime()).isEqualTo(LocalTime.of(13, 0));
    }

    // Existing tests for static method (adapted slightly)
    @Test
    void toEntity_static_shouldThrowIllegalArgumentExceptionForNullRequest() {
        UserEntity user = UserEntity.builder().id(UUID.randomUUID()).email("test@example.com").build();
        assertThatThrownBy(() -> FocusConfigMapper.toEntity(null, user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Request and user must not be null");
    }

    @Test
    void toEntity_static_shouldThrowIllegalArgumentExceptionForNullUser() {
        FocusConfigRequest request = new FocusConfigRequest();
        request.setBlockedSites(List.of("site1.com"));
        request.setDurationMinutes(30);
        request.setActive(false);

        assertThatThrownBy(() -> FocusConfigMapper.toEntity(request, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Request and user must not be null");
    }

    @Test
    void toEntity_static_shouldHandleNullIsActiveInRequest() {
        // Given
        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .build();

        FocusConfigRequest request = new FocusConfigRequest();
        request.setBlockedSites(List.of("site1.com"));
        request.setDurationMinutes(60);
        request.setActive(null); // isActive is null in the request


        // When
        FocusConfigEntity entity = FocusConfigMapper.toEntity(request, user);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.isActive()).isFalse(); // Because Boolean.TRUE.equals(null) is false
    }

    @Test
    void toEntity_static_shouldThrowInvalidTimeFormatExceptionForInvalidPauseStartTime() {
        // Given
        UserEntity user = UserEntity.builder().id(UUID.randomUUID()).email("test@example.com").build();
        FocusConfigRequest request = new FocusConfigRequest();
        request.setPauseStartTime("invalid");

        // When/Then
        assertThatThrownBy(() -> FocusConfigMapper.toEntity(request, user))
                .isInstanceOf(InvalidTimeFormatException.class)
                // Change this line:
                .hasMessage("400-011 : Invalid time format provided."); // Expect the exact error code message
    }

    @Test
    void toEntity_static_shouldThrowInvalidTimeFormatExceptionForInvalidPauseEndTime() {
        // Given
        UserEntity user = UserEntity.builder().id(UUID.randomUUID()).email("test@example.com").build();
        FocusConfigRequest request = new FocusConfigRequest();
        request.setPauseEndTime("not_a_time");

        // When/Then
        assertThatThrownBy(() -> FocusConfigMapper.toEntity(request, user))
                .isInstanceOf(InvalidTimeFormatException.class)
                // Change this line:
                .hasMessage("400-011 : Invalid time format provided."); // Expect the exact error code message
    }

    @Test
    void toEntity_static_shouldThrowJsonConversionExceptionForInvalidCustomSelectors() {
        UserEntity user = UserEntity.builder().id(UUID.randomUUID()).email("test@example.com").build();
        FocusConfigRequest request = new FocusConfigRequest();
        Map<String, Object> problematicMap = new HashMap<>();
        problematicMap.put("self", problematicMap);
        request.setCustomSelectors((Map<String, String>) (Map) problematicMap);

        assertThatThrownBy(() -> FocusConfigMapper.toEntity(request, user))
                .isInstanceOf(JsonConversionException.class)
                .hasMessage("400-010 : Error during JSON data conversion."); // Changed assertion
    }
}