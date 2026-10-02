package com.platform.guesthouse.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String guesthouseId;

    @Column(name = "owner_id")
    private String ownerId;

    @Column(name = "store_id")
    private String storeId;

    @Column(nullable = false)
    private String roomNumber;

    private String type;

    private BigDecimal pricePerNight;

    private int capacity;

    private String amenities;

    private boolean available;

    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if (obj==null || getClass() != obj.getClass()) return false;
        Room room = (Room) obj;
        return id != null && Objects.equals(room.id,id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (!available) {
            available = true;
        }
    }
}
