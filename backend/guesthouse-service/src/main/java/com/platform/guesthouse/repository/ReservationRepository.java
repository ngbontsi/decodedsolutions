package com.platform.guesthouse.repository;

import com.platform.guesthouse.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {
    List<Reservation> findByUserId(String userId);
    List<Reservation> findByRoomId(String roomId);
}
