package com.project.BRP_backend.domain.security;

import com.project.BRP_backend.constant.security.Constant;
import com.project.BRP_backend.model.user.User;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;

public class ApiAuthenticationToken extends AbstractAuthenticationToken {

    private User user;

    @Getter
    private final String email;
    @Getter
    private final String password;

    private ApiAuthenticationToken(User user, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.user = user;
        this.password = Constant.PASSWORD_PROTECTED;
        this.email = Constant.EMAIL_PROTECTED;
        super.setAuthenticated(true);
    }

    private ApiAuthenticationToken(String email, String password) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.email = email;
        this.password = password;
    }

    public static ApiAuthenticationToken unAuthenticated(String email, String password) {
        return new ApiAuthenticationToken(email,password);
    }
    public static ApiAuthenticationToken authenticated(User user, Collection<? extends GrantedAuthority> authorities) {
        return new ApiAuthenticationToken(user,authorities);
    }
    @Override
    public Object getCredentials() {
        return Constant.PASSWORD_PROTECTED;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        throw new RuntimeException("You cannot explicitly set authentication from here");
    }

    @Override
    public Object getPrincipal() {
        return null;
    }


}
