package com.dev.focusshield.exceptions;

public class FocusShieldNotFoundException extends FocusShieldException {
    public FocusShieldNotFoundException(FocusShieldErrorCode errorCode ) {
        super(errorCode);
    }
}
