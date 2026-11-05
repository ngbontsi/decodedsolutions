package com.platform.restaurant.controller;

import com.platform.restaurant.dto.MenuItemRequest;
import com.platform.restaurant.dto.MenuItemResponse;
import com.platform.restaurant.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
@Tag(name ="Menu", description ="The menu endponts")
public class MenuController {

    private final MenuService menuService;

    @PostMapping
    @Operation(summary = "Create a new menu", description = "Creates a new menu with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Menu created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MenuItemResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content)
    })
    public ResponseEntity<MenuItemResponse> create(
            @Valid @RequestBody MenuItemRequest request,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(menuService.create(request, userId));
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Get all menus by restaurant", description = "Retrieves a list of all menus by restaurantId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved menus",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = MenuItemResponse.class))))
    })
    public ResponseEntity<List<MenuItemResponse>> getByRestaurant(@PathVariable String restaurantId) {
        return ResponseEntity.ok(menuService.getByRestaurant(restaurantId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get menu by ID", description = "Retrieves a single menu by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Menu found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MenuItemResponse.class))),
            @ApiResponse(responseCode = "404", description = "Menu not found",
                    content = @Content)
    })
    public ResponseEntity<MenuItemResponse> getById(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        return ResponseEntity.ok(menuService.getById(id, userId, userRole));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a Menu", description = "Updates an existing menu by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Menu updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MenuItemResponse.class))),
            @ApiResponse(responseCode = "404", description = "Menu not found",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content)
    })
    public ResponseEntity<MenuItemResponse> update(
            @PathVariable String id,
            @Valid @RequestBody MenuItemRequest request,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole) {
        return ResponseEntity.ok(menuService.update(id, request, userId, userRole));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a menu", description = "Deletes a menu by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Menu deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Menu not found")
    })
    public ResponseEntity<Void> delete(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole) {
        menuService.delete(id, userId, userRole);
        return ResponseEntity.noContent().build();
    }
}
