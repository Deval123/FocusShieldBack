package com.dev.focusshield.service;

import com.dev.focusshield.entities.AccountStatus;
import com.dev.focusshield.entities.RoleEntity;
import com.dev.focusshield.entities.UserEntity;
import com.dev.focusshield.repositories.RoleRepository;
import com.dev.focusshield.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    @Override
    public void run(String... args) {

        // 1. Ensure ROLE_USER exists
        roleRepository.findByRoleName(ROLE_USER).orElseGet(() -> {
            RoleEntity userRole = RoleEntity.builder()
                    .roleName(ROLE_USER)
                    .level(1)
                    .build();
            log.info("🛠️ Creating default user role: {}", ROLE_USER);
            return roleRepository.save(userRole);
        });

        // 2. Ensure ROLE_ADMIN exists
        RoleEntity adminRole = roleRepository.findByRoleName(ROLE_ADMIN).orElseGet(() -> {
            RoleEntity role = RoleEntity.builder()
                    .roleName(ROLE_ADMIN)
                    .level(1)
                    .build();
            log.info("🛠️ Creating default admin role: {}", ROLE_ADMIN);
            return roleRepository.save(role);
        });

        // 3. Create a default admin user if not exists
        boolean adminExists = userRepository.findByUsername(adminUsername).isPresent()
                || userRepository.findByEmail(adminEmail).isPresent();

        if (adminExists) {
            log.info("ℹ️ Admin user already exists with username or email: {} / {}", adminUsername, adminEmail);
        } else {
            UserEntity admin = UserEntity.builder()
                    .username(adminUsername)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .status(AccountStatus.VALIDATED)
                    .roles(List.of(adminRole))
                    .build();
            userRepository.save(admin);
            log.info("✅ Default admin user created: {}", adminUsername);
        }
    }
}