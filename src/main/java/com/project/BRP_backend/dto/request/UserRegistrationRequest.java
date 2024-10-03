package com.project.BRP_backend.dto.request;

import lombok.Data;

@Data
public class UserRegistrationRequest {
    private final String firstName;
    private final String lastName;
    private final String userType;
    private final String phoneNumber;
    private final String email;
    private final String password;
    private final String gender;
    private final String address;

}
