package com.weg.WEGpark.auth.internal.infra.security.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException() {
        super("You token is invalid!");
    }
}
