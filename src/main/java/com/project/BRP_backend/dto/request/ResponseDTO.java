package com.project.BRP_backend.dto.request;


import com.project.BRP_backend.model.user.User;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO {

    private User user;

    private String token;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}