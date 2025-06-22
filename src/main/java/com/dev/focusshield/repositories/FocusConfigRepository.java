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
    Optional<FocusConfigEntity> findTopByIsActiveOrderBySavedAtDesc(boolean isActive);

}
