package com.platform.restaurant.controller;

import com.platform.restaurant.dto.RestaurantRequest;
import com.platform.restaurant.dto.RestaurantResponse;
import com.platform.restaurant.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurants", description = "Restaurant management endpoints")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    @Operation(summary = "Create a new restaurant", description = "Creates a new restaurant with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurant created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestaurantResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content)
    })
    public ResponseEntity<RestaurantResponse> create(
            @Valid @RequestBody RestaurantRequest request,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(restaurantService.create(request, userId));
    }

    @GetMapping
    @Operation(summary = "Get all restaurants", description = "Retrieves all restaurants. Admins see all; owners see only their own.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved restaurants",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestaurantResponse.class)))
    })
    public ResponseEntity<List<RestaurantResponse>> getAll(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        return ResponseEntity.ok(restaurantService.getAll(userId, userRole));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active restaurants", description = "Retrieves all active restaurants")
    public ResponseEntity<List<RestaurantResponse>> getActive(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        return ResponseEntity.ok(restaurantService.getActive(userId, userRole));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get restaurant by ID", description = "Retrieves a single restaurant by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurant found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestaurantResponse.class))),
            @ApiResponse(responseCode = "404", description = "Restaurant not found",
                    content = @Content)
    })
    public ResponseEntity<RestaurantResponse> getById(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        return ResponseEntity.ok(restaurantService.getById(id, userId, userRole));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a restaurant", description = "Updates an existing restaurant by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurant updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestaurantResponse.class))),
            @ApiResponse(responseCode = "404", description = "Restaurant not found",
                    content = @Content)
    })
    public ResponseEntity<RestaurantResponse> update(
            @PathVariable String id,
            @Valid @RequestBody RestaurantRequest request,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole) {
        return ResponseEntity.ok(restaurantService.update(id, request, userId, userRole));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a restaurant", description = "Deletes a restaurant by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Restaurant deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    public ResponseEntity<Void> delete(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole) {
        restaurantService.delete(id, userId, userRole);
        return ResponseEntity.noContent().build();
    }

}
