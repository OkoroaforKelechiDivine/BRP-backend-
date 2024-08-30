package com.project.BRP_backend.model.user;

import com.project.BRP_backend.domain.user.Audit;
import com.project.BRP_backend.enums.security.Role;
import com.project.BRP_backend.enums.user.UserType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Document("user")
@Getter
public class User implements UserDetails {
    @Id
    private Long id;
    @Indexed
    private final String userId;
    @Indexed
    private final String email;
    private final String firstName;
    private final String lastName;
    private final UserType userType;
    private final UserCredentials userCredentials;
    private final Role role;
    private final Audit audit;
    @Setter
    private boolean accountExpired;
    @Setter
    private boolean accountLocked;
    @Setter
    private boolean credentialsExpired;
    @Setter
    private boolean isEnabled;
    @Setter
    private int loginAttempts;

    public User(Long id, String userId, String email, String firstName, String lastName, String password, Role role, Audit audit) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = UserType.valueOf(role.toString());
        this.userCredentials = new UserCredentials(password);
        this.audit = audit;
        accountExpired = false;
        accountLocked = false;
        credentialsExpired = false;
        isEnabled = false;
        this.role = role;
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

    @Override
    public boolean isAccountNonExpired() {
        return !accountExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !credentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
