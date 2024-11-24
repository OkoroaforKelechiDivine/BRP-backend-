package com.project.BRP_backend.domain.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class VerifyTransactionAuthorization {
    private String authorizationCode;
    private String bin;
    private String last4;
    private String expMonth;
    private String expYear;
    private String channel;
    private String cardType;
    private String bank;
    private String countryCode;
    private String brand;
    private boolean reusable;
    private String signature;
    private String accountName;
}
