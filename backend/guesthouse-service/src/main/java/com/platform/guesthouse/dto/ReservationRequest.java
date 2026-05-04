package com.platform.guesthouse.dto;

import lombok.Data;

@Data
public class ReservationRequest {
    private String roomId;
    private String userId;
    private String checkInDate;
    private String checkOutDate;
    private Integer numberOfGuests;
}
