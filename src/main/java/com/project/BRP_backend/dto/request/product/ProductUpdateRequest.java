package com.project.BRP_backend.dto.request.product;

import lombok.Data;

@Data
public class ProductUpdateRequest {
    private String productId;
    private ProductDTO newProduct;

}
