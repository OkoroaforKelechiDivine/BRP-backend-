package com.project.BRP_backend.domain.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.USE_DEFAULTS)
public class VerifyTransactionResponse {
    private String status;
    private String message;
    private VerifyTransactionData data;

}
