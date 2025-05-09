package com.project.BRP_backend.repository.product;

import com.project.BRP_backend.model.product.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    public boolean existsByPriceAndDescriptionAndName(int price, String description, String name);

}
