package com.dev.focusshield.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
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

    private boolean active;

    @CreationTimestamp
    private LocalDateTime savedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // Option 1 (Recommended for flexibility): Store customSelectors as a JSON String
    // This requires a custom converter or handling JSON serialization/deserialization manually.
    // This is the most flexible if you use `Map<String, String>` in your DTOs.
    @Column(name = "custom_selectors", columnDefinition = "TEXT") // Use TEXT or JSONB (PostgreSQL)
    private String customSelectorsJson; // Store as JSON string

    @Column(name = "pause_start_time")
    private LocalTime pauseStartTime;

    @Column(name = "pause_end_time")
    private LocalTime pauseEndTime;


}