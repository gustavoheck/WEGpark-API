package com.weg.WEGpark.auth.internal.app.exception;

public class AlreadyHaveAccountException extends RuntimeException {
    public AlreadyHaveAccountException(String message) {
        super(message);
    }
}
