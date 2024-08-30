package com.project.BRP_backend.service.security.jwt;

import com.project.BRP_backend.constant.security.Constant;
import com.project.BRP_backend.domain.security.ApiAuthenticationToken;
import com.project.BRP_backend.enums.security.TokenType;
import com.project.BRP_backend.exception.ApiAuthenticationException;
import com.project.BRP_backend.exception.InvalidTokenException;
import com.project.BRP_backend.exception.TokenNotFoundException;
import com.project.BRP_backend.model.security.TokenData;
import com.project.BRP_backend.service.user.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.util.Optional;

import static com.project.BRP_backend.constant.security.Constant.AUTHORIZATION;
import static com.project.BRP_backend.enums.security.TokenType.*;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (!request.getRequestURI().matches("/api/v1/user/(login|logout)") && !request.getRequestURI().matches("api/v1/user/(create|register)")) {
            final Optional<String> authorizationAccessToken = jwtService.extractToken(request, ACCESS.getValue());
            final String authorizationHeader = request.getHeader(AUTHORIZATION);
            String userId = null;
            String jwtToken = null;
            if (authorizationAccessToken.isPresent()) {
                jwtToken = authorizationAccessToken.get();
                userId = jwtService.getClaimsValue(jwtToken, Claims::getSubject);
            } else if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwtToken = authorizationHeader.substring(7);
                userId = jwtService.getClaimsValue(jwtToken, Claims::getSubject);

            } else throw new TokenNotFoundException();

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var userDetails = userService.getUserByUserId(userId);

                if (jwtService.validateToken(request)) {
                    var userRoles = jwtService.getTokenData(jwtToken, TokenData::getGrantedAuthorities);
                    var authentication = ApiAuthenticationToken.authenticated(userDetails, userRoles);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    filterChain.doFilter(request,response);
                } else {
                    throw new InvalidTokenException();
                }
            } else throw new ApiAuthenticationException();
        } else {
            filterChain.doFilter(request,response);
        }
    }
}
