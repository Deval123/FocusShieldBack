package com.dev.focusshield.repositories;

import com.dev.focusshield.entities.FocusConfigEntity;
import com.dev.focusshield.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FocusConfigRepository extends JpaRepository<FocusConfigEntity, UUID> {
    List<FocusConfigEntity> findByUser(UserEntity user);
    /**
     * Finds the top (most recent) FocusConfigEntity for a given user,
     * where the configuration is active, ordered by savedAt in descending order.
     *
     * @param user The UserEntity for whom to find the configuration.
     * @param isActive A boolean indicating if the configuration should be active.
     * @return An Optional containing the latest active FocusConfigEntity, or empty if not found.
     */
    Optional<FocusConfigEntity> findTopByUserAndActiveOrderBySavedAtDesc(UserEntity user, boolean isActive);

}
