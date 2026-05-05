package com.platform.restaurant.controller;

import com.platform.restaurant.dto.RestaurantRequest;
import com.platform.restaurant.model.Restaurant;
import com.platform.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<Restaurant> create(@RequestBody RestaurantRequest request) {
        return ResponseEntity.ok(restaurantService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<Restaurant>> getAll() {
        return ResponseEntity.ok(restaurantService.getAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Restaurant>> getActive() {
        return ResponseEntity.ok(restaurantService.getActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getById(@PathVariable String id) {
        return ResponseEntity.ok(restaurantService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> update(@PathVariable String id, @RequestBody RestaurantRequest request) {
        return ResponseEntity.ok(restaurantService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        restaurantService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
