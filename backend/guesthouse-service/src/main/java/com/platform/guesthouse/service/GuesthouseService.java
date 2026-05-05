package com.platform.guesthouse.service;

import com.platform.guesthouse.dto.GuesthouseRequest;
import com.platform.guesthouse.model.Guesthouse;
import com.platform.guesthouse.repository.GuesthouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuesthouseService {

    private final GuesthouseRepository guesthouseRepository;

    public Guesthouse create(GuesthouseRequest request) {
        Guesthouse guesthouse = Guesthouse.builder()
                .name(request.getName())
                .description(request.getDescription())
                .address(request.getAddress())
                .phone(request.getPhone())
                .imageUrl(request.getImageUrl())
                .amenities(request.getAmenities())
                .build();
        return guesthouseRepository.save(guesthouse);
    }

    public List<Guesthouse> getAll() {
        return guesthouseRepository.findAll();
    }

    public List<Guesthouse> getActive() {
        return guesthouseRepository.findByActiveTrue();
    }

    public Guesthouse getById(String id) {
        return guesthouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guesthouse not found"));
    }

    public Guesthouse update(String id, GuesthouseRequest request) {
        Guesthouse guesthouse = getById(id);
        guesthouse.setName(request.getName());
        guesthouse.setDescription(request.getDescription());
        guesthouse.setAddress(request.getAddress());
        guesthouse.setPhone(request.getPhone());
        guesthouse.setImageUrl(request.getImageUrl());
        guesthouse.setAmenities(request.getAmenities());
        return guesthouseRepository.save(guesthouse);
    }

    public void delete(String id) {
        Guesthouse guesthouse = getById(id);
        guesthouse.setActive(false);
        guesthouseRepository.save(guesthouse);
    }
}
