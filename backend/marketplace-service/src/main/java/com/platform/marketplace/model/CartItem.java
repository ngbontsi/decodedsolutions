package com.platform.marketplace.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String cartId;

    @Column(nullable = false)
    private String productId;

    private int quantity;

    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj==null || getClass() != obj.getClass()) return false;
        CartItem cartItem = (CartItem) obj;
        return id !=null && Objects.equals(cartItem.id,id);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
