package com.project.BRP_backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.BRP_backend.dto.request.LoginDTO;
import com.project.BRP_backend.dto.request.ResponseDTO;
import com.project.BRP_backend.dto.request.UnsuccessfulLogin;
import com.project.BRP_backend.exception.AppException;
import com.project.BRP_backend.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

import static com.project.BRP_backend.security.SecurityConstants.*;

@Slf4j
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final UserRepository repository;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, ApplicationContext context) {
        this.authenticationManager = authenticationManager;
        repository = context.getBean(UserRepository.class);
        setFilterProcessesUrl("/api/user/login");
    }

    @Override
    @ExceptionHandler(AppException.class)
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginDTO credential = new ObjectMapper().readValue(request.getInputStream(), LoginDTO.class);
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credential.getEmail(),
                            credential.getPassword(),
                            new ArrayList<>())
            );
        }
        catch (IOException exception) {
            throw new RuntimeException("User does not exist");
        }
    }

    @Override
    @ExceptionHandler(AppException.class)
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        String token = JWT.create()
                .withSubject(((User) authResult.getPrincipal()).getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+ EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(SECRET.getBytes(StandardCharsets.UTF_8)));

        ObjectMapper oMapper = new ObjectMapper();
        String email = ((User) authResult.getPrincipal()).getUsername();
        com.project.BRP_backend.model.user.User appUser = repository.findByEmail(email);
        ResponseDTO responseDto = new ResponseDTO();
        responseDto.setUser(appUser);
        responseDto.setToken(token);
        logger.info(token);

        response.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        response.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
        response.getOutputStream().print("{ \"data\":"  + oMapper.writeValueAsString(responseDto) +  "}");
        response.flushBuffer();
    }
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        super.unsuccessfulAuthentication(request, response, failed);
        UnsuccessfulLogin responseDetails = new UnsuccessfulLogin(LocalDateTime.now(), "Incorrect email or password", "Bad request", "/api/auths/login");
        response.getOutputStream().print("{ \"message\":"  + responseDetails +  "}");
    }
}