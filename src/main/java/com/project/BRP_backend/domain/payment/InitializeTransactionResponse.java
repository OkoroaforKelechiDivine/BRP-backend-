package com.project.BRP_backend.domain.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class InitializeTransactionResponse {
    private String status;
    private String message;
    private InitializeTransactionData data;

}


