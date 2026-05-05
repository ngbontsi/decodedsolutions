package com.platform.guesthouse.controller;

import com.platform.guesthouse.dto.GuesthouseRequest;
import com.platform.guesthouse.model.Guesthouse;
import com.platform.guesthouse.service.GuesthouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/guesthouses")
@RequiredArgsConstructor
public class GuesthouseController {

    private final GuesthouseService guesthouseService;

    @PostMapping
    public ResponseEntity<Guesthouse> create(@RequestBody GuesthouseRequest request) {
        return ResponseEntity.ok(guesthouseService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<Guesthouse>> getAll() {
        return ResponseEntity.ok(guesthouseService.getAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Guesthouse>> getActive() {
        return ResponseEntity.ok(guesthouseService.getActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Guesthouse> getById(@PathVariable String id) {
        return ResponseEntity.ok(guesthouseService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Guesthouse> update(@PathVariable String id, @RequestBody GuesthouseRequest request) {
        return ResponseEntity.ok(guesthouseService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        guesthouseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
