package com.project.BRP_backend.exception;

public class TokenNotFoundException extends RuntimeException{
    public static final String NO_TOKEN_FOUND_MESSAGE = "No token was found in the header or cookie...";

    public TokenNotFoundException() {
        super(NO_TOKEN_FOUND_MESSAGE);
    }
}
