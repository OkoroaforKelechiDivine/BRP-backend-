package com.project.BRP_backend.exception;

public class ApiAuthenticationException extends RuntimeException{

    public static final String AUTHENTICATION_MESSAGE = "Authentication was not successful please try again";

    public ApiAuthenticationException() {
        super(AUTHENTICATION_MESSAGE);
    }
}
