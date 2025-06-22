package com.dev.focusshield.service.roles;

import com.dev.focusshield.model.RoleRequest;
import com.dev.focusshield.model.Role;
import com.dev.focusshield.model.RoleUpdateRequest;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface RoleService {
    Role createRole(RoleRequest role);
    Page<Role> getAllRoles(int page, int size);
    Optional<Role> getRoleById(UUID roleId);
    Optional <Role> updateRole(UUID roleId, RoleUpdateRequest role);
    void deleteRole(UUID roleId);
}
