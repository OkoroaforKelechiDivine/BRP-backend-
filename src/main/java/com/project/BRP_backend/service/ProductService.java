package com.project.BRP_backend.service;

import com.project.BRP_backend.dto.request.product.ProductDTO;
import com.project.BRP_backend.dto.response.ResponseDetails;
import com.project.BRP_backend.exception.AppException;
import com.project.BRP_backend.model.product.Product;
import com.project.BRP_backend.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ResponseDetails createProduct(ProductDTO productDTO) {
        Product product = Product.builder()
                .name(productDTO.getName())
                .price(Integer.parseInt(productDTO.getPrice()))
                .productImageUrls(productDTO.getProductImageUrls())
                .description(productDTO.getDescription())
                .build();
        if (checkIfExactProductExists(product)) {
            return new ResponseDetails(LocalDateTime.now(), "The product already exists consider increasing quantity or updating existing product", HttpStatus.EXPECTATION_FAILED.toString());
        }
        productRepository.save(product);
        return null;
    }
    public ResponseDetails retrieveProducts(String filter) {
        // A couple of Ifs, Else's or Switch statements. I don't know the filter details yet.
        return null;
    }

    public ResponseDetails updateProduct(String id, ProductDTO productDTO){
        Product product = Product.builder()
                .name(productDTO.getName())
                .price(Integer.parseInt(productDTO.getPrice()))
                .productImageUrls(productDTO.getProductImageUrls())
                .description(productDTO.getDescription())
                .build();

        if (productRepository.existsById(id)) {
            productRepository.save(product);
            return new ResponseDetails(LocalDateTime.now(), "Product update was Successful", HttpStatus.OK.toString());
        }

        return new ResponseDetails(LocalDateTime.now(), "The product does not exist, consider creating a new one", HttpStatus.EXPECTATION_FAILED.toString());
    }
    public ResponseDetails deleteProduct(String id){

        productRepository.delete(productRepository.findById(id).orElseThrow(() -> new AppException("Product does not exist")));
        return new ResponseDetails(LocalDateTime.now(), "Done", HttpStatus.NO_CONTENT.toString());
    }
    private boolean checkIfExactProductExists(Product product) {
        return productRepository.existsByPriceAndDescriptionAndName(product.getPrice(), product.getDescription(), product.getName());
    }
}
