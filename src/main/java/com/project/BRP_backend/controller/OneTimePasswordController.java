package com.project.BRP_backend.controller;

import com.project.BRP_backend.dto.response.ResponseDetails;
import com.project.BRP_backend.service.OneTimePasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/otp")
public class OneTimePasswordController {

    private final OneTimePasswordService oneTimePasswordService;

    @GetMapping(path = "verify",params = {"otp", "user_id"})
    public ResponseDetails verifyUser (@RequestParam("otp") String otp, @RequestParam("user_id") String userId) {
        return oneTimePasswordService.verifyUser(otp, userId);
    }
}
