package com.dev.focusshield.utils.mappers;

import com.dev.focusshield.entities.RoleEntity;
import com.dev.focusshield.model.RoleRequest;
import com.dev.focusshield.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;


@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);
    @Mapping(source = "roleId",target = "roleId")
    @Mapping(source = "roleName",target = "roleName")
    @Mapping(source = "level",target = "level")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    Role roleEntityToRole(RoleEntity roleEntity);


    @Mapping(source = "roleName",target = "roleName")
    @Mapping(source = "level",target = "level")
    RoleEntity roleToRoleEntity(RoleRequest role);

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
