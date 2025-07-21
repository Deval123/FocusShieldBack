package com.dev.focusshield.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<FocusShieldError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        LOGGER.error("Handling MethodArgumentNotValidException: {}", ex.getMessage());

        List<String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::buildFieldErrorMessage)
                .toList();

        String errorMessage = "Validation failed: " + String.join("; ", fieldErrors);

        final var riteError = FocusShieldError.builder()
                .code(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .message(errorMessage)
                .timestamp(LocalDateTime.now().toString())
                .build();

        return new ResponseEntity<>(riteError, HttpStatus.BAD_REQUEST);
    }

    private String buildFieldErrorMessage(FieldError error) {
        return String.format("Field '%s' %s (rejected value: %s)",
                error.getField(),
                error.getDefaultMessage(),
                Objects.isNull(error.getRejectedValue()) ? "null" : error.getRejectedValue().toString());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<FocusShieldError> handleConstraintViolationException(ConstraintViolationException ex) {
        FocusShieldError focusShieldError = FocusShieldError.builder()
                .message("Validation error: " + ex.getMessage()) // More user-friendly message
                .code(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .timestamp(LocalDateTime.now().toString())
                .build();
        return new ResponseEntity<>(focusShieldError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<FocusShieldError> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        LOGGER.error("Handling DataIntegrityViolationException: {}", ex.getMessage());

        String message = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
        FocusShieldErrorCode errorCode = FocusShieldErrorCode.DATA_ERROR_UNKNOWN_CONSTRAINT; // Default

        // Use if-else if structure to ensure only one is chosen
        if (message != null) {
            if (message.contains("UK6dotkott2kjsp8vw4d0m25fb7")) { // Assuming this is email unique key
                errorCode = FocusShieldErrorCode.DATA_ERROR_EMAIL_ALREADY_TAKEN;
            } else if (message.contains("UKr43af9ap4edm43mmtq01oddj6")) { // Assuming this is username unique key
                errorCode = FocusShieldErrorCode.DATA_ERROR_USERNAME_ALREADY_TAKEN;
            }
        }

        FocusShieldError focusShieldError = FocusShieldError.builder()
                .message(errorCode.getLabel())
                .code(errorCode.getCode())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return new ResponseEntity<>(focusShieldError, HttpStatus.CONFLICT);
    }

    // ⭐ CONSOLIDATED EXCEPTION HANDLER FOR FocusShieldException ⭐
    // This method now handles all general FocusShieldException cases,
    // including those for JSON and time parsing issues by checking the cause.
    @ExceptionHandler(FocusShieldException.class)
    public ResponseEntity<FocusShieldError> handleFocusShieldException(FocusShieldException e) {
        LOGGER.error("Handling FocusShieldException: {}", e.getMessage(), e); // Log the exception with cause

        String code = null; // Initialize to null or a default
        String message = e.getMessage(); // Start with the exception's message
        HttpStatus status = HttpStatus.BAD_REQUEST; // Default status for FocusShieldException

        // If the FocusShieldException wraps an error code, use it.
        if (e.getError() != null) {
            code = e.getError().getCode();
            // If the message is generic from error code, prefer the exception's custom message if available
            if (e.getMessage() != null && !e.getMessage().equals(e.getError().getLabel())) {
                message = e.getMessage();
            } else {
                message = e.getError().getLabel();
            }
            // You might also get the status from the error code if your enum supports it
            // status = e.getError().getHttpStatus();
        } else {
            // If no specific error code is set, try to infer from the cause
            if (e.getCause() != null) {
                if (e.getCause() instanceof JsonProcessingException) {
                    code = FocusShieldErrorCode.DATA_ERROR_JSON_CONVERSION.getCode();
                    message = FocusShieldErrorCode.DATA_ERROR_JSON_CONVERSION.getLabel() + ": " + e.getMessage();
                } else if (e.getCause() instanceof DateTimeParseException) {
                    code = FocusShieldErrorCode.DATA_ERROR_INVALID_TIME_FORMAT.getCode();
                    message = FocusShieldErrorCode.DATA_ERROR_INVALID_TIME_FORMAT.getLabel() + ": " + e.getMessage();
                }
                // Add more specific cause checks as needed
            }
            // Fallback for code if no specific code determined
            if (code == null) {
                code = String.valueOf(HttpStatus.BAD_REQUEST.value());
            }
        }

        // Handle cases where the exception itself is directly a bad request due to its nature
        // (e.g., if FocusShieldException is the base for all bad requests)
        if (code == null) { // final fallback for code
            code = String.valueOf(HttpStatus.BAD_REQUEST.value());
        }

        final var riteError = FocusShieldError.builder()
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now().toString())
                .build();
        return new ResponseEntity<>(riteError, status);
    }

    // Keep these specific handlers as they catch more specific exception types
    @ExceptionHandler(FocusShieldAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<FocusShieldError> handleAlreadyExitArgument(FocusShieldAlreadyExistsException ex) {
        LOGGER.error("Handling FocusShieldAlreadyExistsException: {}", ex.getMessage());

        var error = FocusShieldError.builder()
                .code(ex.getError().getCode())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now().toString())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(FocusShieldInvalidCredentials.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<FocusShieldError> handleInvalidCredentials(FocusShieldInvalidCredentials ex) {
        LOGGER.error("Handling FocusShieldInvalidCredentials: {}", ex.getMessage());

        var error = FocusShieldError.builder()
                .code(ex.getError().getCode())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now().toString())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<FocusShieldError> handleIllegalArgument(IllegalArgumentException ex) {
        LOGGER.error("Handling handleIllegalArgument: {}", ex.getMessage());

        final var riteError = FocusShieldError
                .builder()
                .code(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now().toString())
                .build();
        return new ResponseEntity<>(riteError, HttpStatus.BAD_REQUEST);
    }

    // General fallback handler for any unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<FocusShieldError> handleGeneralException(Exception ex) {
        LOGGER.error("An unexpected error occurred: {}", ex.getMessage(), ex); // Log the full exception

        FocusShieldError focusShieldError = FocusShieldError.builder()
                .message("An unexpected error occurred: " + ex.getMessage()) // Avoid exposing too much internal info in production
                .code(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .timestamp(LocalDateTime.now().toString())
                .build();
        return new ResponseEntity<>(focusShieldError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}