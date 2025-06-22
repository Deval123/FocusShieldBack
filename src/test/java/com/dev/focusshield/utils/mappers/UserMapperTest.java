package com.dev.focusshield.utils.mappers;

import com.dev.focusshield.entities.UserEntity;
import com.dev.focusshield.model.RegisterRequest;
import com.dev.focusshield.model.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Test
    void shouldMapUserEntityToUser() {
        // Given
        UserEntity userEntity = new UserEntity();
        userEntity.setId(UUID.randomUUID());
        userEntity.setUsername("john_doe");
        userEntity.setEmail("john@example.com");

        // When
        User user = userMapper.userEntityToUser(userEntity);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("john_doe");
        assertThat(user.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void shouldMapRegisterRequestToUserEntity() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("jane_doe");
        registerRequest.setEmail("jane@example.com");
        registerRequest.setPassword("securePassword123");

        // When
        UserEntity userEntity = userMapper.registerRequestToUserEntity(registerRequest);

        // Then
        assertThat(userEntity).isNotNull();
        assertThat(userEntity.getUsername()).isEqualTo("jane_doe");
        assertThat(userEntity.getEmail()).isEqualTo("jane@example.com");
        assertThat(userEntity.getPassword()).isEqualTo("securePassword123");
    }
}