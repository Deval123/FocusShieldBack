package com.dev.focusshield.exceptions;

public class InvalidTimeFormatException extends FocusShieldException {

    // Constructor to use with an error code
    public InvalidTimeFormatException(FocusShieldErrorCode error) {
        super(error);
    }

    // Constructor to use with a specific message
    public InvalidTimeFormatException(String message) {
        super(message);
    }

    // Constructor to use with a message and a cause (the original DateTimeParseException)
    public InvalidTimeFormatException(String message, Throwable cause) {
        super(message, cause); // Call supper with a message and cause
    }
}