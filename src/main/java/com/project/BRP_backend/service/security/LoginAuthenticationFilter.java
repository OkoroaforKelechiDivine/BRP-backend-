package com.project.BRP_backend.service.security;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.BRP_backend.constant.security.Constant;
import com.project.BRP_backend.domain.security.ApiAuthenticationToken;
import com.project.BRP_backend.enums.security.LoginType;
import com.project.BRP_backend.enums.security.TokenType;
import com.project.BRP_backend.model.user.LoginRequest;
import com.project.BRP_backend.model.user.User;
import com.project.BRP_backend.service.security.jwt.JwtService;
import com.project.BRP_backend.service.user.UserService;
import com.project.BRP_backend.shared.Response;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

import static com.project.BRP_backend.constant.security.Constant.LOGIN_PATH;
import static com.project.BRP_backend.shared.ResponseUtil.getResponse;

@Component
public class LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final UserService userService;
    private final JwtService jwtService;
    public LoginAuthenticationFilter(AuthenticationManager authenticationManager, JwtService jwtService, UserService userService) {
        super(new AntPathRequestMatcher(LOGIN_PATH, HttpMethod.POST.name()), authenticationManager);
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        var loginRequest = new ObjectMapper()
                .configure(JsonParser.Feature.AUTO_CLOSE_SOURCE,true)
                .readValue(request.getInputStream(), LoginRequest.class);
        userService.updateLoginAttempt(loginRequest.email(), LoginType.LOGIN_ATTEMPT);
        var authentication = ApiAuthenticationToken.unAuthenticated(loginRequest.email(), loginRequest.password());
        return getAuthenticationManager().authenticate(authentication);
    }
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        var user =(User) authentication.getPrincipal();
        userService.updateLoginAttempt(user.getEmail(), LoginType.LOGIN_SUCCESS);
        var httpResponse = generatedResponse(request,response,user);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);
        try (var out = response.getOutputStream()){
            var mapper = new ObjectMapper();
            mapper.writeValue(out,httpResponse);
        }
    }

    private Response generatedResponse(HttpServletRequest request, HttpServletResponse response, User user) {
        jwtService.addCookie(response,user, TokenType.ACCESS);
        jwtService.addHeader(response,user);

        return getResponse(request, Map.of("message", "You were logged in successfully"), "login success", HttpStatus.OK);
    }
}
