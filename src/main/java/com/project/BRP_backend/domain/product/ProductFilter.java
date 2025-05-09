package com.project.BRP_backend.domain.product;

import lombok.Data;

import java.util.List;

@Data
public class ProductFilter {
    private List<String> listOfFilters;
}
