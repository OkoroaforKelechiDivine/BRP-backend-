package com.project.BRP_backend.model.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
public class OneTimePassword {
    @Id
    private String id;
    private String userId;
    private String otp;
}
