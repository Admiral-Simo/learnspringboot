package com.simo.learnspringboot.learnspringboot.exception_handler.exceptions;

public class InvalidAuthCredentialsException extends RuntimeException {
    public InvalidAuthCredentialsException(String message) {
        super(message);
    }
}
