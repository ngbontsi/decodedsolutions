package com.platform.marketplace.service;

import com.platform.marketplace.dto.ProductRequest;
import com.platform.marketplace.model.Product;
import com.platform.marketplace.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product create(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(BigDecimal.valueOf(request.getPrice()))
                .stockQuantity(request.getStockQuantity())
                .categoryId(request.getCategoryId())
                .imageUrl(request.getImageUrl())
                .brand(request.getBrand())
                .build();
        return productRepository.save(product);
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public List<Product> getActive() {
        return productRepository.findByActiveTrue();
    }

    public List<Product> getByCategory(String categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> search(String query) {
        return productRepository.findByNameContainingIgnoreCase(query);
    }

    public Product getById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product update(String id, ProductRequest request) {
        Product product = getById(id);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        if (request.getPrice() != null) {
            product.setPrice(BigDecimal.valueOf(request.getPrice()));
        }
        product.setStockQuantity(request.getStockQuantity());
        product.setCategoryId(request.getCategoryId());
        product.setImageUrl(request.getImageUrl());
        product.setBrand(request.getBrand());
        return productRepository.save(product);
    }

    public void delete(String id) {
        Product product = getById(id);
        product.setActive(false);
        productRepository.save(product);
    }

    public void updateStock(String id, int quantity) {
        Product product = getById(id);
        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);
    }
}
