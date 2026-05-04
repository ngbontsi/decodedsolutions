package com.platform.guesthouse.service;

import com.platform.guesthouse.dto.RoomRequest;
import com.platform.guesthouse.model.Room;
import com.platform.guesthouse.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public Room create(RoomRequest request) {
        Room room = Room.builder()
                .guesthouseId(request.getGuesthouseId())
                .roomNumber(request.getRoomNumber())
                .type(request.getType())
                .pricePerNight(request.getPricePerNight() != null ? 
                    java.math.BigDecimal.valueOf(request.getPricePerNight()) : null)
                .capacity(request.getCapacity())
                .amenities(request.getAmenities())
                .build();
        return roomRepository.save(room);
    }

    public List<Room> getByGuesthouse(String guesthouseId) {
        return roomRepository.findByGuesthouseId(guesthouseId);
    }

    public Room getById(String id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    public Room update(String id, RoomRequest request) {
        Room room = getById(id);
        room.setRoomNumber(request.getRoomNumber());
        room.setType(request.getType());
        room.setPricePerNight(request.getPricePerNight() != null ? 
            java.math.BigDecimal.valueOf(request.getPricePerNight()) : null);
        room.setCapacity(request.getCapacity());
        room.setAmenities(request.getAmenities());
        return roomRepository.save(room);
    }

    public void delete(String id) {
        Room room = getById(id);
        room.setAvailable(false);
        roomRepository.save(room);
    }
}
