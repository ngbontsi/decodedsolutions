package com.platform.guesthouse.service;

import com.platform.guesthouse.dto.ReservationRequest;
import com.platform.guesthouse.model.Reservation;
import com.platform.guesthouse.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomService roomService;

    public Reservation create(ReservationRequest request) {
        Reservation reservation = Reservation.builder()
                .roomId(request.getRoomId())
                .userId(request.getUserId())
                .checkInDate(LocalDate.parse(request.getCheckInDate()))
                .checkOutDate(LocalDate.parse(request.getCheckOutDate()))
                .numberOfGuests(request.getNumberOfGuests())
                .build();

        var room = roomService.getById(request.getRoomId());
        long nights = java.time.temporal.ChronoUnit.DAYS.between(
            reservation.getCheckInDate(), reservation.getCheckOutDate());
        reservation.setTotalPrice(room.getPricePerNight().doubleValue() * nights);

        return reservationRepository.save(reservation);
    }

    public List<Reservation> getByUser(String userId) {
        return reservationRepository.findByUserId(userId);
    }

    public Reservation getById(String id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
    }
}
