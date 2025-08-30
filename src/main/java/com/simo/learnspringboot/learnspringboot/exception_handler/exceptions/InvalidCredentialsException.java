package com.simo.learnspringboot.learnspringboot.exception_handler.exceptions;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
