package com.platform.restaurant.controller;

import com.platform.restaurant.dto.MenuItemRequest;
import com.platform.restaurant.model.MenuItem;
import com.platform.restaurant.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PostMapping
    public ResponseEntity<MenuItem> create(@RequestBody MenuItemRequest request) {
        return ResponseEntity.ok(menuService.create(request));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuItem>> getByRestaurant(@PathVariable String restaurantId) {
        return ResponseEntity.ok(menuService.getByRestaurant(restaurantId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getById(@PathVariable String id) {
        return ResponseEntity.ok(menuService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> update(@PathVariable String id, @RequestBody MenuItemRequest request) {
        return ResponseEntity.ok(menuService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        menuService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
