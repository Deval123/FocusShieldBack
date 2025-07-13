package com.dev.focusshield.errors;

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

}
