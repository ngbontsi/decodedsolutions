package com.platform.marketplace.dto;

import lombok.Data;

@Data
public class OrderRequest {
    private String userId;
    private String shippingAddress;
}
