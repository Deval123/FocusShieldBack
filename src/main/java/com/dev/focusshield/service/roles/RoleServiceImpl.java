package com.dev.focusshield.service.roles;

import com.dev.focusshield.entities.RoleEntity;
import com.dev.focusshield.model.RoleRequest;
import com.dev.focusshield.model.RoleResponse;
import com.dev.focusshield.model.RoleUpdateRequest;
import com.dev.focusshield.repositories.RoleRepository;
import com.dev.focusshield.utils.mappers.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    /**
     * @param roleRequest
     * @return
     */
    @Override
    public RoleResponse createRole(RoleRequest roleRequest) {
        RoleEntity entity = roleMapper.roleToRoleEntity(roleRequest);
        entity.setRoleId(UUID.randomUUID());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        RoleEntity saved = roleRepository.save(entity);
        return roleMapper.roleEntityToRole(saved);    }

    /**
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<RoleResponse> getAllRoles(int page, int size) {
        return roleRepository.findAll(PageRequest.of(page, size))
                .map(roleMapper::roleEntityToRole);    }

    /**
     * @param roleId
     * @return
     */
    @Override
    public Optional<RoleResponse> getRoleById(UUID roleId) {
        return roleRepository.findById(roleId)
                .map(roleMapper::roleEntityToRole);    }

    /**
     * @param roleId
     * @param updateRequest
     * @return
     */
    @Override
    public Optional<RoleResponse> updateRole(UUID roleId, RoleUpdateRequest updateRequest) {
        return roleRepository.findById(roleId).map(existing -> {
            existing.setRoleName(updateRequest.getRoleName());
            existing.setLevel(updateRequest.getLevel());
            existing.setUpdatedAt(LocalDateTime.now());
            RoleEntity updated = roleRepository.save(existing);
            return roleMapper.roleEntityToRole(updated);
        });    }

    /**
     * @param roleId
     */
    @Override
    public void deleteRole(UUID roleId) {
        roleRepository.deleteById(roleId);
    }
}
