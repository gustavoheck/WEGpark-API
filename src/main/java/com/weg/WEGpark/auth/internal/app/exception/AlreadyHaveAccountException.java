package com.weg.WEGpark.auth.internal.app.exception;

public class AlreadyHaveAccountException extends RuntimeException {
    public AlreadyHaveAccountException() {
        super("An account with this badge number is already registered");
    }
}
