package com.project.BRP_backend.domain.payment;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class VerifyTransactionData {
    private long id;
    private String domain;
    private String status;
    private String reference;
    @JsonAlias(value = "receipt_number")
    private String receiptNumber;
    private int amount;
    private String message;
    @JsonAlias(value = "gateway_response")
    private String gatewayResponse;
    @JsonAlias(value = "paid_at")
    private String paidAt;
    @JsonAlias(value = "created_at")
    private String createdAt;
    private String channel;
    private String currency;
    @JsonAlias(value = "ip_address")
    private String ipAddress;
    private String metadata;
    private int fees;
    private String feesSplit;
    private VerifyTransactionAuthorization authorization;
    private VerifyPaymentCustomer customer;
    private String plan;
    private Object split;
    private String orderId;
    private int requestedAmount;
    private String posTransactionData;
    private String source;
    private String feesBreakdown;
    private String connect;
    private String transactionDate;
    private Object planObject;
    private Object subAccount;


}
