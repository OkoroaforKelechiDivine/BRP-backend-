package com.project.BRP_backend.service.email;

public interface EmailService {
    void sendNewAccountEmail(String name, String to, String key);
    void sendPasswordResetEmail(String name, String to, String key);
}
