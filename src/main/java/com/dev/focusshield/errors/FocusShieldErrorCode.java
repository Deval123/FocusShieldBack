package com.dev.focusshield.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FocusShieldErrorCode {
    DATA_ERROR_ROLE_NOT_FOUND("404-001", "Role not found."),
    DATA_ERROR_ROLE_ALREADY_EXIST("409-001", "Role name is in use");


    private final String code;
    private final String label;
}
