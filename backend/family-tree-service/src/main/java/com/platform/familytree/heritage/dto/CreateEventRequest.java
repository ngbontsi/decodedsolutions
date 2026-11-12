package com.platform.familytree.heritage.dto;

import com.platform.familytree.heritage.model.HistoricalEvent;

import java.time.LocalDate;
import java.util.UUID;

public record CreateEventRequest(String title,
                                 String description,
                                 LocalDate eventDate,
                                 HistoricalEvent.Precision datePrecision,
                                 UUID locationId,
                                 HistoricalEvent.Type eventType,
                                 UUID actorId) {
}