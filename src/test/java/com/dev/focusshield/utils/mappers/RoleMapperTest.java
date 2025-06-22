package com.dev.focusshield.utils.mappers;

import com.dev.focusshield.entities.RoleEntity;
import com.dev.focusshield.model.RoleRequest;
import com.dev.focusshield.model.RoleResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RoleMapperTest {

    private final RoleMapper mapper = RoleMapper.INSTANCE;

    @Test
    void shouldMapRoleEntityToRoleResponse() {
        UUID roleId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        OffsetDateTime nowOffset = now.atOffset(ZoneOffset.UTC);

        RoleEntity entity = RoleEntity.builder()
                .roleId(roleId)
                .roleName("ADMIN")
                .level(1)
                .createdAt(now)
                .updatedAt(now)
                .build();

        RoleResponse response = mapper.roleEntityToRole(entity);

        assertThat(response).isNotNull();
        assertThat(response.getRoleId()).isEqualTo(roleId);
        assertThat(response.getRoleName()).isEqualTo("ADMIN");
        assertThat(response.getLevel()).isEqualTo(1);
        assertThat(response.getCreatedAt()).isEqualTo(nowOffset);
        assertThat(response.getUpdatedAt()).isEqualTo(nowOffset);
    }

    @Test
    void shouldMapRoleRequestToRoleEntity() {
        RoleRequest request = new RoleRequest();
        request.setRoleName("USER");
        request.setLevel(2);

        RoleEntity entity = mapper.roleToRoleEntity(request);

        assertThat(entity).isNotNull();
        assertThat(entity.getRoleName()).isEqualTo("USER");
        assertThat(entity.getLevel()).isEqualTo(2);
    }
}