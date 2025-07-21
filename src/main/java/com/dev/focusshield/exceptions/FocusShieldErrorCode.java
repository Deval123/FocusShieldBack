package com.dev.focusshield.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FocusShieldErrorCode {
    DATA_ERROR_ROLE_NOT_FOUND("404-001", "Role not found."),
    DATA_ERROR_ROLE_ALREADY_EXIST("409-001", "Role name is in use"),
    DATA_ERROR_INVALID_PASSWORD_FORMAT("400-009", "Password must be at least 8 characters long, Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"),

    DATA_ERROR_USER_NOT_FOUND("404-002", "User not found"),
    DATA_ERROR_USERNAME_ALREADY_TAKEN("409-001", "Username is already taken"),
    DATA_ERROR_EMAIL_ALREADY_TAKEN("409-002", "Email is already taken"),
    DATA_ERROR_DEFAULT_ROLE_NOT_FOUND("404-003", "Default role not found"),
    DATA_ERROR_UNKNOWN_CONSTRAINT("409-999", "A database constraint violation occurred"),
    DATA_ERROR_INVALID_CREDENTIALS("401-001", "Invalid email or password"),

    // ⭐ NEW ERROR CODES ⭐
    DATA_ERROR_JSON_CONVERSION("400-010", "Error during JSON data conversion."),
    DATA_ERROR_INVALID_TIME_FORMAT("400-011", "Invalid time format provided.");

    private final String code;
    private final String label;
}
