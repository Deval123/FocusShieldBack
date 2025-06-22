package com.dev.focusshield.service.roles;

import com.dev.focusshield.model.RoleRequest;
import com.dev.focusshield.model.RoleResponse;
import com.dev.focusshield.model.RoleUpdateRequest;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface RoleService {
    RoleResponse createRole(RoleRequest role);
    Page<RoleResponse> getAllRoles(int page, int size);
    Optional<RoleResponse> getRoleById(UUID roleId);
    Optional <RoleResponse> updateRole(UUID roleId, RoleUpdateRequest role);
    void deleteRole(UUID roleId);
}
