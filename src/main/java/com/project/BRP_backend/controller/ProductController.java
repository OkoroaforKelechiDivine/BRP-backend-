package com.project.BRP_backend.controller;

import com.project.BRP_backend.domain.ProductFilter;
import com.project.BRP_backend.dto.request.product.ProductDTO;
import com.project.BRP_backend.dto.request.product.ProductUpdateRequest;
import com.project.BRP_backend.dto.response.ResponseDetails;
import com.project.BRP_backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("create")
    public ResponseDetails createNewProduct(@RequestBody ProductDTO productDTO) {
        return productService.createProduct(productDTO);
    }
    @GetMapping("products")
    public ResponseDetails getProducts(@RequestBody ProductFilter filter) {
        return productService.retrieveProducts(String.join(";", filter.getListOfFilters()));
    }
    @PutMapping("update")
    public ResponseDetails updateProduct(@RequestBody ProductUpdateRequest productUpdateRequest){
        return productService.updateProduct(productUpdateRequest.getProductId(), productUpdateRequest.getNewProduct());
    }
    @DeleteMapping("delete/{id}")
    public ResponseDetails deleteProduct(@PathVariable("id") String productId) {
        return productService.deleteProduct(productId);
    }
}
