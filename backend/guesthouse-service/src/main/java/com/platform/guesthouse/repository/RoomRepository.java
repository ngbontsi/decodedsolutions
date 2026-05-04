package com.platform.guesthouse.repository;

import com.platform.guesthouse.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {
    List<Room> findByGuesthouseId(String guesthouseId);
    List<Room> findByGuesthouseIdAndAvailableTrue(String guesthouseId);
}
