package com.weg.WEGpark.auth.internal.app.exception;

public class AccountEmailNotActiveException extends RuntimeException {
    public AccountEmailNotActiveException(String message) {
        super(message);
    }
}
