package com.platform.familytree.heritage.repository;

import com.platform.familytree.heritage.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LocationRepository extends JpaRepository<Location, UUID> {

    List<Location> findByArchiveIdOrderByNameAsc(UUID archiveId);
}
