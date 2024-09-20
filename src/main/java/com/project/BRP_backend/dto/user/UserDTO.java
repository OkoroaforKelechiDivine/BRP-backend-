package com.project.BRP_backend.dto.user;

import com.project.BRP_backend.domain.user.Address;

public record UserDTO(
        String firstName,
        String lastName,
        String email,
        String password,
        Address address,
        String phoneNumber,
        String userId
) {
}
