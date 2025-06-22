package com.dev.focusshield.errors;

public class FocusShieldNotFoundException extends FocusShieldException {
    public FocusShieldNotFoundException(FocusShieldErrorCode errorCode ) {
        super(errorCode);
    }
}
