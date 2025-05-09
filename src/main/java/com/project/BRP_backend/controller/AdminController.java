package com.project.BRP_backend.controller;

import com.project.BRP_backend.dto.request.AdminRegistrationRequest;
import com.project.BRP_backend.dto.response.ResponseDetails;
import com.project.BRP_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin")
public class AdminController {

    private final UserService userService;
    @PostMapping("register")
    public ResponseDetails createAdmin(AdminRegistrationRequest adminRegistrationRequest) {
        return userService.createAdmin(adminRegistrationRequest);
    }
    @DeleteMapping("delete/{id}")
    public ResponseDetails deleteAdmin(@PathVariable("id") String adminId) {
        return userService.deleteAdmin(adminId);
    }
    @GetMapping("admins")
    public ResponseDetails getAdmins() {
        return userService.getAdminUsers();
    }
}
