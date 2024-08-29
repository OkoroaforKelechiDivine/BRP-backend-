package com.project.BRP_backend.model.security;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Token {
    private String access;
    private String refresh;

}
