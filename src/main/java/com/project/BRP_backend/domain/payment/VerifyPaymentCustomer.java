package com.project.BRP_backend.domain.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;

@Data
@JsonInclude(JsonInclude.Include.USE_DEFAULTS)
public class VerifyPaymentCustomer {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String customerCode;
    private String phone;
    private String metadata;
    private String riskAction;
    private String internationalFormatPhone;
}
