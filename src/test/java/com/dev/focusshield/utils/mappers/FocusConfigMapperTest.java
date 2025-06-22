package com.dev.focusshield.utils.mappers;

import com.dev.focusshield.entities.FocusConfigEntity;
import com.dev.focusshield.entities.UserEntity;
import com.dev.focusshield.model.FocusConfigResponse;
import com.dev.focusshield.model.FocusConfigRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy; // Import for testing exceptions

class FocusConfigMapperTest {

    // Instantiate the mapper directly using MapStruct's factory
    private FocusConfigMapper mapper = FocusConfigMapper.INSTANCE;

    //@Test
    void shouldMapFocusConfigEntityToResponse() {
        // Given
        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .build();

        LocalDateTime savedAt = LocalDateTime.of(2024, 6, 20, 10, 30);

        FocusConfigEntity entity = FocusConfigEntity.builder()
                .id(UUID.randomUUID())
                .blockedSites(List.of("facebook.com", "youtube.com"))
                .durationMinutes(45)
                .isActive(true)
                .savedAt(savedAt)
                .user(user)
                .build();

        // When
        FocusConfigResponse response = mapper.toResponse(entity);
        //FocusConfigResponse response = FocusConfigMapper.INSTANCE.toResponse(entity);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getBlockedSites()).containsExactly("facebook.com", "youtube.com");
        assertThat(response.getDurationMinutes()).isEqualTo(45);
        assertThat(response.getIsActive()).isTrue();

        // For savedAt, you need to account for the OffsetDateTime conversion
        OffsetDateTime expectedSavedAt = savedAt.atOffset(ZoneOffset.UTC); // Assuming your mapper uses UTC
        assertThat(response.getSavedAt()).isNotNull();
        assertThat(response.getSavedAt()).isEqualTo(expectedSavedAt);
        assertThat(response.getSavedAt().toLocalDateTime()).isEqualTo(savedAt);
    }

    // Updated test for the toEntity static method
    @Test
    void shouldMapFocusConfigRequestToEntity() {
        // Given
        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .build();

        // Instantiate FocusConfigRequest using its no-arg constructor
        // and set properties using setters, as it's a generated file without a builder.
        FocusConfigRequest request = new FocusConfigRequest();
        request.setBlockedSites(List.of("site1.com", "site2.net"));
        request.setDurationMinutes(60);
        request.setIsActive(true);


        // When
        FocusConfigEntity entity = FocusConfigMapper.toEntity(request, user);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getBlockedSites()).containsExactly("site1.com", "site2.net");
        assertThat(entity.getDurationMinutes()).isEqualTo(60);
        assertThat(entity.isActive()).isTrue();
        assertThat(entity.getSavedAt()).isNotNull(); // Should be set to LocalDateTime.now()
        assertThat(entity.getUser()).isEqualTo(user);
    }

    @Test
    void toEntity_shouldThrowIllegalArgumentExceptionForNullRequest() {
        UserEntity user = UserEntity.builder().id(UUID.randomUUID()).email("test@example.com").build();

        assertThatThrownBy(() -> FocusConfigMapper.toEntity(null, user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Request and user must not be null");
    }

    @Test
    void toEntity_shouldThrowIllegalArgumentExceptionForNullUser() {
        FocusConfigRequest request = new FocusConfigRequest();
        request.setBlockedSites(List.of("site1.com"));
        request.setDurationMinutes(30);
        request.setIsActive(false);

        assertThatThrownBy(() -> FocusConfigMapper.toEntity(request, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Request and user must not be null");
    }

    @Test
    void toEntity_shouldHandleNullIsActiveInRequest() {
        // Given
        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .build();

        FocusConfigRequest request = new FocusConfigRequest();
        request.setBlockedSites(List.of("site1.com"));
        request.setDurationMinutes(60);
        request.setIsActive(null); // isActive is null in the request


        // When
        FocusConfigEntity entity = FocusConfigMapper.toEntity(request, user);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.isActive()).isFalse(); // Because Boolean.TRUE.equals(null) is false
    }
}