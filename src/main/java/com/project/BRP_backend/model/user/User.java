package com.project.BRP_backend.model.user;

import com.project.BRP_backend.model.constants.Gender;
import com.project.BRP_backend.model.constants.Role;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class User{

    @Id
    private String id;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String email;

    private String password;

    private String address;

    private Gender gender;

    private Role role;

    private Boolean isVerified;

}