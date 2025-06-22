package com.dev.focusshield.exceptions;

public class FocusShieldAlreadyExistsException extends FocusShieldException{

    public FocusShieldAlreadyExistsException(FocusShieldErrorCode error) {
        super(error);
    }

    public FocusShieldAlreadyExistsException(String message) {
        super(message);
    }
}
