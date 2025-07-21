package com.dev.focusshield.exceptions;

public class JsonConversionException extends FocusShieldException {

    // Constructor to use with an error code
    public JsonConversionException(FocusShieldErrorCode error) {
        super(error);
    }

    // Constructor to use with a specific message (e.g., if no direct error code fits)
    public JsonConversionException(String message) {
        super(message);
    }

    // Constructor to use with a message and a cause (the original exception)
    public JsonConversionException(String message, Throwable cause) {
        super(message, cause); // Call supper with message and cause
    }
}