package com.project.BRP_backend.domain.product;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;

import java.time.LocalDateTime;

@Data
public class ProductPurchaseHistory {

    private String userId;
    private LocalDateTime localDateTime;
    private String purchaseSummary;

}
