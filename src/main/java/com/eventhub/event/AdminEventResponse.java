package com.eventhub.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AdminEventResponse(Long id, String title, String description, String venue, LocalDateTime startsAt,
                                 Integer capacity, Integer numberOfDays, BigDecimal dailyPay, int registeredCount,
                                 int spotsLeft, List<Registration> registrations) {
    static AdminEventResponse from(Event event) {
        int registered = event.getRegistrations().size();
        return new AdminEventResponse(event.getId(), event.getTitle(), event.getDescription(), event.getVenue(),
                event.getStartsAt(), event.getCapacity(), event.getNumberOfDays(), event.getDailyPay(), registered,
                event.getCapacity() - registered, List.copyOf(event.getRegistrations()));
    }
}
