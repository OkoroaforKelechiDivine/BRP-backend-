package com.project.BRP_backend.model.user;

import com.project.BRP_backend.model.constants.Gender;
import com.project.BRP_backend.model.constants.Role;
import lombok.*;
import org.springframework.data.annotation.Id;

//@Getter
//@Data
@NoArgsConstructor
@AllArgsConstructor
public class User{

    @Id
    private String id;

    private String firstName;

    private String lastName;

    private Role userType;

    private String phoneNumber;

    private String email;

    private String password;

    private String Address;

    private Gender gender;

    private Role role;

    private Boolean isVerified;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }
}