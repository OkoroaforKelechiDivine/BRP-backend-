package com.project.BRP_backend.service.user;

import com.project.BRP_backend.constant.security.Constant;
import com.project.BRP_backend.dto.user.UserDTO;
import com.project.BRP_backend.model.user.User;
import com.project.BRP_backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public Optional<UserDTO> getUserByEmail(String email) {
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

    public Optional<UserDTO> getUserByUserId(String userId) {
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

}
