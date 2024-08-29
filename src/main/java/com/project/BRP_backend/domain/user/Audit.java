package com.project.BRP_backend.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Audit {
    private final String referenceId;
    private final String createdBy;
    @Setter
    private String updatedBy;
    private final LocalDateTime createdAt;
    @Setter
    private LocalDateTime updatedAt;
    @Setter
    private LocalDateTime lastLogin;

    public Audit(String createdBy) {
        this.referenceId = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = createdAt;
        this.createdBy = createdBy;
        this.updatedBy = createdBy;
        this.lastLogin = LocalDateTime.now();
    }
}
