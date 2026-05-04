package com.platform.restaurant.service;

import com.platform.restaurant.dto.MenuItemRequest;
import com.platform.restaurant.model.MenuItem;
import com.platform.restaurant.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuItemRepository menuItemRepository;

    public MenuItem create(MenuItemRequest request) {
        MenuItem item = MenuItem.builder()
                .restaurantId(request.getRestaurantId())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice() != null ? java.math.BigDecimal.valueOf(request.getPrice()) : null)
                .category(request.getCategory())
                .imageUrl(request.getImageUrl())
                .build();
        return menuItemRepository.save(item);
    }

    public List<MenuItem> getByRestaurant(String restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId);
    }

    public MenuItem getById(String id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
    }

    public MenuItem update(String id, MenuItemRequest request) {
        MenuItem item = getById(id);
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice() != null ? java.math.BigDecimal.valueOf(request.getPrice()) : null);
        item.setCategory(request.getCategory());
        item.setImageUrl(request.getImageUrl());
        return menuItemRepository.save(item);
    }

    public void delete(String id) {
        MenuItem item = getById(id);
        item.setAvailable(false);
        menuItemRepository.save(item);
    }
}
