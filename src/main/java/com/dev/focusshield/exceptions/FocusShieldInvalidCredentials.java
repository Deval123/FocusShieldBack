package com.dev.focusshield.exceptions;

public class FocusShieldInvalidCredentials extends FocusShieldException{
    public FocusShieldInvalidCredentials(FocusShieldErrorCode error) {
        super(error);
    }

    public FocusShieldInvalidCredentials(String message) {
        super(message);
    }
}
