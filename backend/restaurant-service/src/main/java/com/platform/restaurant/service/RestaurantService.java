package com.platform.restaurant.service;

import com.platform.restaurant.dto.RestaurantRequest;
import com.platform.restaurant.model.Restaurant;
import com.platform.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public Restaurant create(RestaurantRequest request) {
        Restaurant restaurant = Restaurant.builder()
                .name(request.getName())
                .description(request.getDescription())
                .address(request.getAddress())
                .phone(request.getPhone())
                .imageUrl(request.getImageUrl())
                .build();
        return restaurantRepository.save(restaurant);
    }

    public List<Restaurant> getAll() {
        return restaurantRepository.findAll();
    }

    public List<Restaurant> getActive() {
        return restaurantRepository.findByActiveTrue();
    }

    public Restaurant getById(String id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
    }

    public Restaurant update(String id, RestaurantRequest request) {
        Restaurant restaurant = getById(id);
        restaurant.setName(request.getName());
        restaurant.setDescription(request.getDescription());
        restaurant.setAddress(request.getAddress());
        restaurant.setPhone(request.getPhone());
        restaurant.setImageUrl(request.getImageUrl());
        return restaurantRepository.save(restaurant);
    }

    public void delete(String id) {
        Restaurant restaurant = getById(id);
        restaurant.setActive(false);
        restaurantRepository.save(restaurant);
    }
}
