package com.weg.WEGpark.auth.shared.exception;

public class AlreadyHaveAccountException extends RuntimeException {
    public AlreadyHaveAccountException(String message) {
        super(message);
    }
}
