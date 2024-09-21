package com.project.BRP_backend.event.user.listener;

import com.project.BRP_backend.enums.user.UserEventType;
import com.project.BRP_backend.event.user.UserEvent;
import com.project.BRP_backend.service.email.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserEventListener {
    private final EmailService emailService;

    @EventListener
    public void onUserEvent(UserEvent userEvent) {
        switch (userEvent.getEventType()) {
            case REGISTRATION -> emailService
                    .sendNewAccountEmail(
                            userEvent.getUser().getFirstName(),
                            userEvent.getUser().getEmail(),
                            (String) userEvent.getData().get("key")
                    );
            case RESET_PASSWORD -> emailService
                    .sendPasswordResetEmail(
                            userEvent.getUser().getFirstName(),
                            userEvent.getUser().getEmail(),
                            (String) userEvent.getData().get("key")
                    );
        }
    }
}
