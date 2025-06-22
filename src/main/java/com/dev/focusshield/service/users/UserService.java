package com.dev.focusshield.service.users;

import com.dev.focusshield.entities.UserEntity;
import com.dev.focusshield.model.AuthResponse;
import com.dev.focusshield.model.RegisterRequest;
import com.dev.focusshield.model.UpdateRequest;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    AuthResponse register(RegisterRequest registerRequest);
    Page<UserEntity> getAllUsers(int page, int size);
    Optional<UserEntity> getUserById(UUID userId);
    Optional <UserEntity> updateUser(UUID userId, UpdateRequest updateRequest);
    void deleteUser(UUID userId);
}
