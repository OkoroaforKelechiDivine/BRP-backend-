package com.project.BRP_backend.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@JsonInclude(value = NON_DEFAULT)
public record UserRegistrationParams(
        String firstName,
        String lastName,
        String phoneNumber,
        String email,
        String password,
        String houseAddress,
        String city,
        String state,
        String country
) {
}
