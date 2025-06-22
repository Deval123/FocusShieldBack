package com.dev.focusshield.entities;


import com.google.gson.annotations.SerializedName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "focus_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FocusConfigEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ElementCollection
    @CollectionTable(name = "blocked_sites", joinColumns = @JoinColumn(name = "config_id"))
    @Column(name = "site")
    private List<String> blockedSites;

    private Integer durationMinutes;

    private boolean isActive;

    private LocalDateTime savedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

}