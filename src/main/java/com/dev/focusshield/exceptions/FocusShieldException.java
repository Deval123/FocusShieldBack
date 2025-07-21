package com.dev.focusshield.exceptions;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class FocusShieldException extends RuntimeException {
    private FocusShieldErrorCode error;
    private final String message;
    private final Timestamp timestamp;

    public FocusShieldException(FocusShieldErrorCode error){
        super(error.getCode()+" : "+error.getLabel());
        this.message = error.getCode()+" : "+error.getLabel();
        this.error = error;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public FocusShieldException(String message){
        super(message);
        this.message = message;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }
    // ⭐ NEW CONSTRUCTOR TO ADD ⭐
    public FocusShieldException(String message, Throwable cause) {
        super(message, cause); // Call RuntimeException's constructor that takes a cause
        this.message = message;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        // For exceptions like this where no specific FocusShieldErrorCode is provided,
        // you might set a default error code or leave 'error' as null,
        // or refine your error code system to include "GENERAL_PROCESSING_ERROR".
        // For simplicity, we'll leave 'error' null if not set by a specific code.
        this.error = null; // Or a default "GENERAL_ERROR" if you define one
    }
}


