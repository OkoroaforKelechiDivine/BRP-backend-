package com.project.BRP_backend.service.user;

import com.project.BRP_backend.constant.security.Constant;
import com.project.BRP_backend.domain.cache.CacheStore;
import com.project.BRP_backend.dto.user.UserDTO;
import com.project.BRP_backend.enums.security.LoginType;
import com.project.BRP_backend.exception.UserNotFoundException;
import com.project.BRP_backend.model.user.User;
import com.project.BRP_backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final CacheStore<String, Integer> loginCache;

    public Optional<UserDTO> getUserDTOByEmail(String email) {
        return Optional.of(userRepository
                .findByEmail(email)
                .map(user -> new UserDTO(
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        "{secret}",
                        user.getUserId()
                ))).get();
    }

    private User getUserByEmail(String email) {
        return userRepository
                .findByEmail(email).orElseThrow(UserNotFoundException::new);

    }


    public Optional<UserDTO> getUserDTOByUserId(String userId) {
        return Optional.of(userRepository
                .findByUserId(userId)
                .map(user -> new UserDTO(
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        Constant.PASSWORD_RESPONSE_VALUE,
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
    public boolean isUserNotExistsByUserId(String id) {
        return !userRepository.existsByUserId(id);
    }
    public boolean isUserNotExistsByEmail(String email) {
        return !userRepository.existsByEmail(email);
    }

}
