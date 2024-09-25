package com.project.BRP_backend.event.user;

import com.project.BRP_backend.enums.user.UserEventType;
import com.project.BRP_backend.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
@AllArgsConstructor
@Getter
@Setter
public class UserEvent {
    private User user;
    private UserEventType eventType;
    private Map<?,?> data;

}
