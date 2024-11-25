package com.project.BRP_backend.event.listener;

import com.project.BRP_backend.event.UserEvent;
import com.project.BRP_backend.exception.AppException;
import com.project.BRP_backend.model.user.OneTimePassword;
import com.project.BRP_backend.service.OneTimePasswordService;
import com.project.BRP_backend.service.SMSService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class UserEventListener {
    private final SMSService smsService;
    private final OneTimePasswordService oneTimePasswordService;

    @EventListener
    public void onUserEvent(UserEvent userEvent) {
        switch (userEvent.getEventType()) {
            case RESET_PASSWORD:{

            }
            break;
            case ADMIN_REGISTRATION:{

            }
            break;
            case REGISTRATION:{
                var otp = generateOTP();
                smsService.sendMessage(generateOTPMessage(userEvent.getUser().getFirstName(),otp),userEvent.getUser().getPhoneNumber());
                var oneTimePassword = new OneTimePassword();
                oneTimePasswordService.saveOneTimePassword(oneTimePassword);
            }
            break;
            case DELETE_ACCOUNT:{

            }
            break;
            case PAYMENT_TRANSACTION:{

            }
            break;
            default: throw new AppException("Illegal state");
        }
    }

    private String generateOTPMessage(String firstName, String OTP) {
        return String.format("Hello, %s your One-Time-Password is %s.%nPlease don't share this with anyone", firstName,OTP);
    }
    private String generateOTP() {
        Random random = new Random();
        return String.valueOf(random.nextInt(1000000));
    }

}
