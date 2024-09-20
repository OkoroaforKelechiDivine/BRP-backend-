package com.project.BRP_backend.model.security;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public record ConfirmationToken(
        @Id long id,
        @Field(name = "user_id") String userId,
        @Field(name = "token") String token
) {
}
