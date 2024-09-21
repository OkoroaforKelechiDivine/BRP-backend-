package com.project.BRP_backend.model.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigInteger;

@Document
@Getter
public final class ConfirmationToken {
    @Transient
    public static final String SEQUENCE_NAME = "confirmation_token_sequence";
    @Id
    @Setter
    private BigInteger id;
    @Field(name = "user_id")
    private final String userId;
    @Field(name = "key")
    private final String key;

    public ConfirmationToken(
            String userId,
            String key
    ) {
        this.id = BigInteger.ZERO;
        this.userId = userId;
        this.key = key;
    }

}
