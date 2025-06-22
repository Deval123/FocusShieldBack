package com.dev.focusshield.service.roles;

import com.dev.focusshield.exceptions.FocusShieldNotFoundException;
import com.dev.focusshield.model.Role;
import com.dev.focusshield.model.RoleRequest;
import com.dev.focusshield.model.RoleUpdateRequest;
import com.dev.focusshield.repositories.RoleRepository;
import com.dev.focusshield.utils.mappers.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.dev.focusshield.exceptions.FocusShieldErrorCode.DATA_ERROR_ROLE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    /**
     * Creates a new role based on the provided {@link RoleRequest}.
     *
     * @param roleRequest the request object containing role name and level
     * @return the created role as a {@link Role}
     */
    @Override
    public Role createRole(RoleRequest roleRequest) {
        var roleEntity = roleMapper.roleToRoleEntity(roleRequest);
        var savedRole = roleRepository.save(roleEntity);
        return roleMapper.roleEntityToRole(savedRole);
    }

    /**
     * Retrieves all roles with pagination.
     *
     * @param page the page number (0-based)
     * @param size the number of items per page
     * @return a page of {@link Role} objects
     */
    @Override
    public Page<Role> getAllRoles(int page, int size) {
        return roleRepository.findAll(PageRequest.of(page, size))
                .map(roleMapper::roleEntityToRole);
    }

    /**
     * Retrieves a role by its unique identifier.
     *
     * @param roleId the UUID of the role to retrieve
     * @return an {@link Optional} containing the {@link Role} if found, or empty otherwise
     */
    @Override
    public Optional<Role> getRoleById(UUID roleId) {
        return roleRepository.findById(roleId)
                .map(roleMapper::roleEntityToRole);
    }

    /**
     * Updates an existing role by its ID using the provided {@link RoleUpdateRequest}.
     *
     * @param roleId the UUID of the role to update
     * @param updateRequest the object containing an updated role name and level
     * @return an {@link Optional} containing the updated {@link Role}, or empty if not found
     */
    @Override
    public Optional<Role> updateRole(UUID roleId, RoleUpdateRequest updateRequest) {
        return roleRepository.findById(roleId)
                .map(entity -> {
                    Optional.of(updateRequest.getRoleName()).ifPresent(entity::setRoleName);
                    Optional.of(updateRequest.getLevel()).ifPresent(entity::setLevel);
                    return roleRepository.save(entity);
                })
                .map(RoleMapper.INSTANCE::roleEntityToRole);
    }

    /**
     * Deletes a role by its unique identifier.
     *
     * @param roleId the UUID of the role to delete
     */
    @Override
    public void deleteRole(UUID roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new FocusShieldNotFoundException(DATA_ERROR_ROLE_NOT_FOUND);
        }
        roleRepository.deleteById(roleId);
    }
}