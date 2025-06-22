package com.dev.focusshield.utils.mappers;


import com.dev.focusshield.entities.FocusConfigEntity;
import com.dev.focusshield.entities.UserEntity;
import com.dev.focusshield.model.FocusConfigRequest;
import com.dev.focusshield.model.FocusConfigResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset; // Keep this import for ZoneOffset

@Mapper(componentModel = "spring")
public interface FocusConfigMapper {
    // You can keep this, but it's generally not used when componentModel = "spring"
    // as Spring manages the instance. It doesn't hurt to have it.
    FocusConfigMapper INSTANCE = Mappers.getMapper(FocusConfigMapper.class);


    @Mapping(target = "blockedSites", source = "blockedSites")
    @Mapping(target = "durationMinutes", source = "durationMinutes")
    // Assuming 'active' is the field name in the source entity (FocusConfigEntity)
    // and 'isActive' is the field name in the target DTO (FocusConfigRequest)
    @Mapping(target = "isActive", source = "active")
    FocusConfigRequest toDto(FocusConfigEntity entity);


    /**
     * Map a FocusConfigRequest to a FocusConfigEntity.
     *
     * @param request the incoming request
     * @param user    the user associated with the config
     * @return a FocusConfigEntity ready for persistence
     */
    // This static method is fine as it is. It's a utility method, not part of MapStruct's
    // generated mapping logic for the interface methods directly.
    public static FocusConfigEntity toEntity(FocusConfigRequest request, UserEntity user) {
        if (request == null || user == null) {
            throw new IllegalArgumentException("Request and user must not be null");
        }

        return FocusConfigEntity.builder()
                .blockedSites(request.getBlockedSites())
                .durationMinutes(request.getDurationMinutes())
                .isActive(Boolean.TRUE.equals(request.getIsActive())) // avoid null
                .savedAt(LocalDateTime.now())
                .user(user)
                .build();
    }

    @Mapping(source = "savedAt", target = "savedAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    FocusConfigResponse toResponse(FocusConfigEntity entity);

    @Named("localDateTimeToOffsetDateTime")
    default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime) { // Renamed 'map' to be more descriptive
        // This null check is already handled by MapStruct's default behavior,
        // but explicitly checking is harmless and makes it clear.
        if (localDateTime == null) {
            return null;
        }
        // Use ZoneOffset.UTC or another appropriate offset for your application.
        // As of Friday, June 20, 2025 at 2:45:12 PM CEST, using UTC is a good
        // general practice for backend APIs unless a specific time zone is required.
        return localDateTime.atOffset(ZoneOffset.UTC);
    }
}