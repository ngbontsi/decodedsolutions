package com.platform.marketplace.dto;

import lombok.Data;

@Data
public class CartItemRequest {
    private String userId;
    private String productId;
    private Integer quantity;
}
