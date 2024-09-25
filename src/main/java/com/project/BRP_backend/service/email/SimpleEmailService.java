package com.project.BRP_backend.service.email;

import com.project.BRP_backend.utils.email.EmailUtils;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.project.BRP_backend.utils.email.EmailUtils.*;

@Service
@RequiredArgsConstructor
public class SimpleEmailService implements EmailService{

    public static final String PASSWORD_RESET_REQUEST = "Reset Password Request";
    public static final String NEW_USER_ACCOUNT_VERIFICATION= "New User Account Verification";
    public static final Logger logger = LoggerFactory.getLogger(SimpleEmailService.class);
    private final JavaMailSender sender;
    @Value("${spring.mail.verify.host}")
    private String verifyHost;
    @Value("${spring.mail.verify.port}")
    private String verifyPort;
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    @Override
    public void sendNewAccountEmail(String name, String to, String key) {
        try {
            var message = new SimpleMailMessage();
            message.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setText(getRegistrationEmailMessage(name, verifyHost+":"+verifyPort, key));
            sender.send(message);
        } catch (MailException e) {
            logger.error(e.getMessage());
        }
    }

    @Async
    @Override
    public void sendPasswordResetEmail(String name, String to, String key) {
        try {
            var message = new SimpleMailMessage();
            message.setSubject(PASSWORD_RESET_REQUEST);
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setText(getResetPasswordEmailMessage(name, verifyHost+":"+verifyPort, key));
            sender.send(message);
        } catch (MailException e) {
            logger.error(e.getMessage());
        }
    }
}
