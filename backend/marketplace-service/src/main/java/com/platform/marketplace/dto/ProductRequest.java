package com.platform.marketplace.dto;

import lombok.Data;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private Double price;
    private Integer stockQuantity;
    private String categoryId;
    private String imageUrl;
    private String brand;
}
