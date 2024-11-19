package com.project.BRP_backend.dto.request.product;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class ProductDTO {
    private String name;
    private String description;
    private String price;
    private List<String> productImageUrls;
}
