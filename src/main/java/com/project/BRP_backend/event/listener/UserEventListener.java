package com.project.BRP_backend.event.listener;

import com.project.BRP_backend.event.UserEvent;
import com.project.BRP_backend.exception.AppException;
import com.project.BRP_backend.model.user.OneTimePassword;
import com.project.BRP_backend.service.OneTimePasswordService;
import com.project.BRP_backend.service.SMSService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
                var otp = (String) userEvent.getData().get("OTP");
                var firstName = (String) userEvent.getUser().getFirstName();
                var phoneNumber = (String) userEvent.getUser().getPhoneNumber();
                smsService.sendMessage(generateResetPasswordMessage(firstName, otp), phoneNumber);
                var oneTimePassword = OneTimePassword.builder()
                        .userId(userEvent.getUser().getId())
                        .otp(otp)
                        .build();
                oneTimePasswordService.saveOneTimePassword(oneTimePassword);
            }
            break;
            case ADMIN_REGISTRATION:{

            }
            break;
            case REGISTRATION:{
                var otp = (String) userEvent.getData().get("OTP");
                var firstName = (String) userEvent.getUser().getFirstName();
                var phoneNumber = (String) userEvent.getUser().getPhoneNumber();
                smsService.sendMessage(generateOTPMessage(firstName, otp), phoneNumber);
                var oneTimePassword = OneTimePassword.builder()
                        .userId(userEvent.getUser().getId())
                        .otp(otp)
                        .build();
                oneTimePasswordService.saveOneTimePassword(oneTimePassword);
            }
            break;
            case DELETE_ACCOUNT:{

            }
            break;
            case PAYMENT_FAILED:{
                var paymentId = (String) userEvent.getData().get("payment_id");
                var firstName = (String) userEvent.getUser().getFirstName();
                var phoneNumber = (String) userEvent.getUser().getPhoneNumber();
                smsService.sendMessage(generatePaymentFailedMessage(firstName, paymentId), phoneNumber);
            }
            break;
            case PAYMENT_SUCCESS:{
                var paymentId = (String) userEvent.getData().get("payment_id");
                var firstName = (String) userEvent.getUser().getFirstName();
                var phoneNumber = (String) userEvent.getUser().getPhoneNumber();
                smsService.sendMessage(generatePaymentSuccessMessage(firstName, paymentId), phoneNumber);
            }
            break;
            default: throw new AppException("Illegal state");
        }
    }

    private String generateOTPMessage(String firstName, String OTP) {
        return String.format("Hello %s your One-Time-Password is %s.%nPlease don't share this with anyone", firstName,OTP);
    }
    private String generateResetPasswordMessage(String firstName, String OTP) {
        return String.format("Hello %s your reset password OTP is %s.%nPlease don't share this with anyone",firstName,OTP);

    }
    private String generatePaymentFailedMessage(String firstName, String paymentId) {
        return String.format("Hello %s unfortunately your payment with id %s has failed, please try again", firstName,paymentId);
    }
    private String generatePaymentSuccessMessage(String firstName, String paymentId) {
        return String.format("Hello %s, congratulations! your payment with id %s was successful", firstName,paymentId);
    }

}
