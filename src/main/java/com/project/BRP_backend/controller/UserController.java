package com.project.BRP_backend.controller;

import com.project.BRP_backend.dto.request.UserRegistrationRequest;
import com.project.BRP_backend.dto.response.ResponseDetails;
import com.project.BRP_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserController {
    private final UserService userService;

    @PostMapping("create")
    public ResponseDetails createClient(@RequestBody UserRegistrationRequest registrationRequest) {
        return userService.createUser(registrationRequest);
    }

    @GetMapping("clients")
    public ResponseDetails getClients() {
        return userService.getClientUsers();
    }

    @DeleteMapping("delete/{id}")
    public ResponseDetails deleteClient(@PathVariable("id") String id) {
        return userService.deleteClient(id);
    }


}
