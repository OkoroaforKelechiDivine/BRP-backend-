package com.project.BRP_backend.service;

import com.project.BRP_backend.dto.request.AdminRegistrationRequest;
import com.project.BRP_backend.dto.request.UserRegistrationRequest;
import com.project.BRP_backend.dto.response.ResponseDetails;
import com.project.BRP_backend.dto.response.UserDetails;
import com.project.BRP_backend.event.EventType;
import com.project.BRP_backend.event.UserEvent;
import com.project.BRP_backend.exception.AppException;
import com.project.BRP_backend.model.constants.Gender;
import com.project.BRP_backend.model.constants.Role;
import com.project.BRP_backend.model.user.User;
import com.project.BRP_backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.project.BRP_backend.service.Constants.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public ResponseDetails createUser(UserRegistrationRequest registrationRequest) {
        if (isUserExistsByPhoneNumber(registrationRequest.getPhoneNumber())) {
            return new ResponseDetails(LocalDateTime.now(), "User with phone number " + registrationRequest.getPhoneNumber() + " already exists", HttpStatus.OK.toString());
        }
        var user = createUser(registrationRequest.getAddress(),
                registrationRequest.getEmail(),
                registrationRequest.getFirstName(),
                registrationRequest.getLastName(),
                registrationRequest.getGender(),
                registrationRequest.getPhoneNumber(),
                "CLIENT",
                registrationRequest.getPassword());
        UserEvent userEvent = new UserEvent(user, EventType.REGISTRATION, Map.of("OTP", ""));
        applicationEventPublisher.publishEvent(userEvent);
        return new ResponseDetails(LocalDateTime.now(), "Registration Successful", HttpStatus.CREATED.toString());
    }
    private User createUser(String address, String email, String firstName, String lastName, String gender, String phoneNumber, String userType, String password) {
        User user = User.builder()
                .address(address)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .gender(Gender.valueOf(gender.trim().toUpperCase()))
                .phoneNumber(phoneNumber)
                .role(Role.valueOf(userType.trim().toUpperCase()))
                .password(passwordEncoder.encode(password))
                .isVerified(false) // Should this be true by default?...
                .build();
        return userRepository.save(user);

    }

    public ResponseDetails createAdmin(AdminRegistrationRequest adminRegistrationRequest) {
        if (isUserExistsByPhoneNumber(adminRegistrationRequest.getPhoneNumber())) {
            return new ResponseDetails(LocalDateTime.now(), "Admin with phone number " + adminRegistrationRequest.getPhoneNumber() + " already exists", HttpStatus.OK.toString());
        }
        String adminPassword = adminRegistrationRequest.getPassword();
        createUser(adminRegistrationRequest.getAddress(), adminRegistrationRequest.getEmail(), adminRegistrationRequest.getFirstName(), adminRegistrationRequest.getLastName(), adminRegistrationRequest.getGender(), adminRegistrationRequest.getPhoneNumber(),"ADMIN", adminRegistrationRequest.getPassword());
        return new ResponseDetails(LocalDateTime.now(), "New Admin has been created", HttpStatus.OK.toString());
    }

    public ResponseDetails deleteClient(String id) {
        userRepository.deleteById(id);
        return new ResponseDetails(LocalDateTime.now(), "User with id" + id + " has been deleted", HttpStatus.NO_CONTENT.toString());
    }
    public ResponseDetails deleteAdmin(String id) {
        userRepository.deleteById(id);
        return new ResponseDetails(LocalDateTime.now(), "Admin with id" + id + " has been deleted", HttpStatus.NO_CONTENT.toString());
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

    private boolean isUserExistsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }
    private boolean isUserExistsById(String id) {
        return userRepository.existsById(id);
    }
    private boolean isUserExistsByPhoneNumberAndRole(String phoneNumber, Role role) {
        return userRepository.existsByPhoneNumberAndRole(phoneNumber, role);
    }
    private boolean isUserExistsByIdAndRole(String id, Role role) {
        return userRepository.existsByIdAndRole(id,role);
    }

    public ResponseDetails getAdminUsers() {
        Set<UserDetails> admins = userRepository.findAllByRole(Role.ADMIN).stream()
                .map(user -> new UserDetails(user.getId(),
                        user.getPhoneNumber(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getAddress(),
                        user.getGender().toString(),
                        user.getIsVerified()))
                .collect(Collectors.toSet());
        return new ResponseDetails(LocalDateTime.now(), "Successful", HttpStatus.OK.toString(), Map.of("admins",admins));

    }
    public ResponseDetails getClientUsers() {
        Set<UserDetails> clients = userRepository.findAllByRole(Role.CLIENT).stream()
                .map(user -> new UserDetails(user.getId(),
                        user.getPhoneNumber(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getAddress(),
                        user.getGender().toString(),
                        user.getIsVerified()))
                .collect(Collectors.toSet());
        return new ResponseDetails(LocalDateTime.now(), "Successful", HttpStatus.OK.toString(), Map.of("clients",clients));

    }

    public void updateUser(User user) {
        userRepository.save(user);
    }
}
