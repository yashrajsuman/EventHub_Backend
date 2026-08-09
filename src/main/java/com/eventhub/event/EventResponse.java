package com.eventhub.event;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public record EventResponse(Long id, String title, String description, String venue, LocalDateTime startsAt,
                            Integer capacity, Integer numberOfDays, BigDecimal dailyPay, int registeredCount, int spotsLeft) {
    static EventResponse from(Event event) {
        int registered = event.getRegistrations().size();
        return new EventResponse(event.getId(), event.getTitle(), event.getDescription(), event.getVenue(),
                event.getStartsAt(), event.getCapacity(), event.getNumberOfDays(), event.getDailyPay(), registered, event.getCapacity() - registered);
    }
}
