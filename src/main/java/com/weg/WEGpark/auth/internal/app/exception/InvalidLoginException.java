package com.weg.WEGpark.auth.internal.app.exception;

public class InvalidLoginException extends RuntimeException {
    public InvalidLoginException() {
        super("Password or Email is invalid");
    }
}
