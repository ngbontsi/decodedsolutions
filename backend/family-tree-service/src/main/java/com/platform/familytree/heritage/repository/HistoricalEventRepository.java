package com.platform.familytree.heritage.repository;

import com.platform.familytree.heritage.model.HistoricalEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HistoricalEventRepository extends JpaRepository<HistoricalEvent, UUID> {

    List<HistoricalEvent> findByArchiveIdOrderByEventDateAsc(UUID archiveId);
}
