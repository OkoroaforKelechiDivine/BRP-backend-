package com.project.BRP_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class UserDetails {
    private String id;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String address;
    private String gender;
    private boolean isVerified;
}
