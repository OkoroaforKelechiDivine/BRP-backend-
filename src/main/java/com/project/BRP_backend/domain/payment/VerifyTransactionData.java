package com.project.BRP_backend.domain.payment;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyTransactionData {
    private long id;
    private String domain;
    private String status;
    private String reference;
    @JsonProperty(value = "receipt_number")
    private String receiptNumber;
    private int amount;
    private String message;
    @JsonProperty(value = "gateway_response")
    private String gatewayResponse;
    @JsonProperty(value = "paid_at")
    private String paidAt;
    @JsonProperty(value = "created_at")
    private String createdAt;
    private String channel;
    private String currency;
    @JsonProperty(value = "ip_address")
    private String ipAddress;
    private VerifyTransactionAuthorization authorization;
    private VerifyPaymentCustomer customer;
    @JsonProperty("order_id")
    private String orderId;
    @JsonProperty("requested_amount")
    private int requestedAmount;
    @JsonProperty("transaction_date")
    private String transactionDate;
    /*
    private String metadata;
    private int fees;
    private String feesSplit;
   */
    // private String plan;
    // private Object split;
    // private String posTransactionData;
    // private String source;
    // private String feesBreakdown;
    // private String connect;
   // private Object planObject;
   // private Object subAccount;


}
