package com.platform.guesthouse.dto;

import lombok.Data;

@Data
public class RoomRequest {
    private String guesthouseId;
    private String roomNumber;
    private String type;
    private Double pricePerNight;
    private Integer capacity;
    private String amenities;
}
