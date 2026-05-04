package com.platform.guesthouse.controller;

import com.platform.guesthouse.dto.ReservationRequest;
import com.platform.guesthouse.model.Reservation;
import com.platform.guesthouse.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<Reservation> create(@RequestBody ReservationRequest request) {
        return ResponseEntity.ok(reservationService.create(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reservation>> getByUser(@PathVariable String userId) {
        return ResponseEntity.ok(reservationService.getByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getById(@PathVariable String id) {
        return ResponseEntity.ok(reservationService.getById(id));
    }
}
