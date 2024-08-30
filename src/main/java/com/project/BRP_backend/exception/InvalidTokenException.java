package com.project.BRP_backend.exception;

public class InvalidTokenException extends RuntimeException{
    public static final String INVALID_TOKEN_MESSAGE = "The token provided is invalid";

    public InvalidTokenException() {
        super(INVALID_TOKEN_MESSAGE);
    }
}
