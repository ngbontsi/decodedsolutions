package com.platform.guesthouse.service;

import com.platform.guesthouse.dto.ReservationRequest;
import com.platform.guesthouse.dto.ReservationResponse;
import com.platform.guesthouse.exception.ResourceNotFoundException;
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

    public ReservationResponse create(ReservationRequest request, String userId) {
        Reservation reservation = Reservation.builder()
                .roomId(request.roomId())
                .userId(userId)
                .checkInDate(LocalDate.parse(request.checkInDate()))
                .checkOutDate(LocalDate.parse(request.checkOutDate()))
                .numberOfGuests(request.numberOfGuests())
                .build();

        var room = roomService.getByIdEntity(request.roomId());
        long nights = java.time.temporal.ChronoUnit.DAYS.between(
            reservation.getCheckInDate(), reservation.getCheckOutDate());
        reservation.setTotalPrice(room.getPricePerNight().doubleValue() * nights);

        return toResponse(reservationRepository.save(reservation));
    }

    public List<ReservationResponse> getByUser(String userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    public ReservationResponse getById(String id, String userId, String userRole) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        if (!isAdmin(userRole) && !userId.equals(reservation.getUserId())) {
            throw new ResourceNotFoundException("Reservation not found");
        }
        return toResponse(reservation);
    }

    public ReservationResponse cancel(String id, String userId, String userRole) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        if (!isAdmin(userRole) && !userId.equals(reservation.getUserId())) {
            throw new ResourceNotFoundException("Reservation not found");
        }
        if (reservation.getStatus() == com.platform.guesthouse.model.ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Reservation is already cancelled");
        }
        reservation.setStatus(com.platform.guesthouse.model.ReservationStatus.CANCELLED);
        return toResponse(reservationRepository.save(reservation));
    }

    private boolean isAdmin(String userRole) {
        return "ADMIN".equals(userRole);
    }

    private ReservationResponse toResponse(Reservation r) {
        return new ReservationResponse(
                r.getId(), r.getRoomId(), r.getUserId(),
                r.getCheckInDate(), r.getCheckOutDate(), r.getNumberOfGuests(),
                r.getStatus(), r.getTotalPrice(), r.getCreatedAt()
        );
    }
}
