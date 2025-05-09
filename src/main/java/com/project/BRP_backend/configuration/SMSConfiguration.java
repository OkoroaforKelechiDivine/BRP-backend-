package com.project.BRP_backend.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class SMSConfiguration {
    @Value("${sms.account_sid}")
    private String account_Sid;
    @Value("${sms.auth.token}")
    private String auth_token;
    @Value("${sms.phoneNumber}")
    private String phone_Number;
}
