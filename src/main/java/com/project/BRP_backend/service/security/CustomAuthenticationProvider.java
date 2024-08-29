package com.project.BRP_backend.service.security;

import com.project.BRP_backend.domain.security.ApiAuthenticationToken;
import com.project.BRP_backend.model.user.User;
import com.project.BRP_backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var userAuthentication = (ApiAuthenticationToken) authentication;
        var userDetailsOptional = userRepository.findByEmail(userAuthentication.getEmail());
        if (userDetailsOptional.isPresent()) {
            var userDetails =(UserDetails) userDetailsOptional.get();
            validAccount.accept(userDetails);
            if (passwordEncoder.matches(userAuthentication.getPassword(),userDetails.getPassword())) {
                return ApiAuthenticationToken.authenticated(userDetails, userDetails.getAuthorities());
            } else throw new BadCredentialsException("Unable to login due to invalid Credentials");

        }
        throw new IllegalStateException("Unable to Perform Authentication");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiAuthenticationToken.class.isAssignableFrom(authentication);
    }
    private final Consumer<UserDetails> validAccount = user -> {
        if (!user.isAccountNonLocked()) {
            throw new LockedException("Your account is currently locked");
        }
        if (!user.isEnabled()) {
            throw new LockedException("Your account is currently disabled");
        }
        if (!user.isCredentialsNonExpired()) {
            throw new LockedException("Your credentials are currently expired, Please update them");
        }
        if (!user.isAccountNonExpired()) {
            throw new LockedException("Your Account has expired, Please contact the Administration");
        }
    };
}
