package com.project.BRP_backend.event;

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
    private EventType eventType;
    private Map<?, ?> data;

}
