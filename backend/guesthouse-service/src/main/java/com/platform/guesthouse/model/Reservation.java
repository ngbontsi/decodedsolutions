package com.platform.guesthouse.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "reservations")

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String roomId;

    @Column(nullable = false)
    private String userId;

    @Column(name = "store_id")
    private String storeId;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private int numberOfGuests;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private double totalPrice;

    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return  true;
        if(obj == null || getClass() != obj.getClass()) return false;
        Reservation reservation = (Reservation) obj;
        return id!=null && Objects.equals(reservation.id,id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = ReservationStatus.PENDING;
        }
    }
}
