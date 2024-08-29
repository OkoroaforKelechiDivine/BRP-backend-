package com.project.BRP_backend.exception;

public class UserNotFoundException extends RuntimeException{
    private static final String USER_NOT_FOUND_MESSAGE = "The requested user was not found";

    public UserNotFoundException() {
        super(USER_NOT_FOUND_MESSAGE);
    }
}
