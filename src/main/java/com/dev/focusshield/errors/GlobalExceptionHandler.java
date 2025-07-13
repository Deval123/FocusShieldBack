package com.dev.focusshield.errors;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public ResponseEntity<Error> handleValidationExceptions(MethodArgumentNotValidException ex) {
        LOGGER.error("Handling MethodArgumentNotValidException: {}", ex.getMessage());

        // Get all field errors and create a detailed error message for each field
        List<String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::buildFieldErrorMessage)
                .toList();

        String errorMessage = "Validation failed: " + String.join("; ", fieldErrors);

        // Create a custom Error response
        final var riteError = Error.builder()
                .code(HttpStatus.BAD_REQUEST.value())
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
    public ResponseEntity<Error> handleConstraintViolationException(ConstraintViolationException ex) {
        Error error = Error.builder()
                .message("Validation error: " + ex.getMessage())
                .code(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now().toString())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FocusShieldException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<Error> handleMeloAudeBadRequestException(FocusShieldException e) {
        LOGGER.error("Handling MeloAudeException: {}", e.getMessage());
        final var riteError = Error.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().toString())
                .build();
        return new ResponseEntity<>(riteError, HttpStatus.BAD_REQUEST);
    }

    // Gestion des exceptions générales
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleGeneralException(Exception ex) {
        Error error = Error.builder()
                .message("An unexpected error occurred: " + ex.getMessage())
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(LocalDateTime.now().toString())
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
