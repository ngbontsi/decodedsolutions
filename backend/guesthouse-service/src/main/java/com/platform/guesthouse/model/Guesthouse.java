package com.platform.guesthouse.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "guesthouses")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Guesthouse {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, name = "owner_id")
    private String ownerId;

    @Column(nullable = false)
    private String name;

    private String description;

    private String address;

    private String phone;

    private String imageUrl;

    private String amenities;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if (obj == null || getClass() != obj.getClass())return false;
        Guesthouse guesthouse = (Guesthouse) obj;
        return id != null && Objects.equals(guesthouse.id,id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (!active) {
            active = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
