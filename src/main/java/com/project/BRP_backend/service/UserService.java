package com.project.BRP_backend.service;

import com.project.BRP_backend.dto.request.UserRegistrationRequest;
import com.project.BRP_backend.dto.response.ResponseDetails;
import com.project.BRP_backend.exception.AppException;
import com.project.BRP_backend.model.constants.Gender;
import com.project.BRP_backend.model.constants.Role;
import com.project.BRP_backend.model.user.User;
import com.project.BRP_backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;

import static com.project.BRP_backend.service.Constants.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public ResponseDetails createUser (UserRegistrationRequest registrationRequest) {
        if (isUserExistsByPhoneNumber(registrationRequest.getPhoneNumber())) {
            return new ResponseDetails(LocalDateTime.now(), "User with phone number " + registrationRequest.getPhoneNumber() + " already exists", HttpStatus.OK.toString());
        }
        User user = User.builder()
                .address(registrationRequest.getAddress())
                .email(registrationRequest.getEmail())
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .gender(Gender.valueOf(registrationRequest.getGender().trim().toUpperCase()))
                .phoneNumber(registrationRequest.getPhoneNumber())
                .role(Role.valueOf(registrationRequest.getUserType().trim().toUpperCase()))
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .isVerified(true) // Should this be true by default?...
                .build();
        userRepository.save(user);
        return new ResponseDetails(LocalDateTime.now(), "Registration Successful", HttpStatus.CREATED.toString());
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    @ExceptionHandler(AppException.class)
    public User getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new RuntimeException(USER_NOT_FOUND_MESSAGE));
    }
    @ExceptionHandler(AppException.class)
    public User getUserById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException(USER_NOT_FOUND_MESSAGE));
    }

    public boolean isUserExistsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }
    public boolean isUserExistsById(String id) {
        return userRepository.existsById(id);
    }

}
