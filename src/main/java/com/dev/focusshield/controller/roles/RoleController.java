package com.dev.focusshield.controller.roles;

import com.dev.focusshield.model.RoleRequest;
import com.dev.focusshield.model.Role;
import com.dev.focusshield.model.RoleUpdateRequest;
import com.dev.focusshield.service.roles.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

import static com.dev.focusshield.utils.contants.ApiRoutes.ROLES;

@RestController
@RequestMapping(ROLES)
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody RoleRequest request) {
        Role response = roleService.createRole(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<Role>> getAllRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Role> roles = roleService.getAllRoles(page, size);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<Role> getRoleById(@PathVariable UUID roleId) {
        Optional<Role> role = roleService.getRoleById(roleId);
        return role.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<Role> updateRole(
            @PathVariable UUID roleId,
            @RequestBody RoleUpdateRequest request
    ) {
        Optional<Role> updated = roleService.updateRole(roleId, request);
        return updated.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(@PathVariable("roleId") UUID roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.noContent().build();
    }
}
