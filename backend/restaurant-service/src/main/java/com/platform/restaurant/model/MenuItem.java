package com.platform.restaurant.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "menu_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String restaurantId;

    @Column(name = "owner_id")
    private String ownerId;

    @Column(name = "store_id")
    private String storeId;

    @Column(nullable = false)
    private String name;

    private String description;

    private BigDecimal price;

    private String category;

    private String imageUrl;

    private boolean available;

    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuItem menuItem = (MenuItem) o;
        return id != null && Objects.equals(id, menuItem.id);
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
