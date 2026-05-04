package com.platform.restaurant.dto;

import lombok.Data;

@Data
public class MenuItemRequest {
    private String restaurantId;
    private String name;
    private String description;
    private Double price;
    private String category;
    private String imageUrl;
}
