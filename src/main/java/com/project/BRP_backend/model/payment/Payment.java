package com.project.BRP_backend.model.payment;

import com.project.BRP_backend.model.constants.PaymentStatus;
import com.project.BRP_backend.model.product.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Builder
@Setter
@Getter
public class Payment {
    @Id
    private final String id;
    private final String userId;
    private PaymentStatus paymentStatus;
    private final List<Product> products;
    private final int totalPaymentAmount;


}
