package com.dev.focusshield.service.users;

import com.dev.focusshield.model.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User register(RegisterRequest registerRequest);
    Page<User> getAllUsers(int page, int size);
    Optional<User> getUserById(UUID userId);
    Optional <User> updateUser(UUID userId, UpdateRequest updateRequest);
    void deleteUser(UUID userId);

    User assignRoleToUser(String email, String role);

    User unassignRoleToUser(String email, String role);

    AuthResponse login(@Valid LoginRequest request);
}
