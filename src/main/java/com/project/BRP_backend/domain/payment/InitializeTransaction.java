package com.project.BRP_backend.domain.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/*
*
*   Full list of fields
    private String email;
*   private String amount;
    private String currency;
    private String reference;
    private String callback_url;
    private String plan;
    private String invoice_limit;
    private Map<String, Object> metadata;
    private String[] channels;
    private String split_code;
    private String subaccount;
    private String transaction_charge;
    private String bearer;
*
* */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InitializeTransaction {
    private String amount;
    private String email;
    private String currency;
    private String reference;
   private String[] channels;
}
