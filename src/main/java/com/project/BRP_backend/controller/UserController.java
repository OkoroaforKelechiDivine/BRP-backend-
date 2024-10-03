package com.project.BRP_backend.controller;

import com.project.BRP_backend.dto.request.UserRegistrationRequest;
import com.project.BRP_backend.dto.response.ResponseDetails;
import com.project.BRP_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserController {
    private final UserService userService;

    public ResponseDetails createUser(UserRegistrationRequest registrationRequest) {
        return userService.createUser(registrationRequest);
    }


}
