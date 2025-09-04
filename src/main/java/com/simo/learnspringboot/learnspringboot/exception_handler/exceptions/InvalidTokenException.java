package com.simo.learnspringboot.learnspringboot.exception_handler.exceptions;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
