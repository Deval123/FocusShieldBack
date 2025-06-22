package com.dev.focusshield.entities;

import com.google.gson.annotations.SerializedName;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    public static final String SERIALIZED_NAME_FIRSTNAME = "firstname";
    @SerializedName(SERIALIZED_NAME_FIRSTNAME)
    private String firstname;

    public static final String SERIALIZED_NAME_SURNAME = "surname";
    @SerializedName(SERIALIZED_NAME_SURNAME)
    private String surname;

    public static final String SERIALIZED_NAME_UNIVERSAL_ID = "universalId";
    @SerializedName(SERIALIZED_NAME_UNIVERSAL_ID)
    private UUID universalId;

    public static final String SERIALIZED_NAME_PHONE = "phone";
    @SerializedName(SERIALIZED_NAME_PHONE)
    private String phone;

    public static final String SERIALIZED_NAME_DATE_OF_BIRTH = "dateOfBirth";
    @SerializedName(SERIALIZED_NAME_DATE_OF_BIRTH)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING) // Store the enum as a string in DB (e.g., "VALIDATED")
    @Column(nullable = false)
    private AccountStatus status;

    public static final String SERIALIZED_NAME_AUTHENTICATION_CODE = "authenticationCode";
    @SerializedName(SERIALIZED_NAME_AUTHENTICATION_CODE)
    private String authenticationCode;

    public static final String SERIALIZED_NAME_DBFAMIN = "dbfamin";
    @SerializedName(SERIALIZED_NAME_DBFAMIN)
    private UUID dbfamin;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FocusConfigEntity> focusConfigs = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<RoleEntity> roles;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.universalId = UUID.randomUUID();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> (GrantedAuthority) role::getRoleName)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}