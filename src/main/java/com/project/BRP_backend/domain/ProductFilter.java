package com.project.BRP_backend.domain;

import lombok.Data;

import java.util.List;

@Data
public class ProductFilter {
    private List<String> listOfFilters;
}
