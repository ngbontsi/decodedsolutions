package com.platform.guesthouse.dto;

import lombok.Data;

@Data
public class GuesthouseRequest {
    private String name;
    private String description;
    private String address;
    private String phone;
    private String imageUrl;
    private String amenities;
}
