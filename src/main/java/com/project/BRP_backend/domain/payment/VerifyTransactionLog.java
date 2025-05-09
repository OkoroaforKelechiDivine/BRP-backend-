package com.project.BRP_backend.domain.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.USE_DEFAULTS)
public class VerifyTransactionLog {
    private int startTime;
    private int timeSpent;
    private int attempts;
    private int errors;
    private boolean success;
    private boolean mobile;
    private Object[] input;
    private List<VerifyTransactionLogHistory> history;




}
