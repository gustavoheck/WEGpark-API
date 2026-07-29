package com.weg.WEGpark.shared.exception;

public class MoreThenOneFilterException extends RuntimeException {
    public MoreThenOneFilterException(String message) {
        super(message);
    }
}
