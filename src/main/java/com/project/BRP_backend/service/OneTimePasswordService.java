package com.project.BRP_backend.service;

import com.project.BRP_backend.dto.response.ResponseDetails;
import com.project.BRP_backend.model.user.OneTimePassword;
import com.project.BRP_backend.repository.user.OneTimePasswordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OneTimePasswordService {
    private final OneTimePasswordRepository oneTimePasswordRepository;
    private final UserService userService;

    public void saveOneTimePassword (OneTimePassword oneTimePassword){
        oneTimePasswordRepository.save(oneTimePassword);
    }

    public ResponseDetails verifyUser(String otp, String userId) {
        var otpOptional = oneTimePasswordRepository.findByOtp(otp);
        if (otpOptional.isPresent()) {
            var otpObject = otpOptional.get();
            if (otpObject.getUserId().equals(userId)) {
                var user = userService.getUserById(userId);
                user.setIsVerified(true);
                userService.updateUser(user);
                return new ResponseDetails(LocalDateTime.now(), "User was verified successfully", HttpStatus.OK.toString(), Map.of("verified", true));
            }
            return new ResponseDetails(LocalDateTime.now(), "OTP invalid for current user", HttpStatus.EXPECTATION_FAILED.toString(), Map.of("verified", false));
        }
        return new ResponseDetails(LocalDateTime.now(), "Otp doesn't exist", HttpStatus.NOT_FOUND.toString(), Map.of("verified", false));
    }
}
