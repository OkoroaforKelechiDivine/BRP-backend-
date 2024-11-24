package com.project.BRP_backend.domain.payment;

import com.project.BRP_backend.domain.product.ProductAndQuantity;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class Cart {
    private final List<ProductAndQuantity> productAndQuantityList;
    private final String id;

    public Cart (List<ProductAndQuantity> productAndQuantityList){
        this.productAndQuantityList = productAndQuantityList;
        this.id = UUID.randomUUID().toString();
    }
}
