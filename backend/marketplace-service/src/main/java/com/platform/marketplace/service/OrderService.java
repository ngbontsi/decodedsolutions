package com.platform.marketplace.service;

import com.platform.marketplace.dto.OrderRequest;
import com.platform.marketplace.model.*;
import com.platform.marketplace.repository.CartItemRepository;
import com.platform.marketplace.repository.CartRepository;
import com.platform.marketplace.repository.OrderItemRepository;
import com.platform.marketplace.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;

    @Transactional
    public Order createOrder(OrderRequest request) {
        Cart cart = cartRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = Order.builder()
                .userId(request.getUserId())
                .shippingAddress(request.getShippingAddress())
                .status("PENDING")
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Product product = productService.getById(cartItem.getProductId());
            
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = OrderItem.builder()
                    .orderId(order.getId())
                    .productId(product.getId())
                    .productName(product.getName())
                    .quantity(cartItem.getQuantity())
                    .price(product.getPrice())
                    .subtotal(subtotal)
                    .build();
            
            orderItemRepository.save(orderItem);
            productService.updateStock(product.getId(), cartItem.getQuantity());
        }

        order.setTotalAmount(totalAmount);
        order = orderRepository.save(order);

        cartItemRepository.deleteByCartId(cart.getId());

        return order;
    }

    public List<Order> getUserOrders(String userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order getById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<OrderItem> getOrderItems(String orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public Order updateStatus(String orderId, String status) {
        Order order = getById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
