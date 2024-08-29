package com.project.BRP_backend.model.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@RequiredArgsConstructor
@Getter
@Setter
@Document("user")
public class User implements UserDetails {
    @Id
    private Long id;
    @Indexed
    private final String userId;
    @Indexed
    private final String email;
    private final String firstName;
    private final String lastName;
    private final UserCredentials userCredentials;

    public User(Long id, String userId, String email, String firstName, String lastName, String password) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userCredentials = new UserCredentials(password);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return userCredentials.getPassword();
    }

    @Override
    public String getUsername() {
        return email;
    }
}
