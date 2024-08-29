package com.project.BRP_backend.config.security;

import com.project.BRP_backend.repository.user.UserRepository;
import com.project.BRP_backend.service.security.CustomAuthenticationProvider;
import com.project.BRP_backend.service.security.CustomUserDetailsService;
import com.project.BRP_backend.service.security.LoginAuthenticationFilter;
import com.project.BRP_backend.service.security.jwt.JwtAuthenticationFilter;
import com.project.BRP_backend.service.security.jwt.JwtService;
import com.project.BRP_backend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    @Bean
    UserDetailsService userDetailsService(UserRepository userRepository){return new CustomUserDetailsService(userRepository);}

    @Bean
    AuthenticationManager authenticationManager(UserRepository userRepository, PasswordEncoder passwordEncoder){
        var provider = new CustomAuthenticationProvider(userRepository, passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain webSecurity(HttpSecurity httpSecurity,
                                           LoginAuthenticationFilter loginAuthenticationFilter,
                                           JwtAuthenticationFilter jwtFilter ) throws Exception {
        return httpSecurity
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .addFilterBefore(loginAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
    @Bean
    LoginAuthenticationFilter authenticationFilter (JwtService jwtService, UserService userService, AuthenticationManager manager) {
        return new LoginAuthenticationFilter(manager, jwtService, userService);
    }

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter (JwtService jwtService, UserService userService) {
        return new JwtAuthenticationFilter(jwtService, userService);
    }



}
