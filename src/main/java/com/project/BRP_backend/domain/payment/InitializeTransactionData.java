package com.project.BRP_backend.domain.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class InitializeTransactionData {
        private String authorizationUrl;
        private String accessCode;
        private String reference;
}
