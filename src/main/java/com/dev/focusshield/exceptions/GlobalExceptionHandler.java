package com.dev.focusshield.exceptions;

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
import java.util.List;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<FocusShieldError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        LOGGER.error("Handling MethodArgumentNotValidException: {}", ex.getMessage());

        // Get all field exceptions and create a detailed error message for each field
        List<String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::buildFieldErrorMessage)
                .toList();

        String errorMessage = "Validation failed: " + String.join("; ", fieldErrors);

        // Create a custom FocusShieldError response
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

    // Gestion des erreurs de contraintes (@NotNull, @Size, etc.)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<FocusShieldError> handleConstraintViolationException(ConstraintViolationException ex) {
        FocusShieldError focusShieldError = FocusShieldError.builder()
                .message("Validation focusShieldError: " + ex.getMessage())
                .code(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .timestamp(LocalDateTime.now().toString())
                .build();
        return new ResponseEntity<>(focusShieldError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<FocusShieldError> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        LOGGER.error("Handling DataIntegrityViolationException: {}", ex.getMessage());

        // Check if it's a duplicate email
        String message = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
        FocusShieldErrorCode errorCode;

        if (message != null && message.contains("UK6dotkott2kjsp8vw4d0m25fb7")) {
            // This is the email unique key constraint
            errorCode = FocusShieldErrorCode.DATA_ERROR_EMAIL_ALREADY_TAKEN;
        }
        if (message != null && message.contains("UKr43af9ap4edm43mmtq01oddj6")) {
            // This is the username unique key constraint
            errorCode = FocusShieldErrorCode.DATA_ERROR_USERNAME_ALREADY_TAKEN;
        } else {
            // Fallback for other constraints
            errorCode = FocusShieldErrorCode.DATA_ERROR_UNKNOWN_CONSTRAINT;
        }

        FocusShieldError focusShieldError = FocusShieldError.builder()
                .message(errorCode.getLabel())
                .code(errorCode.getCode())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return new ResponseEntity<>(focusShieldError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(FocusShieldException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<FocusShieldError> handleBadRequestException(FocusShieldException e) {
        LOGGER.error("Handling MeloAudeException: {}", e.getMessage());
        final var riteError = FocusShieldError.builder()
                .code(e.getError().getCode())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().toString())
                .build();
        return new ResponseEntity<>(riteError, HttpStatus.BAD_REQUEST);
    }


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


    // Gestion des exceptions générales
    @ExceptionHandler(Exception.class)
    public ResponseEntity<FocusShieldError> handleGeneralException(Exception ex) {
        FocusShieldError focusShieldError = FocusShieldError.builder()
                .message("An unexpected focusShieldError occurred: " + ex.getMessage())
                .code(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .timestamp(LocalDateTime.now().toString())
                .build();
        return new ResponseEntity<>(focusShieldError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
