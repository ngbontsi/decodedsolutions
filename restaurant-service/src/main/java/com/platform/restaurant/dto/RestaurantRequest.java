package com.platform.restaurant.dto;

import lombok.Data;

@Data
public class RestaurantRequest {
    private String name;
    private String description;
    private String address;
    private String phone;
    private String imageUrl;
}
