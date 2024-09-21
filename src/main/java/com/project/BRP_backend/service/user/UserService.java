package com.project.BRP_backend.service.user;

import com.project.BRP_backend.constant.security.Constant;
import com.project.BRP_backend.domain.cache.CacheStore;
import com.project.BRP_backend.dto.user.UserDTO;
import com.project.BRP_backend.enums.security.LoginType;
import com.project.BRP_backend.enums.security.Role;
import com.project.BRP_backend.enums.user.UserEventType;
import com.project.BRP_backend.event.user.UserEvent;
import com.project.BRP_backend.exception.UserNotFoundException;
import com.project.BRP_backend.model.security.ConfirmationToken;
import com.project.BRP_backend.model.user.User;
import com.project.BRP_backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final CacheStore<String, Integer> loginCache;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher applicationEventPublisher;

    public Optional<UserDTO> getUserDTOByEmail(String email) {
        return Optional.of(userRepository
                .findByEmail(email)
                .map(user -> new UserDTO(
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        "{secret}",
                        user.getAddress(),
                        user.getPhoneNumber(),
                        user.getUserId()
                ))).get();
    }

    private User getUserByEmail(String email) {
        return userRepository
                .findByEmail(email).orElseThrow(UserNotFoundException::new);

    }

    public UserDTO registerUser(UserDTO userDTO){
        if (isUserExistsByEmail(userDTO.email()))
            return null;
        User user = new User(
                UUID.randomUUID().toString(),
                userDTO.email(),
                userDTO.firstName(),
                userDTO.lastName(),
                passwordEncoder.encode(userDTO.password()),
                Role.CLIENT,
                userDTO.address(),
                userDTO.phoneNumber()
        );

        user = userRepository.save(user);

        //Code that uses the UserEvent to send Email
        ConfirmationToken confirmationToken = new ConfirmationToken(user.getUserId(), UUID.randomUUID().toString());
        UserEvent userEvent = new UserEvent(user, UserEventType.REGISTRATION, Map.of("key", confirmationToken.getKey()));
        applicationEventPublisher.publishEvent(userEvent);


        return new UserDTO(user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                "Protected",
                user.getAddress(),
                user.getPhoneNumber(),
                user.getUserId());

    }


    public Optional<UserDTO> getUserDTOByUserId(String userId) {
        return Optional.of(userRepository
                .findByUserId(userId)
                .map(user -> new UserDTO(
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        Constant.PASSWORD_RESPONSE_VALUE,
                        user.getAddress(),
                        user.getPhoneNumber(),
                        user.getUserId()
                ))).get();
    }
    public User getUserByUserId(String userId) {
        return userRepository
                .findByUserId(userId)
                .orElseThrow(UserNotFoundException::new);
    }


    public void updateLoginAttempt(String email, LoginType loginType) {
        var user = getUserByEmail(email);
        switch (loginType) {
            case LOGIN_ATTEMPT -> {
                if (loginCache.get(user.getEmail()) == null) {
                    user.setLoginAttempts(0);
                    user.setAccountLocked(false);
                }
                user.setLoginAttempts(user.getLoginAttempts() + 1);
                loginCache.put(user.getEmail(), user.getLoginAttempts());
                if (loginCache.get(user.getEmail()) > 5) {
                    user.setAccountLocked(true);
                }
            }
            case LOGIN_SUCCESS -> {
                user.setAccountLocked(false);
                user.setLoginAttempts(0);
                user.getAudit().setLastLogin(LocalDateTime.now());
                loginCache.evict(user.getEmail());
            }
        }
        userRepository.save(user);
    }
    public boolean isUserExistsByUserId(String id) {
        return userRepository.existsByUserId(id);
    }
    public boolean isUserExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

}
