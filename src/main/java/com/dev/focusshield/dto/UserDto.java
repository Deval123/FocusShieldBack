package com.dev.focusshield.dto;

import com.dev.focusshield.entities.AccountStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "user")
public class UserDto {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    private String email;

    private String password;

    private String firstname;

    private String surname;

    private UUID universalId;

    private String phone;

    private LocalDate dateOfBirth;

    private AccountStatus status;
}
