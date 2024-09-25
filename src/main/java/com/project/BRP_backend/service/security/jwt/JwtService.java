package com.project.BRP_backend.service.security.jwt;

import com.project.BRP_backend.enums.security.TokenType;
import com.project.BRP_backend.domain.security.Token;
import com.project.BRP_backend.domain.security.TokenData;
import com.project.BRP_backend.model.user.User;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;
import java.util.function.Function;

public interface JwtService {
    String createToken(User user, Function<Token, String> token);
    Optional<String> extractToken(HttpServletRequest httpServletRequest, String cookieName);
    void addCookie(HttpServletResponse response, User user, TokenType tokenType);
    void addHeader(HttpServletResponse response, User user);

    <T> T getTokenData(String token, Function<TokenData, T> tokenDataTFunction);
    <T> T getClaimsValue(String token, Function<Claims, T> claimsTFunction);
    void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookie);
    boolean validateToken(HttpServletRequest request);
}
