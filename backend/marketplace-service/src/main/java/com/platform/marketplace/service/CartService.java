package com.platform.marketplace.service;

import com.platform.marketplace.dto.CartItemDetail;
import com.platform.marketplace.dto.CartItemRequest;
import com.platform.marketplace.dto.CartResponse;
import com.platform.marketplace.model.Cart;
import com.platform.marketplace.model.CartItem;
import com.platform.marketplace.model.Product;
import com.platform.marketplace.repository.CartItemRepository;
import com.platform.marketplace.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;

    public CartResponse getCart(String userId) {
        Cart cart = getOrCreateCart(userId);
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        
        List<CartItemDetail> itemDetails = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : items) {
            Product product = productService.getById(item.getProductId());
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            
            itemDetails.add(CartItemDetail.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .quantity(item.getQuantity())
                    .price(product.getPrice())
                    .subtotal(subtotal)
                    .build());
            
            total = total.add(subtotal);
        }

        return CartResponse.builder()
                .id(cart.getId())
                .userId(userId)
                .items(itemDetails)
                .totalPrice(total)
                .build();
    }

    public CartResponse addToCart(CartItemRequest request) {
        Cart cart = getOrCreateCart(request.getUserId());
        
        CartItem existingItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), request.getProductId())
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .cartId(cart.getId())
                    .productId(request.getProductId())
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(newItem);
        }

        return getCart(request.getUserId());
    }

    public CartResponse updateCartItem(String userId, String productId, int quantity) {
        Cart cart = getOrCreateCart(userId);
        
        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        return getCart(userId);
    }

    public void clearCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cartItemRepository.deleteByCartId(cart.getId());
    }

    private Cart getOrCreateCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder().userId(userId).build();
                    return cartRepository.save(newCart);
                });
    }
}
